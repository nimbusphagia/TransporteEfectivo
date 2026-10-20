package pe.cibertec.transportes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pe.cibertec.transportes.dto.LineaForm;
import pe.cibertec.transportes.entity.LineaTransporte;
import pe.cibertec.transportes.exception.BadRequestException;
import pe.cibertec.transportes.exception.ConflictException;
import pe.cibertec.transportes.service.LineaService;

@Controller
@RequiredArgsConstructor
public class LineaController {
  private static final String VISTA_FORM = "linea/form";
  private static final String REDIRECT_ADMIN = "redirect:/admin/lineas";

  private final LineaService lineaService;

  @GetMapping("/lineas")
  public String listar(Model model) {
    var lineas = lineaService.listarActivas();
    model.addAttribute("lineas", lineas);
    model.addAttribute("imagenesPorLinea", LineaImagenes.porLinea(lineas));
    return "linea/lista";
  }

  @GetMapping("/admin/lineas")
  public String gestionar(Model model) {
    model.addAttribute("lineas", lineaService.listarTodas());
    return "linea/admin";
  }

  @GetMapping("/admin/lineas/nueva")
  public String nueva(Model model) {
    model.addAttribute("lineaForm", new LineaForm());
    return VISTA_FORM;
  }

  @GetMapping("/admin/lineas/{id}/editar")
  public String editar(@PathVariable Long id, Model model) {
    model.addAttribute("lineaForm", LineaForm.desde(lineaService.buscarPorId(id)));
    return VISTA_FORM;
  }

  @PostMapping("/admin/lineas/guardar")
  public String guardar(@Valid @ModelAttribute("lineaForm") LineaForm form,
      BindingResult result, RedirectAttributes redirect) {
    if (result.hasErrors()) {
      return VISTA_FORM;
    }
    try {
      LineaTransporte linea = lineaService.guardar(form);
      redirect.addFlashAttribute("exito", form.esNueva()
          ? "Línea «" + linea.getNombre() + "» creada correctamente."
          : "Línea «" + linea.getNombre() + "» actualizada correctamente.");
      return REDIRECT_ADMIN;
    } catch (ConflictException ex) {
      result.rejectValue("nombre", "duplicado", ex.getMessage());
      return VISTA_FORM;
    } catch (BadRequestException ex) {
      result.rejectValue("costoPasaje", "invalido", ex.getMessage());
      return VISTA_FORM;
    }
  }

  @PostMapping("/admin/lineas/{id}/desactivar")
  public String desactivar(@PathVariable Long id, RedirectAttributes redirect) {
    LineaTransporte linea = lineaService.cambiarEstado(id, false);
    redirect.addFlashAttribute("exito",
        "Línea «" + linea.getNombre() + "» desactivada. Ya no se mostrará al público.");
    return REDIRECT_ADMIN;
  }

  @PostMapping("/admin/lineas/{id}/activar")
  public String activar(@PathVariable Long id, RedirectAttributes redirect) {
    LineaTransporte linea = lineaService.cambiarEstado(id, true);
    redirect.addFlashAttribute("exito", "Línea «" + linea.getNombre() + "» activada nuevamente.");
    return REDIRECT_ADMIN;
  }

}
