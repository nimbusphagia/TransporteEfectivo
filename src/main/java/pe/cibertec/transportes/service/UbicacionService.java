package pe.cibertec.transportes.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.cibertec.transportes.dto.LineaEnPunto;
import pe.cibertec.transportes.dto.PuntoCercano;
import pe.cibertec.transportes.entity.Paradero;
import pe.cibertec.transportes.exception.BadRequestException;
import pe.cibertec.transportes.util.GeoUtils;

@Service
@Transactional(readOnly = true)
public class UbicacionService {
    public static final int MAX_RESULTADOS = 5;
    public static final double RADIO_AGRUPACION_METROS = 150;
    public static final double RADIO_MAXIMO_METROS = 5_000;

    private final LineaService lineaService;
    private final ParaderoService paraderoService;

    public UbicacionService(LineaService lineaService, ParaderoService paraderoService) {
        this.lineaService = lineaService;
        this.paraderoService = paraderoService;
    }

    public List<Paradero> paraderosConCoordenadas() {
        return lineaService.listarActivas().stream()
                .flatMap(linea -> paraderoService.listarActivosPorLinea(linea.getId()).stream())
                .filter(p -> p.getLatitud() != null && p.getLongitud() != null)
                .toList();
    }

    public List<PuntoCercano> buscarCercanos(double latitud, double longitud) {
        if (!GeoUtils.coordenadasValidas(latitud, longitud)) {
            throw new BadRequestException("La ubicación indicada no es válida.");
        }

        List<Candidato> ordenados = paraderosConCoordenadas().stream()
                .map(p -> new Candidato(p,
                        GeoUtils.distanciaMetros(latitud, longitud, p.getLatitud(), p.getLongitud())))
                .filter(c -> c.distancia() <= RADIO_MAXIMO_METROS)
                .sorted(Comparator.comparingDouble(Candidato::distancia))
                .toList();

        List<Grupo> grupos = new ArrayList<>();
        for (Candidato candidato : ordenados) {
            Grupo cercano = grupos.stream().filter(g -> g.estaCerca(candidato)).findFirst().orElse(null);
            if (cercano != null) {
                cercano.agregarSiLineaNueva(candidato);
            } else if (grupos.size() < MAX_RESULTADOS) {
                grupos.add(new Grupo(candidato));
            }
        }
        return grupos.stream().map(Grupo::aPunto).toList();
    }

    private record Candidato(Paradero paradero, double distancia) {
    }

    private static final class Grupo {
        private final Candidato ancla;
        private final List<Candidato> miembros = new ArrayList<>();

        Grupo(Candidato ancla) {
            this.ancla = ancla;
            this.miembros.add(ancla);
        }

        boolean estaCerca(Candidato otro) {
            return GeoUtils.distanciaMetros(
                    ancla.paradero().getLatitud(), ancla.paradero().getLongitud(),
                    otro.paradero().getLatitud(), otro.paradero().getLongitud()) <= RADIO_AGRUPACION_METROS;
        }

        void agregarSiLineaNueva(Candidato candidato) {
            Long lineaId = candidato.paradero().getLinea().getId();
            boolean repetida = miembros.stream()
                    .anyMatch(m -> m.paradero().getLinea().getId().equals(lineaId));
            if (!repetida) {
                miembros.add(candidato);
            }
        }

        PuntoCercano aPunto() {
            List<LineaEnPunto> lineas = miembros.stream().map(m -> {
                Paradero p = m.paradero();
                return new LineaEnPunto(p.getLinea().getId(), p.getLinea().getNombre(),
                        p.getLinea().getCostoPasaje(), p.getId(), p.getNombre(), p.getOrden());
            }).toList();
            return new PuntoCercano(ancla.paradero().getNombre(), ancla.paradero().getLatitud(),
                    ancla.paradero().getLongitud(), ancla.distancia(), lineas);
        }
    }
}