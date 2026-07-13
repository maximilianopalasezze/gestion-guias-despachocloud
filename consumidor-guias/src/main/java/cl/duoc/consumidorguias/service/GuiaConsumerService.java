package cl.duoc.consumidorguias.service;

import cl.duoc.consumidorguias.dto.GuiaErrorMensajeDTO;
import cl.duoc.consumidorguias.dto.GuiaMensajeDTO;
import cl.duoc.consumidorguias.model.GuiaProcesada;
import cl.duoc.consumidorguias.repository.GuiaProcesadaRepository;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class GuiaConsumerService {

    private final RabbitTemplate rabbitTemplate;
    private final GuiaProcesadaRepository guiaProcesadaRepository;
    private final PdfGeneratorService pdfGeneratorService;
    private final S3StorageService s3StorageService;

    private final String guiasQueue;
    private final String guiasErrorExchange;
    private final String guiasErrorRoutingKey;

    public GuiaConsumerService(
            RabbitTemplate rabbitTemplate,
            GuiaProcesadaRepository guiaProcesadaRepository,
            PdfGeneratorService pdfGeneratorService,
            S3StorageService s3StorageService,
            @Value("${app.rabbitmq.guias.queue}") String guiasQueue,
            @Value("${app.rabbitmq.guias.error-exchange}") String guiasErrorExchange,
            @Value("${app.rabbitmq.guias.error-routing-key}") String guiasErrorRoutingKey
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.guiaProcesadaRepository = guiaProcesadaRepository;
        this.pdfGeneratorService = pdfGeneratorService;
        this.s3StorageService = s3StorageService;
        this.guiasQueue = guiasQueue;
        this.guiasErrorExchange = guiasErrorExchange;
        this.guiasErrorRoutingKey = guiasErrorRoutingKey;
    }

    @Transactional
    public GuiaProcesada consumirYGuardarGuia() {

        Object mensajeRecibido = rabbitTemplate.receiveAndConvert(guiasQueue);

        if (mensajeRecibido == null) {
            return null;
        }

        try {
            if (!(mensajeRecibido instanceof GuiaMensajeDTO mensaje)) {
                throw new IllegalStateException(
                        "El mensaje recibido no corresponde al formato esperado de una guía de despacho"
                );
            }

            if (mensaje.getCorrelationId() != null
                    && guiaProcesadaRepository.existsByCorrelationId(mensaje.getCorrelationId())) {
                throw new IllegalStateException(
                        "La guía ya fue procesada anteriormente. Correlation ID duplicado: "
                                + mensaje.getCorrelationId()
                );
            }

            if (mensaje.getDescripcionPedido() != null
                    && mensaje.getDescripcionPedido().toUpperCase().contains("FORZAR_ERROR_DLQ")) {
                throw new IllegalStateException(
                        "Error controlado para demostrar funcionamiento de la DLQ en RabbitMQ"
                );
            }

            var rutaPdf = pdfGeneratorService.generarPdfDesdeMensaje(mensaje);

            String rutaS3 = s3StorageService.subirGuiaSiEstaHabilitado(
                    rutaPdf,
                    mensaje.getTransportista(),
                    mensaje.getFechaGeneracion(),
                    mensaje.getNombreArchivo()
            );

            GuiaProcesada guiaProcesada = new GuiaProcesada(
                    mensaje.getIdGuia(),
                    mensaje.getNumeroGuia(),
                    mensaje.getCorrelationId(),
                    mensaje.getTransportista(),
                    mensaje.getDestinatario(),
                    mensaje.getDireccionDestino(),
                    mensaje.getDescripcionPedido(),
                    mensaje.getPesoKg(),
                    mensaje.getFechaGeneracion(),
                    mensaje.getFechaDespacho(),
                    mensaje.getNombreArchivo(),
                    rutaS3,
                    mensaje.getFechaMensaje(),
                    "PROCESADA_DESDE_COLA"
            );

            return guiaProcesadaRepository.save(guiaProcesada);

        } catch (Exception ex) {
            enviarErrorADlq(mensajeRecibido, ex);

            throw new IllegalStateException(
                    ex.getMessage(),
                    ex
            );
        }
    }

    private void enviarErrorADlq(Object mensajeRecibido, Exception ex) {
        GuiaErrorMensajeDTO mensajeError;

        if (mensajeRecibido instanceof GuiaMensajeDTO mensaje) {
            mensajeError = new GuiaErrorMensajeDTO(
                    mensaje.getIdGuia(),
                    mensaje.getNumeroGuia(),
                    mensaje.getCorrelationId(),
                    ex.getMessage(),
                    "CONSUMIDOR_GUIAS",
                    LocalDateTime.now()
            );
        } else {
            mensajeError = new GuiaErrorMensajeDTO(
                    null,
                    "SIN_NUMERO_GUIA",
                    null,
                    ex.getMessage(),
                    "CONSUMIDOR_GUIAS_FORMATO_INVALIDO",
                    LocalDateTime.now()
            );
        }

        rabbitTemplate.convertAndSend(
                guiasErrorExchange,
                guiasErrorRoutingKey,
                mensajeError,
                message -> {
                    message.getMessageProperties()
                            .setDeliveryMode(MessageDeliveryMode.PERSISTENT);

                    if (mensajeError.getCorrelationId() != null) {
                        message.getMessageProperties()
                                .setCorrelationId(mensajeError.getCorrelationId());
                    }

                    return message;
                }
        );
    }
}