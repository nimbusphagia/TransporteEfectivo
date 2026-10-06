package pe.cibertec.transportes.exception;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  private static final String VISTA_ERROR = "error/general";

  @ExceptionHandler(AccessDeniedException.class)
  public void handleAccesoDenegado(AccessDeniedException ex) {
    throw ex;
  }

  @ExceptionHandler(AppException.class)
  public String handleApp(AppException ex, Model model, HttpServletResponse response) {
    return render(ex.getStatus(), ex.getMessage(), model, response);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public String handleTipoInvalido(Model model, HttpServletResponse response) {
    return render(HttpStatus.BAD_REQUEST, "El valor enviado no es válido.", model, response);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public String handleIntegridad(DataIntegrityViolationException ex, Model model,
      HttpServletResponse response) {
    log.warn("Violación de integridad: {}", ex.getMostSpecificCause().getMessage());
    return render(HttpStatus.CONFLICT, "Ya existe un registro con esos datos.", model, response);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public String handleNoEncontrado(Model model, HttpServletResponse response) {
    return render(HttpStatus.NOT_FOUND, "La página que buscas no existe.", model, response);
  }

  @ExceptionHandler(Exception.class)
  public String handleGeneral(Exception ex, Model model, HttpServletResponse response) {
    log.error("Error no controlado", ex);
    return render(HttpStatus.INTERNAL_SERVER_ERROR,
        "Ocurrió un error inesperado. Intenta nuevamente.", model, response);
  }

  private String render(HttpStatus status, String mensaje, Model model,
      HttpServletResponse response) {
    response.setStatus(status.value());
    model.addAttribute("status", status.value());
    model.addAttribute("titulo", status.getReasonPhrase());
    model.addAttribute("mensaje", mensaje);
    return VISTA_ERROR;
  }
}
