package cl.duoc.gestionguias.service;

import cl.duoc.gestionguias.exception.AlmacenamientoException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EfsStorageService {

    private final Path rutaGuias;

    public EfsStorageService(@Value("${app.efs.guias-path}") String rutaGuias) {
        this.rutaGuias = Path.of(rutaGuias);
        crearDirectorioSiNoExiste();
    }

    public Path obtenerRutaArchivo(String nombreArchivo) {
        crearDirectorioSiNoExiste();
        return rutaGuias.resolve(nombreArchivo);
    }

    public byte[] leerArchivo(String rutaArchivo) {
        try {
            return Files.readAllBytes(Path.of(rutaArchivo));
        } catch (IOException ex) {
            throw new AlmacenamientoException("No se pudo leer el archivo temporal desde EFS", ex);
        }
    }

    public void eliminarArchivo(String rutaArchivo) {
        try {
            Files.deleteIfExists(Path.of(rutaArchivo));
        } catch (IOException ex) {
            throw new AlmacenamientoException("No se pudo eliminar el archivo temporal desde EFS", ex);
        }
    }

    private void crearDirectorioSiNoExiste() {
        try {
            Files.createDirectories(rutaGuias);
        } catch (IOException ex) {
            throw new AlmacenamientoException("No se pudo crear el directorio temporal de EFS", ex);
        }
    }
}
