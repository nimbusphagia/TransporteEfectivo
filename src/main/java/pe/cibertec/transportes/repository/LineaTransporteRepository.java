package pe.cibertec.transportes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pe.cibertec.transportes.entity.LineaTransporte;

@Repository
public interface LineaTransporteRepository extends JpaRepository<LineaTransporte, Long> {

  List<LineaTransporte> findByActivoTrueOrderByNombreAsc();

  List<LineaTransporte> findAllByOrderByNombreAsc();

  Optional<LineaTransporte> findByIdAndActivoTrue(Long id);

  boolean existsByNombreIgnoreCase(String nombre);

  boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

  List<LineaTransporte> findByActivoTrueAndNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);
}
