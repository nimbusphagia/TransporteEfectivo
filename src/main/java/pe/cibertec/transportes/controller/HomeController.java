package pe.cibertec.transportes.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import pe.cibertec.transportes.entity.AlertaTrafico;
import pe.cibertec.transportes.entity.LineaTransporte;
import pe.cibertec.transportes.service.AlertaService;
import pe.cibertec.transportes.service.LineaService;
import pe.cibertec.transportes.service.ParaderoService;

@Controller
@RequiredArgsConstructor
public class HomeController {

  private final ParaderoService paraderoService;
  private final LineaService lineaService;
  private final AlertaService alertaService;

  @GetMapping("/")
  public String inicio(Model model) {
    cargarDatosComunes(model);
    return "home/index";
  }

  @GetMapping("/buscar")
  public String buscar(@RequestParam(name = "q", required = false) String q, Model model) {
    cargarDatosComunes(model);

    if (q != null && !q.trim().isEmpty()) {
      String termino = q.trim();
      model.addAttribute("query", termino);
      model.addAttribute("lineasEncontradas", lineaService.buscarActivasPorNombre(termino));
      model.addAttribute("resultadosBusqueda", paraderoService.buscarPorNombre(termino));
    }

    return "home/index";
  }

  private void cargarDatosComunes(Model model) {
    List<LineaTransporte> lineas = lineaService.listarActivas();
    model.addAttribute("lineas", lineas);
    model.addAttribute("imagenesPorLinea", LineaImagenes.porLinea(lineas));
    model.addAttribute("alerta", alertaService.ultimaActiva());
  }

}