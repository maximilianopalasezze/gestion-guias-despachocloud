package cl.duoc.consumidorguias.repository;

import cl.duoc.consumidorguias.model.GuiaProcesada;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuiaProcesadaRepository extends JpaRepository<GuiaProcesada, Long> {

    boolean existsByCorrelationId(String correlationId);
}