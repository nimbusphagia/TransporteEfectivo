package pe.cibertec.transportes.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends AppException {

  public ConflictException(String message) {
    super(HttpStatus.CONFLICT, message);
  }

  public ConflictException() {
    super(HttpStatus.CONFLICT, "Ya existe un registro con esos datos.");
  }
}
