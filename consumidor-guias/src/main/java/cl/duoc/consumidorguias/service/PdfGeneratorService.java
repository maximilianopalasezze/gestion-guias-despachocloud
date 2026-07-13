package cl.duoc.consumidorguias.service;

import cl.duoc.consumidorguias.dto.GuiaMensajeDTO;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class PdfGeneratorService {

    public Path generarPdfDesdeMensaje(GuiaMensajeDTO guia) {
        try {
            Path carpeta = Path.of("archivos-generados");
            Files.createDirectories(carpeta);

            String nombreArchivo = guia.getNombreArchivo();

            if (nombreArchivo == null || nombreArchivo.isBlank()) {
                nombreArchivo = guia.getNumeroGuia().toLowerCase() + ".pdf";
            }

            Path rutaArchivo = carpeta.resolve(nombreArchivo);

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo.toFile()));

            document.open();

            Font titulo = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font subtitulo = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normal = new Font(Font.HELVETICA, 12);

            document.add(new Paragraph("GUÍA DE DESPACHO", titulo));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Número de guía: " + guia.getNumeroGuia(), subtitulo));
            document.add(new Paragraph("Transportista: " + guia.getTransportista(), normal));
            document.add(new Paragraph("Destinatario: " + guia.getDestinatario(), normal));
            document.add(new Paragraph("Dirección de destino: " + guia.getDireccionDestino(), normal));
            document.add(new Paragraph("Descripción del pedido: " + guia.getDescripcionPedido(), normal));
            document.add(new Paragraph("Peso: " + guia.getPesoKg() + " kg", normal));
            document.add(new Paragraph("Fecha de generación: " + guia.getFechaGeneracion(), normal));
            document.add(new Paragraph("Fecha de despacho: " + guia.getFechaDespacho(), normal));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Documento generado automáticamente desde el microservicio consumidor.", normal));
            document.add(new Paragraph("Correlation ID: " + guia.getCorrelationId(), normal));

            document.close();

            return rutaArchivo;

        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo generar el PDF de la guía en el consumidor", ex);
        }
    }
}