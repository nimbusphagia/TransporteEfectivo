package pe.cibertec.transportes.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends AppException {

  public ForbiddenException(String message) {
    super(HttpStatus.FORBIDDEN, message);
  }

  public ForbiddenException() {
    super(HttpStatus.FORBIDDEN, "No tienes permiso para realizar esta acción.");
  }
}
