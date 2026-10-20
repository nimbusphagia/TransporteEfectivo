package pe.cibertec.transportes.repository;

import pe.cibertec.transportes.entity.AlertaTrafico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlertaTraficoRepository extends JpaRepository<AlertaTrafico, Long> {
    List<AlertaTrafico> findAllByOrderByCreatedAtDesc();
    Optional<AlertaTrafico> findTopByOrderByCreatedAtDesc();
    Optional<AlertaTrafico> findTopByActivoTrueOrderByCreatedAtDesc();
}