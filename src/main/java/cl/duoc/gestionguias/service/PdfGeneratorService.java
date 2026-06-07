package cl.duoc.gestionguias.service;

import cl.duoc.gestionguias.exception.AlmacenamientoException;
import cl.duoc.gestionguias.model.Guia;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Service;

@Service
public class PdfGeneratorService {

    public void generarPdf(Guia guia, Path destino) {
        try {
            Files.createDirectories(destino.getParent());

            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream(destino.toFile()));
            documento.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font subtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font texto = FontFactory.getFont(FontFactory.HELVETICA, 11);

            documento.add(new Paragraph("GUIA DE DESPACHO", titulo));
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("Numero de guia: " + guia.getNumeroGuia(), subtitulo));
            documento.add(new Paragraph("Fecha de generacion: " + guia.getFechaGeneracion(), texto));
            documento.add(new Paragraph("Fecha de despacho: " + guia.getFechaDespacho(), texto));
            documento.add(new Paragraph("Estado: " + guia.getEstado(), texto));
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("Datos del transportista", subtitulo));
            documento.add(new Paragraph("Transportista: " + guia.getTransportista(), texto));
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("Datos del destinatario", subtitulo));
            documento.add(new Paragraph("Destinatario: " + guia.getDestinatario(), texto));
            documento.add(new Paragraph("Direccion destino: " + guia.getDireccionDestino(), texto));
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("Detalle del pedido", subtitulo));
            documento.add(new Paragraph("Descripcion: " + guia.getDescripcionPedido(), texto));
            documento.add(new Paragraph("Peso KG: " + guia.getPesoKg(), texto));
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("Ruta temporal EFS: " + guia.getRutaEfs(), texto));
            documento.add(new Paragraph("Ruta S3: " + (guia.getRutaS3() == null ? "Pendiente" : guia.getRutaS3()), texto));

            documento.close();
        } catch (Exception ex) {
            throw new AlmacenamientoException("No se pudo generar el PDF de la guia", ex);
        }
    }
}
