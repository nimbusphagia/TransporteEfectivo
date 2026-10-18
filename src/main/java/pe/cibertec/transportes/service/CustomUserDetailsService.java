package pe.cibertec.transportes.service;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cibertec.transportes.entity.Usuario;
import pe.cibertec.transportes.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UsuarioRepository usuarios;

  public CustomUserDetailsService(UsuarioRepository usuarios) {
    this.usuarios = usuarios;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
    Usuario usuario = usuarios.findByCorreoIgnoreCase(UsuarioService.normalizarCorreo(correo))
        .orElseThrow(() -> new UsernameNotFoundException("Credenciales incorrectas."));
    return new UsuarioPrincipal(usuario.getCorreo(), usuario.getPassword(), usuario.isActivo(),
        List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre())),
        usuario.getNombreCompleto());
  }
}
