package pe.cibertec.transportes.service;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class UsuarioPrincipal extends User {
  private final String nombreCompleto;

  public UsuarioPrincipal(String correo, String password, boolean activo,
      Collection<? extends GrantedAuthority> authorities, String nombreCompleto) {
    super(correo, password, activo, true, true, true, authorities);
    this.nombreCompleto = nombreCompleto;
  }

  public String getNombreCompleto() {
    return nombreCompleto;
  }
}
