package pe.cibertec.transportes.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.cibertec.transportes.entity.Paradero;

@Getter
@Setter
@NoArgsConstructor
public class ParaderoForm {
  private Long id;

  @NotNull(message = "Selecciona una línea.")
  private Long lineaId;

  @NotBlank(message = "El nombre es obligatorio.")
  @Size(max = 100, message = "El nombre puede tener como máximo 100 caracteres.")
  private String nombre;

  @NotNull(message = "La posición es obligatoria.")
  @Min(value = 1, message = "La posición debe ser 1 o mayor.")
  private Integer orden;

  @DecimalMin(value = "-90", message = "La latitud debe estar entre -90 y 90.")
  @DecimalMax(value = "90", message = "La latitud debe estar entre -90 y 90.")
  private Double latitud;

  @DecimalMin(value = "-180", message = "La longitud debe estar entre -180 y 180.")
  @DecimalMax(value = "180", message = "La longitud debe estar entre -180 y 180.")
  private Double longitud;

  @AssertTrue(message = "Ingresa latitud y longitud juntas, o deja ambas vacías.")
  public boolean isCoordenadasCompletas() {
    return (latitud == null) == (longitud == null);
  }

  public static ParaderoForm desde(Paradero paradero) {
    ParaderoForm form = new ParaderoForm();
    form.setId(paradero.getId());
    form.setLineaId(paradero.getLinea().getId());
    form.setNombre(paradero.getNombre());
    form.setOrden(paradero.getOrden());
    form.setLatitud(paradero.getLatitud());
    form.setLongitud(paradero.getLongitud());
    return form;
  }

  public boolean esNuevo() {
    return id == null;
  }
}
