package cl.duoc.gestionguias.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponseDTO {

    private LocalDateTime fecha;
    private int estado;
    private String error;
    private List<String> detalles;

    public ErrorResponseDTO() {
    }

    public ErrorResponseDTO(LocalDateTime fecha, int estado, String error, List<String> detalles) {
        this.fecha = fecha;
        this.estado = estado;
        this.error = error;
        this.detalles = detalles;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<String> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<String> detalles) {
        this.detalles = detalles;
    }
}
