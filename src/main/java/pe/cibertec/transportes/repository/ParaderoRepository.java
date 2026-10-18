package pe.cibertec.transportes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pe.cibertec.transportes.entity.Paradero;

@Repository
public interface ParaderoRepository extends JpaRepository<Paradero, Long> {
  List<Paradero> findByLineaIdAndActivoTrueAndLineaActivoTrueOrderByOrdenAsc(Long lineaId);

  List<Paradero> findByLineaIdOrderByActivoDescOrdenAsc(Long lineaId);

  boolean existsByLineaIdAndOrdenAndActivoTrue(Long lineaId, Integer orden);

  boolean existsByLineaIdAndOrdenAndActivoTrueAndIdNot(Long lineaId, Integer orden, Long id);

  Optional<Paradero> findFirstByLineaIdAndActivoTrueAndOrdenLessThanOrderByOrdenDesc(
      Long lineaId, Integer orden);

  Optional<Paradero> findFirstByLineaIdAndActivoTrueAndOrdenGreaterThanOrderByOrdenAsc(
      Long lineaId, Integer orden);

  @Query("select coalesce(max(p.orden), 0) from Paradero p "
      + "where p.linea.id = :lineaId and p.activo = true")
  Integer maxOrdenActivo(@Param("lineaId") Long lineaId);


  @Query("SELECT p FROM Paradero p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND p.activo = true")
  List<Paradero> buscarPorNombreActivo(@Param("nombre") String nombre);

}
