package cl.duoc.gestionguias.service;

import cl.duoc.gestionguias.dto.GuiaResponseDTO;
import cl.duoc.gestionguias.model.Guia;
import org.springframework.stereotype.Component;

@Component
public class GuiaMapper {

    public GuiaResponseDTO convertirAResponseDTO(Guia guia) {
        GuiaResponseDTO dto = new GuiaResponseDTO();
        dto.setId(guia.getId());
        dto.setNumeroGuia(guia.getNumeroGuia());
        dto.setTransportista(guia.getTransportista());
        dto.setDestinatario(guia.getDestinatario());
        dto.setDireccionDestino(guia.getDireccionDestino());
        dto.setDescripcionPedido(guia.getDescripcionPedido());
        dto.setPesoKg(guia.getPesoKg());
        dto.setFechaGeneracion(guia.getFechaGeneracion());
        dto.setFechaDespacho(guia.getFechaDespacho());
        dto.setNombreArchivo(guia.getNombreArchivo());
        dto.setRutaEfs(guia.getRutaEfs());
        dto.setRutaS3(guia.getRutaS3());
        dto.setEstado(guia.getEstado().name());
        dto.setFechaCreacion(guia.getFechaCreacion());
        dto.setFechaActualizacion(guia.getFechaActualizacion());
        return dto;
    }
}
