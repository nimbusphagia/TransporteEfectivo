package pe.cibertec.transportes.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import pe.cibertec.transportes.entity.LineaTransporte;

class LineaImagenesTest {

  @Test
  void resuelveImagenesPorNombreAunqueTenganTildes() {
    assertEquals("/img/rojo.png", LineaImagenes.resolver(linea("El Rápido", null)));
    assertEquals("/img/azul.png", LineaImagenes.resolver(linea("Línea Azul", null)));
    assertEquals("/img/costaverde.png",
        LineaImagenes.resolver(linea("Circuito Costa Verde", null)));
  }

  @Test
  void reemplazaRutasDeImagenesDeDemostracionAnteriores() {
    assertEquals("/img/metrop.png",
        LineaImagenes.resolver(linea("Metropolitano", "/img/metropolitano.jpg")));
  }

  @Test
  void conservaImagenPersonalizadaYUsaPredeterminadaParaNombresDesconocidos() {
    assertEquals("/img/personalizada.png",
        LineaImagenes.resolver(linea("Línea nueva", "/img/personalizada.png")));
    assertEquals("/img/linea-default.svg", LineaImagenes.resolver(linea("Línea nueva", null)));
  }

  private LineaTransporte linea(String nombre, String imagenUrl) {
    LineaTransporte linea = new LineaTransporte();
    linea.setNombre(nombre);
    linea.setImagenUrl(imagenUrl);
    return linea;
  }
}
