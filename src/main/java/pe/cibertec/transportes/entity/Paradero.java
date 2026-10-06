package pe.cibertec.transportes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "paradero")
@Getter
@Setter
@NoArgsConstructor
public class Paradero extends BaseEntity {

  @NotBlank
  @Size(max = 100)
  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = true)
  private Double latitud;

  @Column(nullable = true)
  private Double longitud;

  @NotNull
  @Min(1)
  @Column(nullable = false)
  private Integer orden;

  @Column(nullable = false)
  private boolean activo = true;

  @NotNull
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "linea_id", nullable = false)
  private LineaTransporte linea;
}
