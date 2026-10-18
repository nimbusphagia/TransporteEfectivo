package pe.cibertec.transportes.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.cibertec.transportes.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  Optional<Usuario> findByCorreoIgnoreCase(String correo);
  boolean existsByCorreoIgnoreCase(String correo);
  boolean existsByDni(String dni);
}
