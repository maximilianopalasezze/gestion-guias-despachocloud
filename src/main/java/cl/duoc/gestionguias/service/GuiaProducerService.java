package cl.duoc.gestionguias.service;

import cl.duoc.gestionguias.dto.GuiaErrorMensajeDTO;
import cl.duoc.gestionguias.dto.GuiaMensajeDTO;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GuiaProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final String guiasExchange;
    private final String guiasErrorExchange;
    private final String guiasRoutingKey;
    private final String guiasErrorRoutingKey;

    public GuiaProducerService(
            RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.guias.exchange}") String guiasExchange,
            @Value("${app.rabbitmq.guias.error-exchange}") String guiasErrorExchange,
            @Value("${app.rabbitmq.guias.routing-key}") String guiasRoutingKey,
            @Value("${app.rabbitmq.guias.error-routing-key}") String guiasErrorRoutingKey
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.guiasExchange = guiasExchange;
        this.guiasErrorExchange = guiasErrorExchange;
        this.guiasRoutingKey = guiasRoutingKey;
        this.guiasErrorRoutingKey = guiasErrorRoutingKey;
    }

    public void enviarGuiaColaPrincipal(GuiaMensajeDTO mensaje) {
        rabbitTemplate.convertAndSend(
                guiasExchange,
                guiasRoutingKey,
                mensaje,
                message -> {
                    message.getMessageProperties()
                            .setDeliveryMode(MessageDeliveryMode.PERSISTENT);

                    if (mensaje.getCorrelationId() != null) {
                        message.getMessageProperties()
                                .setCorrelationId(mensaje.getCorrelationId());
                    }

                    return message;
                }
        );
    }

    public void enviarGuiaColaErrores(GuiaErrorMensajeDTO mensajeError) {
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