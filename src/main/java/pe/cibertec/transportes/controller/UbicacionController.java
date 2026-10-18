package pe.cibertec.transportes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import pe.cibertec.transportes.exception.BadRequestException;
import pe.cibertec.transportes.service.UbicacionService;

@Controller
@RequiredArgsConstructor
public class UbicacionController {
    private final UbicacionService ubicacionService;

    @GetMapping("/ubicacion")
    public String ubicacion(@RequestParam(required = false) Double lat,
                            @RequestParam(required = false) Double lng, Model model) {
        if ((lat == null) != (lng == null)) {
            throw new BadRequestException("Indica la latitud y la longitud juntas.");
        }
        if (lat != null) {
            model.addAttribute("lat", lat);
            model.addAttribute("lng", lng);
            model.addAttribute("puntos", ubicacionService.buscarCercanos(lat, lng));
        }
        model.addAttribute("radioMaximoKm", (int) (UbicacionService.RADIO_MAXIMO_METROS / 1000));
        return "ubicacion/index";
    }
}