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
  private final LineaTransporteRepository lineaRepository;
  private final ParaderoRepository paraderoRepository;

  @Override
  @Transactional
  public void run(String... args) {
    if (lineaRepository.count() > 0) {
      return;
    }

    crear("Metropolitano", "2.50", "/img/metropolitano.jpg", List.of(
        "Estación Naranjal", "Estación Tomás Valle", "Estación UNI", "Estación Caquetá",
        "Estación Central", "Estación Estadio Nacional", "Estación Javier Prado",
        "Estación Angamos", "Estación Ricardo Palma (Miraflores)", "Estación Plaza de Flores"));

    crear("Corredor Azul", "1.50", "/img/linea_azul.jpg", List.of(
        "Av. Tacna / Jr. Ica", "Av. Garcilaso de la Vega", "Av. Arequipa / Av. Petit Thouars",
        "Av. Arequipa / Av. Javier Prado", "Av. Arequipa / Av. Angamos",
        "Óvalo de Miraflores", "Av. Larco / Parque Kennedy"));

    crear("El Rápido", "2.00", "/img/rapido.jpg", List.of(
        "Plaza Norte", "Av. Túpac Amaru / Av. Tomás Valle", "Av. Caquetá",
        "Plaza Dos de Mayo", "Av. Abancay / Parque Universitario", "Av. Grau"));

    crear("Corredor Rojo", "1.80", null, List.of(
        "Av. La Marina / Av. Faucett", "Plaza San Miguel", "Av. Javier Prado / Av. Salaverry",
        "Av. Javier Prado / Av. Arequipa", "Av. Javier Prado / Av. Aviación",
        "Óvalo Monitor"));

    crear("Corredor Morado", "2.20", null, List.of(
        "Av. Próceres de la Independencia / Bayóvar", "Av. Próceres / Av. Los Jardines",
        "Av. Próceres / Av. Wiesse", "Puente Nuevo", "Av. Abancay", "Av. Arequipa / Miraflores"));

    log.info("Datos de prueba: 5 líneas con sus paraderos cargadas.");
  }

  private void crear(String nombre, String costo, String imagen, List<String> paraderos) {
    LineaTransporte linea = new LineaTransporte();
    linea.setNombre(nombre);
    linea.setCostoPasaje(new BigDecimal(costo));
    linea.setImagenUrl(imagen);
    lineaRepository.save(linea);

    for (int i = 0; i < paraderos.size(); i++) {
      Paradero p = new Paradero();
      p.setNombre(paraderos.get(i));
      p.setOrden(i + 1);
      p.setLinea(linea);
      paraderoRepository.save(p);
    }
  }
}
