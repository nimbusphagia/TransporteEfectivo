package pe.cibertec.transportes.controller;

import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.cibertec.transportes.dto.RegistroForm;
import pe.cibertec.transportes.service.UsuarioService;

@Controller
public class AuthController {
  private final UsuarioService usuarioService;

  public AuthController(UsuarioService usuarioService) {
    this.usuarioService = usuarioService;
  }

  @GetMapping("/login")
  public String login(Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()
        && !(authentication instanceof AnonymousAuthenticationToken)) {
      return "redirect:/perfil";
    }
    return "auth/login";
  }

  @GetMapping("/registro")
  public String formulario(Model model, Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()
        && !(authentication instanceof AnonymousAuthenticationToken)) {
      return "redirect:/perfil";
    }
    if (!model.containsAttribute("registro")) {
      model.addAttribute("registro", new RegistroForm());
    }
    return "auth/registro";
  }

  @PostMapping("/registro")
  public String registrar(@Valid @ModelAttribute("registro") RegistroForm registro,
      BindingResult errores, RedirectAttributes mensajes) {
    if (!registro.getPassword().equals(registro.getConfirmarPassword())) {
      errores.rejectValue("confirmarPassword", "noCoincide", "Las contraseñas no coinciden.");
    }
    if (!errores.hasFieldErrors("correo") && usuarioService.correoRegistrado(registro.getCorreo())) {
      errores.rejectValue("correo", "duplicado", "El correo ya está registrado.");
    }
    if (!errores.hasFieldErrors("dni") && usuarioService.dniRegistrado(registro.getDni())) {
      errores.rejectValue("dni", "duplicado", "El DNI ya está registrado.");
    }
    if (errores.hasErrors()) {
      return "auth/registro";
    }
    usuarioService.registrarPasajero(registro);
    mensajes.addFlashAttribute("exito", "Cuenta creada correctamente. Ya puedes ingresar.");
    return "redirect:/login";
  }
}
