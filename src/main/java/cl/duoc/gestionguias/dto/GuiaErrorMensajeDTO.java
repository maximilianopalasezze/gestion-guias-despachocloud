package cl.duoc.gestionguias.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class GuiaErrorMensajeDTO implements Serializable {

    private Long idGuia;
    private String numeroGuia;
    private String correlationId;
    private String mensajeError;
    private String origenError;
    private LocalDateTime fechaError;

    public GuiaErrorMensajeDTO() {
    }

    public GuiaErrorMensajeDTO(
            Long idGuia,
            String numeroGuia,
            String correlationId,
            String mensajeError,
            String origenError,
            LocalDateTime fechaError
    ) {
        this.idGuia = idGuia;
        this.numeroGuia = numeroGuia;
        this.correlationId = correlationId;
        this.mensajeError = mensajeError;
        this.origenError = origenError;
        this.fechaError = fechaError;
    }

    public Long getIdGuia() {
        return idGuia;
    }

    public void setIdGuia(Long idGuia) {
        this.idGuia = idGuia;
    }

    public String getNumeroGuia() {
        return numeroGuia;
    }

    public void setNumeroGuia(String numeroGuia) {
        this.numeroGuia = numeroGuia;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    public String getOrigenError() {
        return origenError;
    }

    public void setOrigenError(String origenError) {
        this.origenError = origenError;
    }

    public LocalDateTime getFechaError() {
        return fechaError;
    }

    public void setFechaError(LocalDateTime fechaError) {
        this.fechaError = fechaError;
    }
}