package pe.cibertec.transportes.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.cibertec.transportes.dto.LineaForm;
import pe.cibertec.transportes.entity.LineaTransporte;
import pe.cibertec.transportes.exception.BadRequestException;
import pe.cibertec.transportes.exception.ConflictException;
import pe.cibertec.transportes.exception.NotFoundException;
import pe.cibertec.transportes.repository.LineaTransporteRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LineaService {
  private final LineaTransporteRepository lineaRepository;

  public List<LineaTransporte> listarActivas() {
    return lineaRepository.findByActivoTrueOrderByNombreAsc();
  }

  public List<LineaTransporte> buscarActivasPorNombre(String termino) {
    if (termino == null || termino.isBlank()) {
      return List.of();
    }
    return lineaRepository.findByActivoTrueAndNombreContainingIgnoreCaseOrderByNombreAsc(termino.trim());
  }

  public List<LineaTransporte> listarTodas() {
    return lineaRepository.findAllByOrderByNombreAsc();
  }

  public LineaTransporte buscarPorId(Long id) {
    return lineaRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("No existe la línea solicitada."));
  }

  public LineaTransporte buscarActivaPorId(Long id) {
    return lineaRepository.findByIdAndActivoTrue(id)
        .orElseThrow(() -> new NotFoundException("La línea solicitada no existe o no está disponible."));
  }

  @Transactional
  public LineaTransporte guardar(LineaForm form) {
    String nombre = form.getNombre().trim();
    BigDecimal costo = form.getCostoPasaje();

    if (costo == null || costo.compareTo(BigDecimal.ZERO) <= 0) {
      throw new BadRequestException("El costo debe ser mayor a cero.");
    }

    boolean duplicado = form.esNueva()
        ? lineaRepository.existsByNombreIgnoreCase(nombre)
        : lineaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, form.getId());
    if (duplicado) {
      throw new ConflictException("Ya existe una línea con el nombre «" + nombre + "».");
    }

    LineaTransporte linea = form.esNueva() ? new LineaTransporte() : buscarPorId(form.getId());
    linea.setNombre(nombre);
    linea.setCostoPasaje(costo);
    linea.setImagenUrl(normalizarImagen(form.getImagenUrl()));
    return lineaRepository.save(linea);
  }

  @Transactional
  public LineaTransporte cambiarEstado(Long id, boolean activo) {
    LineaTransporte linea = buscarPorId(id);
    linea.setActivo(activo);
    return lineaRepository.save(linea);
  }

  private String normalizarImagen(String imagenUrl) {
    if (imagenUrl == null || imagenUrl.isBlank()) {
      return null;
    }
    return imagenUrl.trim();
  }
}
