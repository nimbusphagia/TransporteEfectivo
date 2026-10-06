package pe.cibertec.transportes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "linea_transporte")
@Getter
@Setter
@NoArgsConstructor
public class LineaTransporte extends BaseEntity {

  @NotBlank
  @Size(max = 100)
  @Column(nullable = false, unique = true, length = 100)
  private String nombre;

  @NotNull
  @DecimalMin(value = "0.00", inclusive = false)
  @Column(nullable = false, precision = 8, scale = 2)
  private BigDecimal costoPasaje;

  @Size(max = 255)
  @Column(length = 255)
  private String imagenUrl;

  @Column(nullable = false)
  private boolean activo = true;
}
