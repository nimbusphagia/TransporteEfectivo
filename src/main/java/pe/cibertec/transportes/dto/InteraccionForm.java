package pe.cibertec.transportes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.cibertec.transportes.entity.TipoInteraccion;

@Getter
@Setter
@NoArgsConstructor
public class InteraccionForm {

  @NotNull(message = "Selecciona si es una queja o una sugerencia.")
  private TipoInteraccion tipo;

  @NotNull(message = "Selecciona una línea.")
  private Long lineaId;

  @NotBlank(message = "El comentario es obligatorio.")
  @Size(max = 1000, message = "El comentario puede tener como máximo 1000 caracteres.")
  private String comentario;
}
