package pe.cibertec.transportes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alerta_trafico")
@Getter
@Setter
@NoArgsConstructor
public class AlertaTrafico extends BaseEntity {

  @NotBlank
  @Size(max = 100)
  @Column(nullable = false, length = 100)
  private String titulo;

  @NotBlank
  @Size(max = 500)
  @Column(nullable = false, length = 500)
  private String descripcion;

  @Column(nullable = false)
  private boolean activo = true;
}
