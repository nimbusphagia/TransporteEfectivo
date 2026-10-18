package pe.cibertec.transportes.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.cibertec.transportes.entity.LineaTransporte;

@Getter
@Setter
@NoArgsConstructor
public class LineaForm {
  private Long id;

  @NotBlank(message = "El nombre es obligatorio.")
  @Size(max = 100, message = "El nombre puede tener como máximo 100 caracteres.")
  private String nombre;

  @NotNull(message = "El costo del pasaje es obligatorio.")
  @DecimalMin(value = "0.00", inclusive = false, message = "El costo debe ser mayor a cero.")
  @Digits(integer = 6, fraction = 2, message = "El costo admite como máximo 2 decimales.")
  private BigDecimal costoPasaje;

  @Size(max = 255, message = "La ruta de la imagen puede tener como máximo 255 caracteres.")
  private String imagenUrl;

  public static LineaForm desde(LineaTransporte linea) {
    LineaForm form = new LineaForm();
    form.setId(linea.getId());
    form.setNombre(linea.getNombre());
    form.setCostoPasaje(linea.getCostoPasaje());
    form.setImagenUrl(linea.getImagenUrl());
    return form;
  }

  public boolean esNueva() {
    return id == null;
  }
}
