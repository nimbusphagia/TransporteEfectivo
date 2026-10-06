package pe.cibertec.transportes.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends AppException {

  public NotFoundException(String message) {
    super(HttpStatus.NOT_FOUND, message);
  }

  public NotFoundException() {
    super(HttpStatus.NOT_FOUND, "El recurso solicitado no existe.");
  }
}
