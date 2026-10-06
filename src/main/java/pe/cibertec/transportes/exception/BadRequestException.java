package pe.cibertec.transportes.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends AppException {

  public BadRequestException(String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }

  public BadRequestException() {
    super(HttpStatus.BAD_REQUEST, "La solicitud no es válida.");
  }
}
