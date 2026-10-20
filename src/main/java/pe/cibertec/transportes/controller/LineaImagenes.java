package pe.cibertec.transportes.controller;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import pe.cibertec.transportes.entity.LineaTransporte;

final class LineaImagenes {

  private static final Map<String, String> POR_NOMBRE = Map.of(
      "linea azul", "/img/azul.png",
      "corredor azul", "/img/azul.png",
      "el rapido", "/img/rojo.png",
      "metropolitano", "/img/metrop.png",
      "corredor rojo", "/img/corredorojo.png",
      "corredor morado", "/img/morado.png",
      "corredor verde", "/img/verde.png",
      "expreso sur", "/img/expresour.png",
      "circuito costa verde", "/img/costaverde.png");

  private static final Map<String, String> IMAGENES_DEMO_ANTERIORES = Map.of(
      "/img/linea_azul.jpg", "/img/azul.png",
      "/img/rapido.jpg", "/img/rojo.png",
      "/img/metropolitano.jpg", "/img/metrop.png");

  private LineaImagenes() {
  }

  static Map<Long, String> porLinea(List<LineaTransporte> lineas) {
    return lineas.stream().collect(Collectors.toMap(
        LineaTransporte::getId,
        LineaImagenes::resolver));
  }

  static String resolver(LineaTransporte linea) {
    String imagenGuardada = linea.getImagenUrl();
    if (imagenGuardada != null && !imagenGuardada.isBlank()) {
      String anterior = IMAGENES_DEMO_ANTERIORES.get(imagenGuardada.trim());
      return anterior == null ? imagenGuardada.trim() : anterior;
    }

    String nombreNormalizado = Normalizer.normalize(linea.getNombre(), Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "")
        .toLowerCase(Locale.ROOT)
        .trim();
    return POR_NOMBRE.getOrDefault(nombreNormalizado, "/img/linea-default.svg");
  }
}
