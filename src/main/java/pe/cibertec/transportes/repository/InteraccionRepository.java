package pe.cibertec.transportes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.cibertec.transportes.entity.EstadoInteraccion;
import pe.cibertec.transportes.entity.Interaccion;
import pe.cibertec.transportes.entity.TipoInteraccion;

public interface InteraccionRepository extends JpaRepository<Interaccion, Long> {

  List<Interaccion> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);

  @Query("""
      select i
      from Interaccion i
      where (:tipo is null or i.tipo = :tipo)
        and (:lineaId is null or i.linea.id = :lineaId)
        and (:estado is null or i.estado = :estado)
      order by i.createdAt desc
      """)
  List<Interaccion> buscarParaAdministracion(
      @Param("tipo") TipoInteraccion tipo,
      @Param("lineaId") Long lineaId,
      @Param("estado") EstadoInteraccion estado);
}
