package cl.duoc.gestionguias.service;

import cl.duoc.gestionguias.dto.GuiaRequestDTO;
import cl.duoc.gestionguias.dto.GuiaResponseDTO;
import cl.duoc.gestionguias.dto.GuiaUpdateDTO;
import cl.duoc.gestionguias.exception.GuiaNoEncontradaException;
import cl.duoc.gestionguias.model.EstadoGuia;
import cl.duoc.gestionguias.model.Guia;
import cl.duoc.gestionguias.repository.GuiaRepository;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GuiaService {

    private final GuiaRepository guiaRepository;
    private final EfsStorageService efsStorageService;
    private final S3StorageService s3StorageService;
    private final PdfGeneratorService pdfGeneratorService;
    private final GuiaMapper guiaMapper;

    public GuiaService(
            GuiaRepository guiaRepository,
            EfsStorageService efsStorageService,
            S3StorageService s3StorageService,
            PdfGeneratorService pdfGeneratorService,
            GuiaMapper guiaMapper) {
        this.guiaRepository = guiaRepository;
        this.efsStorageService = efsStorageService;
        this.s3StorageService = s3StorageService;
        this.pdfGeneratorService = pdfGeneratorService;
        this.guiaMapper = guiaMapper;
    }

    @Transactional
    public GuiaResponseDTO crearGuia(GuiaRequestDTO request) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDate fechaGeneracion = LocalDate.now();

        Guia guia = new Guia();
        guia.setNumeroGuia("TEMP");
        guia.setTransportista(request.getTransportista().trim());
        guia.setDestinatario(request.getDestinatario().trim());
        guia.setDireccionDestino(request.getDireccionDestino().trim());
        guia.setDescripcionPedido(request.getDescripcionPedido().trim());
        guia.setPesoKg(request.getPesoKg());
        guia.setFechaGeneracion(fechaGeneracion);
        guia.setFechaDespacho(
                request.getFechaDespacho() == null
                        ? fechaGeneracion
                        : request.getFechaDespacho()
        );
        guia.setEstado(EstadoGuia.GENERADA_EFS);
        guia.setFechaCreacion(ahora);
        guia.setFechaActualizacion(ahora);
        guia.setNombreArchivo("temporal.pdf");
        guia.setRutaEfs("pendiente");

        Guia guardada = guiaRepository.save(guia);

        String numeroGuia = "GUIA-" + String.format("%06d", guardada.getId());
        String nombreArchivo = numeroGuia.toLowerCase() + ".pdf";
        Path rutaArchivo = efsStorageService.obtenerRutaArchivo(nombreArchivo);

        guardada.setNumeroGuia(numeroGuia);
        guardada.setNombreArchivo(nombreArchivo);
        guardada.setRutaEfs(rutaArchivo.toString());

        // La guía se genera inicialmente solo en EFS.
        pdfGeneratorService.generarPdf(guardada, rutaArchivo);

        guardada.setFechaActualizacion(LocalDateTime.now());

        return guiaMapper.convertirAResponseDTO(
                guiaRepository.save(guardada)
        );
    }

    @Transactional
    public GuiaResponseDTO subirGuiaAS3(Long id) {
        Guia guia = buscarGuiaPorId(id);
        Path rutaArchivo = Path.of(guia.getRutaEfs());

        String key = guia.getRutaS3();

        if (key == null || key.isBlank()) {
            key = s3StorageService.generarKeyS3(
                    guia.getTransportista(),
                    guia.getFechaGeneracion(),
                    guia.getNombreArchivo()
            );
        }

        String keySubida = s3StorageService.subirArchivo(rutaArchivo, key);

        guia.setRutaS3(keySubida);
        guia.setEstado(EstadoGuia.SUBIDA_S3);
        guia.setFechaActualizacion(LocalDateTime.now());

        return guiaMapper.convertirAResponseDTO(
                guiaRepository.save(guia)
        );
    }

    @Transactional(readOnly = true)
    public byte[] descargarGuia(Long id) {
        Guia guia = buscarGuiaPorId(id);

        if (guia.getRutaS3() != null && !guia.getRutaS3().isBlank()) {
            return s3StorageService.descargarArchivo(guia.getRutaS3());
        }

        return efsStorageService.leerArchivo(guia.getRutaEfs());
    }

    @Transactional
    public GuiaResponseDTO actualizarGuia(Long id, GuiaUpdateDTO request) {
        Guia guia = buscarGuiaPorId(id);

        guia.setTransportista(request.getTransportista().trim());
        guia.setDestinatario(request.getDestinatario().trim());
        guia.setDireccionDestino(request.getDireccionDestino().trim());
        guia.setDescripcionPedido(request.getDescripcionPedido().trim());
        guia.setPesoKg(request.getPesoKg());
        guia.setFechaDespacho(request.getFechaDespacho());
        guia.setFechaActualizacion(LocalDateTime.now());

        Path rutaArchivo = efsStorageService.obtenerRutaArchivo(
                guia.getNombreArchivo()
        );

        guia.setRutaEfs(rutaArchivo.toString());

        // Se genera nuevamente el PDF actualizado en EFS.
        pdfGeneratorService.generarPdf(guia, rutaArchivo);

        // Si la guía ya existía en S3, se sobrescribe con la versión actualizada.
        if (guia.getRutaS3() != null && !guia.getRutaS3().isBlank()) {
            String rutaS3Anterior = guia.getRutaS3();

            String nuevaRutaS3 = s3StorageService.generarKeyS3(
                    guia.getTransportista(),
                    guia.getFechaGeneracion(),
                    guia.getNombreArchivo()
            );

            s3StorageService.subirArchivo(rutaArchivo, nuevaRutaS3);

            // Si cambió el transportista, se elimina el archivo de la carpeta anterior.
            if (!rutaS3Anterior.equals(nuevaRutaS3)) {
                s3StorageService.eliminarArchivo(rutaS3Anterior);
            }

            guia.setRutaS3(nuevaRutaS3);
            guia.setEstado(EstadoGuia.ACTUALIZADA_S3);
        } else {
            // Si nunca fue subida a S3, se mantiene solamente en EFS.
            guia.setEstado(EstadoGuia.GENERADA_EFS);
        }

        return guiaMapper.convertirAResponseDTO(
                guiaRepository.save(guia)
        );
    }

    @Transactional
    public void eliminarGuia(Long id) {
        Guia guia = buscarGuiaPorId(id);

        if (guia.getRutaS3() != null && !guia.getRutaS3().isBlank()) {
            s3StorageService.eliminarArchivo(guia.getRutaS3());
        }

        if (guia.getRutaEfs() != null && !guia.getRutaEfs().isBlank()) {
            efsStorageService.eliminarArchivo(guia.getRutaEfs());
        }

        guiaRepository.delete(guia);
    }

    @Transactional(readOnly = true)
    public List<GuiaResponseDTO> consultarGuias(
            String transportista,
            LocalDate fecha
    ) {
        List<Guia> guias;

        boolean tieneTransportista = transportista != null
                && !transportista.isBlank();
        boolean tieneFecha = fecha != null;

        if (tieneTransportista && tieneFecha) {
            guias = guiaRepository
                    .findByTransportistaIgnoreCaseAndFechaGeneracion(
                            transportista.trim(),
                            fecha
                    );
        } else if (tieneTransportista) {
            guias = guiaRepository
                    .findByTransportistaIgnoreCase(transportista.trim());
        } else if (tieneFecha) {
            guias = guiaRepository.findByFechaGeneracion(fecha);
        } else {
            guias = guiaRepository.findAll();
        }

        return guias.stream()
                .map(guiaMapper::convertirAResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public GuiaResponseDTO obtenerGuia(Long id) {
        return guiaMapper.convertirAResponseDTO(
                buscarGuiaPorId(id)
        );
    }

    private Guia buscarGuiaPorId(Long id) {
        return guiaRepository.findById(id)
                .orElseThrow(() -> new GuiaNoEncontradaException(id));
    }
}