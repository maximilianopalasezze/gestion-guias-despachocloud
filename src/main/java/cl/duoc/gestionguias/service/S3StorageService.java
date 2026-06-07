package cl.duoc.gestionguias.service;

import cl.duoc.gestionguias.exception.AlmacenamientoException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class S3StorageService {

    private final S3Client s3Client;
    private final String bucketName;
    private final boolean s3Enabled;

    public S3StorageService(
            S3Client s3Client,
            @Value("${aws.s3.bucket-name}") String bucketName,
            @Value("${aws.s3.enabled}") boolean s3Enabled) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.s3Enabled = s3Enabled;
    }

    public Optional<String> subirArchivoSiEstaHabilitado(Path archivo, String transportista, LocalDate fecha, String nombreArchivo) {
        if (!s3Enabled || bucketName == null || bucketName.isBlank()) {
            return Optional.empty();
        }
        String key = generarKeyS3(transportista, fecha, nombreArchivo);
        subirArchivo(archivo, key);
        return Optional.of(key);
    }

    public String subirArchivo(Path archivo, String key) {
        validarS3Habilitado();
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType("application/pdf")
                    .build();

            s3Client.putObject(request, RequestBody.fromFile(archivo));
            return key;
        } catch (S3Exception ex) {
            throw new AlmacenamientoException("No se pudo subir el archivo a S3: " + ex.awsErrorDetails().errorMessage(), ex);
        } catch (Exception ex) {
            throw new AlmacenamientoException("No se pudo subir el archivo a S3", ex);
        }
    }

    public byte[] descargarArchivo(String key) {
        validarS3Habilitado();
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            ResponseBytes<GetObjectResponse> objeto = s3Client.getObjectAsBytes(request);
            return objeto.asByteArray();
        } catch (S3Exception ex) {
            throw new AlmacenamientoException("No se pudo descargar el archivo desde S3: " + ex.awsErrorDetails().errorMessage(), ex);
        } catch (Exception ex) {
            throw new AlmacenamientoException("No se pudo descargar el archivo desde S3", ex);
        }
    }

    public void eliminarArchivo(String key) {
        validarS3Habilitado();
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(request);
        } catch (S3Exception ex) {
            throw new AlmacenamientoException("No se pudo eliminar el archivo desde S3: " + ex.awsErrorDetails().errorMessage(), ex);
        } catch (Exception ex) {
            throw new AlmacenamientoException("No se pudo eliminar el archivo desde S3", ex);
        }
    }

    public String generarKeyS3(String transportista, LocalDate fecha, String nombreArchivo) {
        String transportistaNormalizado = transportista
                .trim()
                .toLowerCase()
                .replace(" ", "-")
                .replace("/", "-");

        return "guias/"
                + fecha.getYear() + "/"
                + String.format("%02d", fecha.getMonthValue()) + "/"
                + String.format("%02d", fecha.getDayOfMonth()) + "/"
                + transportistaNormalizado + "/"
                + nombreArchivo;
    }

    private void validarS3Habilitado() {
        if (!s3Enabled) {
            throw new AlmacenamientoException("AWS S3 no esta habilitado. Configura AWS_S3_ENABLED=true en EC2.");
        }
        if (bucketName == null || bucketName.isBlank()) {
            throw new AlmacenamientoException("No se configuro el nombre del bucket S3. Revisa AWS_S3_BUCKET_NAME.");
        }
    }
}
