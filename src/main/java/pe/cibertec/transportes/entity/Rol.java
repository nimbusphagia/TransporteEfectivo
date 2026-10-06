package pe.cibertec.transportes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rol")
@Getter
@Setter
@NoArgsConstructor
public class Rol {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // ADMIN, PASAJERO
  @Column(nullable = false, unique = true, length = 50)
  private String nombre;

  @Column(length = 255)
  private String descripcion;
}
