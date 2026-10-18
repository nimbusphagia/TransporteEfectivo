package pe.cibertec.transportes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroForm {
  @NotBlank(message = "Ingresa tu nombre completo.")
  @Size(max = 150, message = "Máximo 150 caracteres.")
  private String nombreCompleto;

  @NotBlank(message = "Ingresa tu DNI.")
  @Pattern(regexp = "[0-9]{8}", message = "El DNI debe tener 8 dígitos.")
  private String dni;

  @NotBlank(message = "Ingresa tu correo.")
  @Email(message = "Escribe un correo válido.")
  @Size(max = 100, message = "Máximo 100 caracteres.")
  private String correo;

  @NotBlank(message = "Ingresa una contraseña.")
  @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres.")
  private String password;

  @NotBlank(message = "Confirma tu contraseña.")
  private String confirmarPassword;
}
