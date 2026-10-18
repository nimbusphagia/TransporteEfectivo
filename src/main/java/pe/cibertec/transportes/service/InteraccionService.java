package pe.cibertec.transportes.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.cibertec.transportes.dto.InteraccionForm;
import pe.cibertec.transportes.entity.EstadoInteraccion;
import pe.cibertec.transportes.entity.Interaccion;
import pe.cibertec.transportes.entity.LineaTransporte;
import pe.cibertec.transportes.entity.TipoInteraccion;
import pe.cibertec.transportes.entity.Usuario;
import pe.cibertec.transportes.exception.ForbiddenException;
import pe.cibertec.transportes.exception.NotFoundException;
import pe.cibertec.transportes.repository.InteraccionRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InteraccionService {

  private final InteraccionRepository interacciones;
  private final LineaService lineaService;

  public List<Interaccion> listarPropias(Long usuarioId) {
    return interacciones.findByUsuarioIdOrderByCreatedAtDesc(usuarioId);
  }

  public Interaccion buscarPropia(Long interaccionId, Long usuarioId) {
    Interaccion interaccion = interacciones.findById(interaccionId)
        .orElseThrow(() -> new NotFoundException("No existe el reporte solicitado."));
    if (!interaccion.getUsuario().getId().equals(usuarioId)) {
      throw new ForbiddenException("No tienes permiso para consultar este reporte.");
    }
    return interaccion;
  }

  public List<Interaccion> listarParaAdministracion(
      TipoInteraccion tipo, Long lineaId, EstadoInteraccion estado) {
    return interacciones.buscarParaAdministracion(tipo, lineaId, estado);
  }

  @Transactional
  public Interaccion crear(InteraccionForm form, Usuario usuario) {
    LineaTransporte linea = lineaService.buscarActivaPorId(form.getLineaId());
    Interaccion interaccion = new Interaccion();
    interaccion.setTipo(form.getTipo());
    interaccion.setLinea(linea);
    interaccion.setComentario(form.getComentario().trim());
    interaccion.setEstado(EstadoInteraccion.PENDIENTE);
    interaccion.setUsuario(usuario);
    return interacciones.save(interaccion);
  }

  @Transactional
  public Interaccion marcarRevisada(Long interaccionId) {
    Interaccion interaccion = interacciones.findById(interaccionId)
        .orElseThrow(() -> new NotFoundException("No existe el reporte solicitado."));
    interaccion.setEstado(EstadoInteraccion.REVISADA);
    return interacciones.save(interaccion);
  }
}
