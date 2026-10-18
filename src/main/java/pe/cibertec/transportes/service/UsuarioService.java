package pe.cibertec.transportes.service;

import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cibertec.transportes.dto.RegistroForm;
import pe.cibertec.transportes.entity.Rol;
import pe.cibertec.transportes.entity.Usuario;
import pe.cibertec.transportes.exception.BadRequestException;
import pe.cibertec.transportes.exception.ConflictException;
import pe.cibertec.transportes.exception.NotFoundException;
import pe.cibertec.transportes.repository.RolRepository;
import pe.cibertec.transportes.repository.UsuarioRepository;

@Service
public class UsuarioService {
  private final UsuarioRepository usuarios;
  private final RolRepository roles;
  private final PasswordEncoder passwordEncoder;

  public UsuarioService(UsuarioRepository usuarios, RolRepository roles,
                        PasswordEncoder passwordEncoder) {
    this.usuarios = usuarios;
    this.roles = roles;
    this.passwordEncoder = passwordEncoder;
  }

  public boolean correoRegistrado(String correo) {
    return usuarios.existsByCorreoIgnoreCase(normalizarCorreo(correo));
  }

  public boolean dniRegistrado(String dni) {
    return usuarios.existsByDni(dni == null ? "" : dni.trim());
  }

  @Transactional
  public Usuario registrarPasajero(RegistroForm formulario) {
    String correo = normalizarCorreo(formulario.getCorreo());
    String dni = formulario.getDni().trim();
    if (correoRegistrado(correo)) {
      throw new ConflictException("El correo ya está registrado.");
    }
    if (dniRegistrado(dni)) {
      throw new ConflictException("El DNI ya está registrado.");
    }
    Rol rol = roles.findByNombre("PASAJERO")
        .orElseThrow(() -> new IllegalStateException("Falta el rol PASAJERO."));
    Usuario usuario = new Usuario();
    usuario.setNombreCompleto(formulario.getNombreCompleto().trim());
    usuario.setDni(dni);
    usuario.setCorreo(correo);
    usuario.setPassword(passwordEncoder.encode(formulario.getPassword()));
    usuario.setRol(rol);
    usuario.setActivo(true);
    return usuarios.save(usuario);
  }

  @Transactional(readOnly = true)
  public Usuario buscarPorCorreo(String correo) {
    return usuarios.findByCorreoIgnoreCase(normalizarCorreo(correo))
        .orElseThrow(() -> new NotFoundException("Usuario no encontrado."));
  }

  @Transactional
  public void cambiarPassword(String correo, String actual, String nueva) {
    Usuario usuario = buscarPorCorreo(correo);
    if (!usuario.isActivo()) {
      throw new BadRequestException("La cuenta está desactivada.");
    }
    if (!passwordEncoder.matches(actual, usuario.getPassword())) {
      throw new BadRequestException("La contraseña actual es incorrecta.");
    }
    if (passwordEncoder.matches(nueva, usuario.getPassword())) {
      throw new BadRequestException("La nueva contraseña debe ser diferente de la actual.");
    }
    usuario.setPassword(passwordEncoder.encode(nueva));
    usuarios.save(usuario);
  }

  public static String normalizarCorreo(String correo) {
    return correo == null ? "" : correo.trim().toLowerCase(Locale.ROOT);
  }
}
