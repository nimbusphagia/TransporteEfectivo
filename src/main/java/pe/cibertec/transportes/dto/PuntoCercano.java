package pe.cibertec.transportes.dto;

import java.util.List;
import java.util.Locale;

public record PuntoCercano(
        String nombre,
        double latitud,
        double longitud,
        double distanciaMetros,
        List<LineaEnPunto> lineas) {

    public String distanciaTexto() {
        if (distanciaMetros < 1000) {
            long metros = Math.max(10, Math.round(distanciaMetros / 10.0) * 10);
            return metros + " m";
        }
        return String.format(Locale.US, "%.1f km", distanciaMetros / 1000.0);
    }
}