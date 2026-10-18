package pe.cibertec.transportes.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.cibertec.transportes.dto.ParaderoForm;
import pe.cibertec.transportes.entity.LineaTransporte;
import pe.cibertec.transportes.entity.Paradero;
import pe.cibertec.transportes.exception.ConflictException;
import pe.cibertec.transportes.exception.NotFoundException;
import pe.cibertec.transportes.repository.ParaderoRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParaderoService {
  private final ParaderoRepository paraderoRepository;
  private final LineaService lineaService;

  public List<Paradero> listarActivosPorLinea(Long lineaId) {
    return paraderoRepository.findByLineaIdAndActivoTrueAndLineaActivoTrueOrderByOrdenAsc(lineaId);
  }

  public List<Paradero> listarPorLinea(Long lineaId) {
    return paraderoRepository.findByLineaIdOrderByActivoDescOrdenAsc(lineaId);
  }

  public Paradero buscarPorId(Long id) {
    return paraderoRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("No existe el paradero solicitado."));
  }

  public int siguienteOrden(Long lineaId) {
    return paraderoRepository.maxOrdenActivo(lineaId) + 1;
  }

  @Transactional
  public Paradero guardar(ParaderoForm form) {
    LineaTransporte linea = lineaService.buscarActivaPorId(form.getLineaId());
    validarPosicionLibre(linea.getId(), form.getOrden(), form.getId());

    Paradero paradero = form.esNuevo() ? new Paradero() : buscarPorId(form.getId());
    paradero.setNombre(form.getNombre().trim());
    paradero.setLinea(linea);
    paradero.setOrden(form.getOrden());
    paradero.setLatitud(form.getLatitud());
    paradero.setLongitud(form.getLongitud());
    return paraderoRepository.save(paradero);
  }

  @Transactional
  public Paradero desactivar(Long id) {
    Paradero paradero = buscarPorId(id);
    paradero.setActivo(false);
    return paraderoRepository.save(paradero);
  }

  @Transactional
  public Paradero activar(Long id) {
    Paradero paradero = buscarPorId(id);
    validarPosicionLibre(paradero.getLinea().getId(), paradero.getOrden(), paradero.getId());
    paradero.setActivo(true);
    return paraderoRepository.save(paradero);
  }

  @Transactional
  public Paradero subir(Long id) {
    Paradero paradero = buscarPorId(id);
    paraderoRepository
        .findFirstByLineaIdAndActivoTrueAndOrdenLessThanOrderByOrdenDesc(
            paradero.getLinea().getId(), paradero.getOrden())
        .ifPresent(anterior -> intercambiar(paradero, anterior));
    return paradero;
  }

  @Transactional
  public Paradero bajar(Long id) {
    Paradero paradero = buscarPorId(id);
    Optional<Paradero> siguiente = paraderoRepository
        .findFirstByLineaIdAndActivoTrueAndOrdenGreaterThanOrderByOrdenAsc(
            paradero.getLinea().getId(), paradero.getOrden());
    siguiente.ifPresent(s -> intercambiar(paradero, s));
    return paradero;
  }

  private void intercambiar(Paradero a, Paradero b) {
    Integer ordenA = a.getOrden();
    a.setOrden(b.getOrden());
    b.setOrden(ordenA);
    paraderoRepository.save(a);
    paraderoRepository.save(b);
  }

  private void validarPosicionLibre(Long lineaId, Integer orden, Long idActual) {
    boolean ocupada = idActual == null
        ? paraderoRepository.existsByLineaIdAndOrdenAndActivoTrue(lineaId, orden)
        : paraderoRepository.existsByLineaIdAndOrdenAndActivoTrueAndIdNot(lineaId, orden, idActual);
    if (ocupada) {
      throw new ConflictException("La posición " + orden + " ya está ocupada en esta línea.");
    }
  }
}
