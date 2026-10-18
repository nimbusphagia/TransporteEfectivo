package pe.cibertec.transportes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.cibertec.transportes.entity.Rol;
import pe.cibertec.transportes.entity.Usuario;
import pe.cibertec.transportes.repository.RolRepository;
import pe.cibertec.transportes.repository.UsuarioRepository;
import pe.cibertec.transportes.service.UsuarioService;

@Component
public class DataInitializer implements ApplicationRunner {
  private final RolRepository roles;
  private final UsuarioRepository usuarios;
  private final PasswordEncoder passwordEncoder;

  @Value("${ADMIN_EMAIL:}") private String adminEmail;
  @Value("${ADMIN_PASSWORD:}") private String adminPassword;
  @Value("${ADMIN_DNI:}") private String adminDni;
  @Value("${ADMIN_NOMBRE:}") private String adminNombre;
  @Value("${DEMO_EMAIL:}") private String demoEmail;
  @Value("${DEMO_PASSWORD:}") private String demoPassword;
  @Value("${DEMO_DNI:}") private String demoDni;
  @Value("${DEMO_NOMBRE:}") private String demoNombre;

  public DataInitializer(RolRepository roles, UsuarioRepository usuarios,
      PasswordEncoder passwordEncoder) {
    this.roles = roles;
    this.usuarios = usuarios;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    Rol admin = rol("ADMIN", "Administración del sistema");
    Rol pasajero = rol("PASAJERO", "Usuario pasajero");
    crearInicial(admin, adminNombre, adminDni, adminEmail, adminPassword);
    crearInicial(pasajero, demoNombre, demoDni, demoEmail, demoPassword);
  }

  private Rol rol(String nombre, String descripcion) {
    return roles.findByNombre(nombre).orElseGet(() -> {
      Rol rol = new Rol();
      rol.setNombre(nombre);
      rol.setDescripcion(descripcion);
      return roles.save(rol);
    });
  }

  private void crearInicial(Rol rol, String nombre, String dni, String correo, String clave) {
    // Las cuentas iniciales son opcionales: nunca se publican claves predeterminadas.
    if (nombre.isBlank() || dni.isBlank() || correo.isBlank() || clave.isBlank()) return;
    if (!dni.matches("[0-9]{8}") || clave.length() < 8 || clave.length() > 72) {
      throw new IllegalStateException("Datos inválidos para la cuenta inicial " + rol.getNombre());
    }
    String correoNormalizado = UsuarioService.normalizarCorreo(correo);
    if (usuarios.existsByCorreoIgnoreCase(correoNormalizado)) return;
    if (usuarios.existsByDni(dni)) {
      throw new IllegalStateException("El DNI de la cuenta inicial ya está registrado.");
    }
    Usuario usuario = new Usuario();
    usuario.setNombreCompleto(nombre.trim());
    usuario.setDni(dni);
    usuario.setCorreo(correoNormalizado);
    usuario.setPassword(passwordEncoder.encode(clave));
    usuario.setRol(rol);
    usuario.setActivo(true);
    usuarios.save(usuario);
  }
}
