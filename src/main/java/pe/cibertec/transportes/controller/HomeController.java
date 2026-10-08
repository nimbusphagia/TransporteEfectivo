package pe.cibertec.transportes.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pe.cibertec.transportes.entity.AlertaTrafico;
import pe.cibertec.transportes.entity.LineaTransporte;

@Controller
public class HomeController {

  @GetMapping("/")
  public String inicio(Model model) {
    model.addAttribute("lineas", lineasDeEjemplo());
    model.addAttribute("alerta", alertaDeEjemplo());
    return "home/index";
  }

  // mockup data
  private List<LineaTransporte> lineasDeEjemplo() {
    return List.of(
        linea(1L, "Línea Azul", "1.50"),
        linea(2L, "El Rápido", "2.00"),
        linea(3L, "Metropolitano", "2.50"),
        linea(4L, "Corredor Rojo", "1.80"),
        linea(5L, "Corredor Morado", "2.20"),
        linea(6L, "Corredor Verde", "1.50"),
        linea(7L, "Expreso Sur", "3.00"),
        linea(8L, "Circuito Costa Verde", "2.00"));
  }

  private LineaTransporte linea(Long id, String nombre, String costo) {
    LineaTransporte linea = new LineaTransporte();
    linea.setId(id);
    linea.setNombre(nombre);
    linea.setCostoPasaje(new BigDecimal(costo));
    return linea;
  }

  private AlertaTrafico alertaDeEjemplo() {
    AlertaTrafico alerta = new AlertaTrafico();
    alerta.setTitulo("Congestión en Av. Javier Prado");
    alerta.setDescripcion("Tráfico lento por mantenimiento de la vía.");
    return alerta;
  }
}
