package pe.cibertec.transportes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pe.cibertec.transportes.dto.InteraccionForm;
import pe.cibertec.transportes.entity.EstadoInteraccion;
import pe.cibertec.transportes.entity.Interaccion;
import pe.cibertec.transportes.entity.LineaTransporte;
import pe.cibertec.transportes.entity.TipoInteraccion;
import pe.cibertec.transportes.entity.Usuario;
import pe.cibertec.transportes.exception.ForbiddenException;
import pe.cibertec.transportes.repository.InteraccionRepository;
import pe.cibertec.transportes.service.InteraccionService;
import pe.cibertec.transportes.service.LineaService;

@ExtendWith(MockitoExtension.class)
class InteraccionServiceTest {

  @Mock
  private InteraccionRepository interacciones;

  @Mock
  private LineaService lineaService;

  @InjectMocks
  private InteraccionService servicio;

  @Test
  void crearAsociaUsuarioDeSesionYGuardaReportePendiente() {
    InteraccionForm form = new InteraccionForm();
    form.setTipo(TipoInteraccion.QUEJA);
    form.setLineaId(8L);
    form.setComentario("  El servicio tuvo una demora.  ");
    Usuario pasajero = usuario(4L);
    LineaTransporte linea = new LineaTransporte();
    when(lineaService.buscarActivaPorId(8L)).thenReturn(linea);
    when(interacciones.save(any(Interaccion.class))).thenAnswer(inv -> inv.getArgument(0));

    Interaccion guardada = servicio.crear(form, pasajero);

    assertEquals(TipoInteraccion.QUEJA, guardada.getTipo());
    assertEquals(EstadoInteraccion.PENDIENTE, guardada.getEstado());
    assertEquals("El servicio tuvo una demora.", guardada.getComentario());
    assertSame(pasajero, guardada.getUsuario());
    assertSame(linea, guardada.getLinea());
  }

  @Test
  void buscarPropiaRechazaReportesDeOtroPasajero() {
    Usuario propietario = usuario(1L);
    Interaccion reporte = new Interaccion();
    reporte.setUsuario(propietario);
    when(interacciones.findById(10L)).thenReturn(Optional.of(reporte));

    assertThrows(ForbiddenException.class, () -> servicio.buscarPropia(10L, 2L));
  }

  @Test
  void buscarPropiaDevuelveReporteDelPasajero() {
    Usuario propietario = usuario(1L);
    Interaccion reporte = new Interaccion();
    reporte.setUsuario(propietario);
    when(interacciones.findById(10L)).thenReturn(Optional.of(reporte));

    assertSame(reporte, servicio.buscarPropia(10L, 1L));
  }

  @Test
  void marcarRevisadaActualizaElEstado() {
    Interaccion reporte = new Interaccion();
    reporte.setEstado(EstadoInteraccion.PENDIENTE);
    when(interacciones.findById(10L)).thenReturn(Optional.of(reporte));
    when(interacciones.save(reporte)).thenReturn(reporte);

    Interaccion revisada = servicio.marcarRevisada(10L);

    assertEquals(EstadoInteraccion.REVISADA, revisada.getEstado());
    verify(interacciones).save(reporte);
  }

  private Usuario usuario(Long id) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    return usuario;
  }
}
