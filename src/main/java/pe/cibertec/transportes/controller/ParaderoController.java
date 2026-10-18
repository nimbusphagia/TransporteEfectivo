package pe.cibertec.transportes.controller;

import java.util.List;

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
import pe.cibertec.transportes.dto.ParaderoForm;
import pe.cibertec.transportes.entity.LineaTransporte;
import pe.cibertec.transportes.entity.Paradero;
import pe.cibertec.transportes.exception.ConflictException;
import pe.cibertec.transportes.exception.NotFoundException;
import pe.cibertec.transportes.service.LineaService;
import pe.cibertec.transportes.service.ParaderoService;

@Controller
@RequiredArgsConstructor
public class ParaderoController {
  private static final String VISTA_FORM = "paradero/form";

  private final ParaderoService paraderoService;
  private final LineaService lineaService;

  @GetMapping("/lineas/{id}/paraderos")
  public String itinerario(@PathVariable Long id, Model model) {
    model.addAttribute("linea", lineaService.buscarActivaPorId(id));
    model.addAttribute("paraderos", paraderoService.listarActivosPorLinea(id));
    return "paradero/lista";
  }

  @GetMapping("/admin/paraderos")
  public String gestionar(@RequestParam(required = false) Long lineaId, Model model) {
    List<LineaTransporte> lineas = lineaService.listarTodas();
    model.addAttribute("lineas", lineas);

    LineaTransporte seleccionada = null;
    if (lineaId != null) {
      seleccionada = lineaService.buscarPorId(lineaId);
    } else if (!lineas.isEmpty()) {
      seleccionada = lineas.stream().filter(LineaTransporte::isActivo).findFirst()
          .orElse(lineas.get(0));
    }

    model.addAttribute("lineaSeleccionada", seleccionada);
    model.addAttribute("paraderos",
        seleccionada == null ? List.of() : paraderoService.listarPorLinea(seleccionada.getId()));
    return "paradero/admin";
  }

  @GetMapping("/admin/paraderos/nuevo")
  public String nuevo(@RequestParam(required = false) Long lineaId, Model model) {
    ParaderoForm form = new ParaderoForm();
    if (lineaId != null) {
      form.setLineaId(lineaId);
      form.setOrden(paraderoService.siguienteOrden(lineaId));
    }
    model.addAttribute("paraderoForm", form);
    return prepararFormulario(model);
  }

  @GetMapping("/admin/paraderos/{id}/editar")
  public String editar(@PathVariable Long id, Model model) {
    model.addAttribute("paraderoForm", ParaderoForm.desde(paraderoService.buscarPorId(id)));
    return prepararFormulario(model);
  }

  @PostMapping("/admin/paraderos/guardar")
  public String guardar(@Valid @ModelAttribute("paraderoForm") ParaderoForm form,
      BindingResult result, Model model, RedirectAttributes redirect) {
    if (result.hasErrors()) {
      return prepararFormulario(model);
    }
    try {
      Paradero paradero = paraderoService.guardar(form);
      redirect.addFlashAttribute("exito", form.esNuevo()
          ? "Paradero «" + paradero.getNombre() + "» agregado correctamente."
          : "Paradero «" + paradero.getNombre() + "» actualizado correctamente.");
      return redirectAdmin(paradero);
    } catch (ConflictException ex) {
      result.rejectValue("orden", "ocupada", ex.getMessage());
      return prepararFormulario(model);
    } catch (NotFoundException ex) {
      result.rejectValue("lineaId", "inexistente", "Selecciona una línea activa.");
      return prepararFormulario(model);
    }
  }

  @PostMapping("/admin/paraderos/{id}/desactivar")
  public String desactivar(@PathVariable Long id, RedirectAttributes redirect) {
    Paradero paradero = paraderoService.desactivar(id);
    redirect.addFlashAttribute("exito", "Paradero «" + paradero.getNombre() + "» desactivado.");
    return redirectAdmin(paradero);
  }

  @PostMapping("/admin/paraderos/{id}/activar")
  public String activar(@PathVariable Long id, RedirectAttributes redirect) {
    Paradero paradero = paraderoService.buscarPorId(id);
    try {
      paraderoService.activar(id);
      redirect.addFlashAttribute("exito", "Paradero «" + paradero.getNombre() + "» activado.");
    } catch (ConflictException ex) {
      redirect.addFlashAttribute("error",
          ex.getMessage() + " Edita su posición antes de activarlo.");
    }
    return redirectAdmin(paradero);
  }

  @PostMapping("/admin/paraderos/{id}/subir")
  public String subir(@PathVariable Long id, RedirectAttributes redirect) {
    Paradero paradero = paraderoService.subir(id);
    redirect.addFlashAttribute("exito", "Orden actualizado.");
    return redirectAdmin(paradero);
  }

  @PostMapping("/admin/paraderos/{id}/bajar")
  public String bajar(@PathVariable Long id, RedirectAttributes redirect) {
    Paradero paradero = paraderoService.bajar(id);
    redirect.addFlashAttribute("exito", "Orden actualizado.");
    return redirectAdmin(paradero);
  }

  private String prepararFormulario(Model model) {
    model.addAttribute("lineas", lineaService.listarActivas());
    return VISTA_FORM;
  }

  private String redirectAdmin(Paradero paradero) {
    return "redirect:/admin/paraderos?lineaId=" + paradero.getLinea().getId();
  }
}
