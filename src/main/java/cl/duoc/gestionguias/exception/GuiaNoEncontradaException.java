package cl.duoc.gestionguias.exception;

public class GuiaNoEncontradaException extends RuntimeException {

    public GuiaNoEncontradaException(Long id) {
        super("No existe una guia de despacho con ID: " + id);
    }
}
