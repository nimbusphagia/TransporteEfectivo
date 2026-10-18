package pe.cibertec.transportes.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pe.cibertec.transportes.dto.InteraccionForm;
import pe.cibertec.transportes.entity.EstadoInteraccion;
import pe.cibertec.transportes.entity.TipoInteraccion;
import pe.cibertec.transportes.entity.Usuario;
import pe.cibertec.transportes.exception.NotFoundException;
import pe.cibertec.transportes.service.InteraccionService;
import pe.cibertec.transportes.service.LineaService;
import pe.cibertec.transportes.service.UsuarioService;

@Controller
@RequiredArgsConstructor
public class InteraccionController {

  private static final String VISTA_FORM = "interaccion/form";

  private final InteraccionService interaccionService;
  private final LineaService lineaService;
  private final UsuarioService usuarioService;

  @GetMapping("/reportes/nuevo")
  public String nuevo(Model model) {
    model.addAttribute("interaccionForm", new InteraccionForm());
    return prepararFormulario(model);
  }

  @PostMapping("/reportes")
  public String crear(
      @Valid @ModelAttribute("interaccionForm") InteraccionForm form,
      BindingResult result,
      @AuthenticationPrincipal UserDetails principal,
      Model model,
      RedirectAttributes redirect) {
    if (result.hasErrors()) {
      return prepararFormulario(model);
    }

    Usuario usuario = usuarioService.buscarPorCorreo(principal.getUsername());
    try {
      interaccionService.crear(form, usuario);
    } catch (NotFoundException ex) {
      result.rejectValue("lineaId", "inactiva", "Selecciona una línea activa.");
      return prepararFormulario(model);
    }

    redirect.addFlashAttribute("exito", "El reporte se envió y quedó pendiente de revisión.");
    return "redirect:/reportes";
  }

  @GetMapping("/reportes")
  public String misReportes(
      @AuthenticationPrincipal UserDetails principal, Model model) {
    Usuario usuario = usuarioService.buscarPorCorreo(principal.getUsername());
    model.addAttribute("reportes", interaccionService.listarPropias(usuario.getId()));
    return "interaccion/mis-reportes";
  }

  @GetMapping("/reportes/{id}")
  public String verReporte(
      @PathVariable Long id,
      @AuthenticationPrincipal UserDetails principal,
      Model model) {
    Usuario usuario = usuarioService.buscarPorCorreo(principal.getUsername());
    model.addAttribute("reporte", interaccionService.buscarPropia(id, usuario.getId()));
    return "interaccion/detalle";
  }

  @GetMapping("/admin/reportes")
  public String administrar(
      @RequestParam(name = "tipo", required = false) TipoInteraccion tipo,
      @RequestParam(name = "lineaId", required = false) Long lineaId,
      @RequestParam(name = "estado", required = false) EstadoInteraccion estado,
      Model model) {
    model.addAttribute("reportes",
        interaccionService.listarParaAdministracion(tipo, lineaId, estado));
    model.addAttribute("tipos", TipoInteraccion.values());
    model.addAttribute("estados", EstadoInteraccion.values());
    model.addAttribute("lineas", lineaService.listarTodas());
    model.addAttribute("tipoSeleccionado", tipo);
    model.addAttribute("lineaSeleccionada", lineaId);
    model.addAttribute("estadoSeleccionado", estado);
    return "interaccion/admin";
  }

  @PostMapping("/admin/reportes/{id}/revisar")
  public String marcarRevisada(@PathVariable Long id, RedirectAttributes redirect) {
    interaccionService.marcarRevisada(id);
    redirect.addFlashAttribute("exito", "El reporte quedó marcado como revisado.");
    return "redirect:/admin/reportes";
  }

  private String prepararFormulario(Model model) {
    model.addAttribute("lineas", lineaService.listarActivas());
    model.addAttribute("tipos", TipoInteraccion.values());
    return VISTA_FORM;
  }
}
