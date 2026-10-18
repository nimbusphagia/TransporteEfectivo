package pe.cibertec.transportes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambioPasswordForm {
  @NotBlank(message = "Ingresa tu contraseña actual.")
  private String passwordActual;

  @NotBlank(message = "Ingresa una contraseña nueva.")
  @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres.")
  private String passwordNueva;

  @NotBlank(message = "Confirma la contraseña nueva.")
  private String confirmarPassword;
}
