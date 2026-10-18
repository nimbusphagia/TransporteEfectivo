package pe.cibertec.transportes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.cibertec.transportes.dto.RegistroForm;
import pe.cibertec.transportes.entity.Rol;
import pe.cibertec.transportes.entity.Usuario;
import pe.cibertec.transportes.repository.RolRepository;
import pe.cibertec.transportes.repository.UsuarioRepository;
import pe.cibertec.transportes.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
  @Mock UsuarioRepository usuarios;
  @Mock RolRepository roles;
  @Mock PasswordEncoder passwordEncoder;
  @InjectMocks UsuarioService servicio;

  @Test
  void registrarPasajeroNormalizaCorreoYCifraPassword() {
    RegistroForm form = new RegistroForm();
    form.setNombreCompleto("  Ana Torres  ");
    form.setDni("12345678");
    form.setCorreo(" Ana@Example.com ");
    form.setPassword("claveSegura123");
    Rol pasajero = new Rol();
    pasajero.setNombre("PASAJERO");
    when(roles.findByNombre("PASAJERO")).thenReturn(Optional.of(pasajero));
    when(passwordEncoder.encode("claveSegura123")).thenReturn("hashBCrypt");
    when(usuarios.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

    Usuario guardado = servicio.registrarPasajero(form);
    assertEquals("Ana Torres", guardado.getNombreCompleto());
    assertEquals("ana@example.com", guardado.getCorreo());
    assertEquals("hashBCrypt", guardado.getPassword());
    assertEquals("PASAJERO", guardado.getRol().getNombre());
  }

  @Test
  void cambiarPasswordVerificaActualYGuardaHashNuevo() {
    Usuario usuario = new Usuario();
    usuario.setActivo(true);
    usuario.setCorreo("ana@example.com");
    usuario.setPassword("hashViejo");
    when(usuarios.findByCorreoIgnoreCase("ana@example.com")).thenReturn(Optional.of(usuario));
    when(passwordEncoder.matches("anterior123", "hashViejo")).thenReturn(true);
    when(passwordEncoder.matches("nuevoPassword123", "hashViejo")).thenReturn(false);
    when(passwordEncoder.encode("nuevoPassword123")).thenReturn("hashNuevo");

    servicio.cambiarPassword("ana@example.com", "anterior123", "nuevoPassword123");
    verify(usuarios).save(usuario);
    assertTrue(usuario.getPassword().equals("hashNuevo"));
  }
}
