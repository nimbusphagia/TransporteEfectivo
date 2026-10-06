package pe.cibertec.transportes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario extends BaseEntity {

  @Column(nullable = false, length = 150)
  private String nombreCompleto;

  @Column(nullable = false, unique = true, length = 8)
  private String dni;

  @Column(nullable = false, unique = true, length = 100)
  private String correo;

  @Column(nullable = false, length = 100)
  private String password;

  @Column(nullable = false)
  private boolean activo = true;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "rol_id", nullable = false)
  private Rol rol;
}
