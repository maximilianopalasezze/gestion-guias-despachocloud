package cl.duoc.consumidorguias.controller;

import cl.duoc.consumidorguias.model.GuiaProcesada;
import cl.duoc.consumidorguias.repository.GuiaProcesadaRepository;
import cl.duoc.consumidorguias.service.GuiaConsumerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/consumidor/guias")
public class ConsumidorGuiaController {

    private final GuiaConsumerService guiaConsumerService;
    private final GuiaProcesadaRepository guiaProcesadaRepository;

    public ConsumidorGuiaController(
            GuiaConsumerService guiaConsumerService,
            GuiaProcesadaRepository guiaProcesadaRepository
    ) {
        this.guiaConsumerService = guiaConsumerService;
        this.guiaProcesadaRepository = guiaProcesadaRepository;
    }

    @PostMapping("/consumir")
    public ResponseEntity<?> consumirGuiaDesdeCola() {
        try {
            GuiaProcesada guiaProcesada = guiaConsumerService.consumirYGuardarGuia();

            if (guiaProcesada == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "mensaje", "No existen mensajes pendientes en la cola principal",
                        "cola", "guias.principal.queue"
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Guía consumida correctamente desde RabbitMQ y guardada en la base de datos",
                    "idRegistro", guiaProcesada.getId(),
                    "idGuiaOriginal", guiaProcesada.getIdGuiaOriginal(),
                    "numeroGuia", guiaProcesada.getNumeroGuia(),
                    "transportista", guiaProcesada.getTransportista(),
                    "estadoProcesamiento", guiaProcesada.getEstadoProcesamiento(),
                    "correlationId", guiaProcesada.getCorrelationId()
            ));

        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "No se pudo consumir la guía desde RabbitMQ",
                    "detalle", ex.getMessage(),
                    "colaErrores", "guias.dlq.queue"
            ));
        }
    }

    @GetMapping("/procesadas")
    public ResponseEntity<?> listarGuiasProcesadas() {
        return ResponseEntity.ok(guiaProcesadaRepository.findAll());
    }
}