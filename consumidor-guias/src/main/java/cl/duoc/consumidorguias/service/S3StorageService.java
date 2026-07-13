package cl.duoc.consumidorguias.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Path;
import java.time.LocalDate;

@Service
public class S3StorageService {

    private final S3Client s3Client;
    private final String bucketName;
    private final boolean s3Enabled;

    public S3StorageService(
            S3Client s3Client,
            @Value("${aws.s3.bucket-name}") String bucketName,
            @Value("${aws.s3.enabled}") boolean s3Enabled
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.s3Enabled = s3Enabled;
    }

    public String subirGuiaSiEstaHabilitado(
            Path archivo,
            String transportista,
            LocalDate fecha,
            String nombreArchivo
    ) {
        if (!s3Enabled) {
            return null;
        }

        if (bucketName == null || bucketName.isBlank()) {
            throw new IllegalStateException("S3 está habilitado, pero no se configuró el bucket");
        }

        String key = generarKeyS3(transportista, fecha, nombreArchivo);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("application/pdf")
                .build();

        s3Client.putObject(request, RequestBody.fromFile(archivo));

        return key;
    }

    public String generarKeyS3(String transportista, LocalDate fecha, String nombreArchivo) {
        String transportistaNormalizado = transportista
                .trim()
                .toLowerCase()
                .replace(" ", "-")
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace("ñ", "n");

        return "guias/" + transportistaNormalizado + "/" + fecha + "/" + nombreArchivo;
    }
}