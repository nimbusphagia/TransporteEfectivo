package pe.cibertec.transportes.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.cibertec.transportes.entity.LineaTransporte;
import pe.cibertec.transportes.entity.Paradero;
import pe.cibertec.transportes.repository.LineaTransporteRepository;
import pe.cibertec.transportes.repository.ParaderoRepository;

@Slf4j
@Component
@Order(20)
@RequiredArgsConstructor
public class LineasParaderosDemoData implements CommandLineRunner {

  private record Punto(String nombre, double latitud, double longitud) {}

  private record LineaDemo(String nombre, String costo, String imagen, List<Punto> paraderos) {}

  private static final List<LineaDemo> LINEAS = List.of(
          new LineaDemo("Metropolitano", "2.50", "/img/metropolitano.jpg", List.of(
                  p("Estación Naranjal", -11.9960, -77.0610),
                  p("Estación Tomás Valle", -12.0138, -77.0562),
                  p("Estación UNI", -12.0235, -77.0500),
                  p("Estación Caquetá", -12.0370, -77.0440),
                  p("Estación Central", -12.0615, -77.0365),
                  p("Estación Estadio Nacional", -12.0686, -77.0343),
                  p("Estación Javier Prado", -12.0905, -77.0285),
                  p("Estación Angamos", -12.1124, -77.0236),
                  p("Estación Ricardo Palma (Miraflores)", -12.1254, -77.0237),
                  p("Estación Plaza de Flores", -12.1445, -77.0210))),

          new LineaDemo("Corredor Azul", "1.50", "/img/linea_azul.jpg", List.of(
                  p("Av. Tacna / Jr. Ica", -12.0535, -77.0385),
                  p("Av. Garcilaso de la Vega", -12.0630, -77.0385),
                  p("Av. Arequipa / Av. Petit Thouars", -12.0745, -77.0365),
                  p("Av. Arequipa / Av. Javier Prado", -12.0920, -77.0335),
                  p("Av. Arequipa / Av. Angamos", -12.1115, -77.0322),
                  p("Óvalo de Miraflores", -12.1160, -77.0312),
                  p("Av. Larco / Parque Kennedy", -12.1219, -77.0300))),

          new LineaDemo("El Rápido", "2.00", "/img/rapido.jpg", List.of(
                  p("Plaza Norte", -12.0069, -77.0585),
                  p("Av. Túpac Amaru / Av. Tomás Valle", -12.0140, -77.0560),
                  p("Av. Caquetá", -12.0345, -77.0450),
                  p("Plaza Dos de Mayo", -12.0568, -77.0405),
                  p("Av. Abancay / Parque Universitario", -12.0560, -77.0270),
                  p("Av. Grau", -12.0625, -77.0235))),

          new LineaDemo("Corredor Rojo", "1.80", null, List.of(
                  p("Av. La Marina / Av. Faucett", -12.0680, -77.0990),
                  p("Plaza San Miguel", -12.0770, -77.0827),
                  p("Av. Javier Prado / Av. Salaverry", -12.0912, -77.0480),
                  p("Av. Javier Prado / Av. Arequipa", -12.0921, -77.0336),
                  p("Av. Javier Prado / Av. Aviación", -12.0915, -77.0060),
                  p("Óvalo Monitor", -12.0930, -77.0010))),

          new LineaDemo("Corredor Morado", "2.20", null, List.of(
                  p("Av. Próceres de la Independencia / Bayóvar", -11.9870, -76.9940),
                  p("Av. Próceres / Av. Los Jardines", -11.9995, -77.0005),
                  p("Av. Próceres / Av. Wiesse", -12.0120, -77.0020),
                  p("Puente Nuevo", -12.0450, -77.0150),
                  p("Av. Abancay", -12.0510, -77.0285),
                  p("Av. Arequipa / Miraflores", -12.1190, -77.0315))));

  private final LineaTransporteRepository lineaRepository;
  private final ParaderoRepository paraderoRepository;

  private static Punto p(String nombre, double latitud, double longitud) {
    return new Punto(nombre, latitud, longitud);
  }

  @Override
  @Transactional
  public void run(String... args) {
    if (lineaRepository.count() == 0) {
      LINEAS.forEach(this::crear);
      log.info("Datos de prueba: {} líneas con sus paraderos cargadas.", LINEAS.size());
      return;
    }
    int completados = completarCoordenadas();
    if (completados > 0) {
      log.info("Datos de prueba: coordenadas agregadas a {} paraderos.", completados);
    }
  }

  private void crear(LineaDemo demo) {
    LineaTransporte linea = new LineaTransporte();
    linea.setNombre(demo.nombre());
    linea.setCostoPasaje(new BigDecimal(demo.costo()));
    linea.setImagenUrl(demo.imagen());
    lineaRepository.save(linea);

    for (int i = 0; i < demo.paraderos().size(); i++) {
      Punto punto = demo.paraderos().get(i);
      Paradero paradero = new Paradero();
      paradero.setNombre(punto.nombre());
      paradero.setOrden(i + 1);
      paradero.setLatitud(punto.latitud());
      paradero.setLongitud(punto.longitud());
      paradero.setLinea(linea);
      paraderoRepository.save(paradero);
    }
  }

  private int completarCoordenadas() {
    int completados = 0;
    for (LineaTransporte linea : lineaRepository.findAll()) {
      LineaDemo demo = LINEAS.stream()
              .filter(l -> l.nombre().equalsIgnoreCase(linea.getNombre()))
              .findFirst()
              .orElse(null);
      if (demo == null) {
        continue;
      }
      for (Paradero paradero : paraderoRepository.findByLineaIdOrderByActivoDescOrdenAsc(linea.getId())) {
        if (paradero.getLatitud() != null || paradero.getLongitud() != null) {
          continue;
        }
        int indice = paradero.getOrden() - 1;
        if (indice < 0 || indice >= demo.paraderos().size()) {
          continue;
        }
        Punto punto = demo.paraderos().get(indice);
        if (!punto.nombre().equals(paradero.getNombre())) {
          continue;
        }
        paradero.setLatitud(punto.latitud());
        paradero.setLongitud(punto.longitud());
        paraderoRepository.save(paradero);
        completados++;
      }
    }
    return completados;
  }
}