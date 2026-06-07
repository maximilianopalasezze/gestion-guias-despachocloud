package cl.duoc.gestionguias.repository;

import cl.duoc.gestionguias.model.Guia;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuiaRepository extends JpaRepository<Guia, Long> {

    List<Guia> findByTransportistaIgnoreCase(String transportista);

    List<Guia> findByFechaGeneracion(LocalDate fechaGeneracion);

    List<Guia> findByTransportistaIgnoreCaseAndFechaGeneracion(String transportista, LocalDate fechaGeneracion);
}
