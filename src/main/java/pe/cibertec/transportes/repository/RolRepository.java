package pe.cibertec.transportes.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.cibertec.transportes.entity.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
  Optional<Rol> findByNombre(String nombre);
}
