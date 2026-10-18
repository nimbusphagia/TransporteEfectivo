package pe.cibertec.transportes.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.cibertec.transportes.dto.CambioPasswordForm;
import pe.cibertec.transportes.entity.Usuario;
import pe.cibertec.transportes.exception.BadRequestException;
import pe.cibertec.transportes.service.UsuarioService;

@Controller
public class PerfilController {
  private final UsuarioService usuarioService;

  public PerfilController(UsuarioService usuarioService) {
    this.usuarioService = usuarioService;
  }

  @GetMapping({"/perfil", "/admin/perfil"})
  public String verPerfil(Model model, Principal principal) {
    cargarPerfil(model, principal);
    model.addAttribute("cambio", new CambioPasswordForm());
    return "perfil/index";
  }

  @PostMapping("/perfil/contrasena")
  public String cambiarContrasena(@Valid @ModelAttribute("cambio") CambioPasswordForm cambio,
      BindingResult errores, Model model, Principal principal, RedirectAttributes mensajes) {
    if (cambio.getPasswordNueva() != null
        && !cambio.getPasswordNueva().equals(cambio.getConfirmarPassword())) {
      errores.rejectValue("confirmarPassword", "noCoincide", "Las contraseñas no coinciden.");
    }
    if (errores.hasErrors()) {
      cargarPerfil(model, principal);
      return "perfil/index";
    }
    try {
      usuarioService.cambiarPassword(principal.getName(), cambio.getPasswordActual(),
          cambio.getPasswordNueva());
    } catch (BadRequestException ex) {
      errores.rejectValue("passwordActual", "incorrecta", ex.getMessage());
      cargarPerfil(model, principal);
      return "perfil/index";
    }
    mensajes.addFlashAttribute("exito", "Contraseña actualizada correctamente.");
    Usuario usuario = usuarioService.buscarPorCorreo(principal.getName());
    return "redirect:" + (esAdmin(usuario) ? "/admin/perfil" : "/perfil");
  }

  private void cargarPerfil(Model model, Principal principal) {
    Usuario usuario = usuarioService.buscarPorCorreo(principal.getName());
    model.addAttribute("usuario", usuario);
    model.addAttribute("esAdmin", esAdmin(usuario));
  }

  private boolean esAdmin(Usuario usuario) {
    return "ADMIN".equals(usuario.getRol().getNombre());
  }
}
