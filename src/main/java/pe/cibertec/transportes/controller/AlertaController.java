package pe.cibertec.transportes.controller;

import pe.cibertec.transportes.entity.AlertaTrafico;
import pe.cibertec.transportes.service.AlertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AlertaController {

    @Autowired
    private AlertaService alertaService;

    @GetMapping("/alertas")
    public String listar(Model model) {
        model.addAttribute("alertas", alertaService.listarTodas());
        return "alerta/lista";
    }

    @GetMapping("/admin/alertas")
    public String gestionar(Model model) {
        model.addAttribute("alertas", alertaService.listarTodas());
        model.addAttribute("alerta", new AlertaTrafico());
        return "alerta/form";
    }

    @PostMapping("/admin/alertas/guardar")
    public String guardar(@ModelAttribute AlertaTrafico alerta) {
        alertaService.guardar(alerta);
        return "redirect:/admin/alertas";
    }

    @DeleteMapping("/admin/alertas/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        alertaService.eliminar(id);
        return "redirect:/admin/alertas";
    }
}