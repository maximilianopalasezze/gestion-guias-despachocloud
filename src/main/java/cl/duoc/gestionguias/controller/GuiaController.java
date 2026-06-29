package cl.duoc.gestionguias.controller;

import cl.duoc.gestionguias.dto.GuiaRequestDTO;
import cl.duoc.gestionguias.dto.GuiaResponseDTO;
import cl.duoc.gestionguias.dto.GuiaUpdateDTO;
import cl.duoc.gestionguias.dto.MensajeResponseDTO;
import cl.duoc.gestionguias.service.GuiaService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guias")
public class GuiaController {

    private final GuiaService guiaService;

    public GuiaController(GuiaService guiaService) {
        this.guiaService = guiaService;
    }

    @PostMapping
    public ResponseEntity<GuiaResponseDTO> crearGuia(
            @Valid @RequestBody GuiaRequestDTO request
    ) {
        GuiaResponseDTO response = guiaService.crearGuia(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/subir-s3")
    public ResponseEntity<GuiaResponseDTO> subirGuiaAS3(
            @PathVariable Long id
    ) {
        GuiaResponseDTO response = guiaService.subirGuiaAS3(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/descargar")
    public ResponseEntity<byte[]> descargarGuia(@PathVariable Long id) {
        GuiaResponseDTO guia = guiaService.obtenerGuia(id);
        byte[] archivo = guiaService.descargarGuia(id);

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(guia.getNombreArchivo())
                .build();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        contentDisposition.toString()
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(archivo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuiaResponseDTO> actualizarGuia(
            @PathVariable Long id,
            @Valid @RequestBody GuiaUpdateDTO request
    ) {
        GuiaResponseDTO response = guiaService.actualizarGuia(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MensajeResponseDTO> eliminarGuia(
            @PathVariable Long id
    ) {
        guiaService.eliminarGuia(id);

        return ResponseEntity.ok(
                new MensajeResponseDTO(
                        "Guia eliminada correctamente desde EFS, S3 e historial"
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<GuiaResponseDTO>> consultarGuias(
            @RequestParam(required = false) String transportista,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha
    ) {
        List<GuiaResponseDTO> response = guiaService.consultarGuias(
                transportista,
                fecha
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuiaResponseDTO> obtenerGuia(
            @PathVariable Long id
    ) {
        GuiaResponseDTO response = guiaService.obtenerGuia(id);
        return ResponseEntity.ok(response);
    }
}