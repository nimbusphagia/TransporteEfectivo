import {
    hayLeaflet,
    crearMapa,
    iconoMarcador,
    crearPopupTexto,
    marcarActivo,
} from "./mapa-base.js";

function leerParadas() {
    const paradas = [];
    for (const fila of document.querySelectorAll("tr[data-lat]")) {
        const lat = Number.parseFloat(fila.dataset.lat);
        const lng = Number.parseFloat(fila.dataset.lng);
        if (Number.isNaN(lat) || Number.isNaN(lng)) {
            continue; // paradero sin ubicación
        }
        paradas.push({
            fila,
            posicion: [lat, lng],
            orden: Number.parseInt(fila.dataset.orden, 10),
            nombre: fila.dataset.nombre,
        });
    }
    return paradas;
}

function modificadorDe(indice, total) {
    if (total < 2) return "";
    if (indice === 0) return "marcador--inicio";
    if (indice === total - 1) return "marcador--fin";
    return "";
}

function iniciar() {
    const contenedor = document.getElementById("mapa-itinerario");
    if (!contenedor) return;

    const paradas = leerParadas();
    if (!hayLeaflet() || paradas.length === 0) {
        contenedor.hidden = true; // la tabla del itinerario sigue disponible
        return;
    }

    const mapa = crearMapa(contenedor);
    const posiciones = paradas.map(({ posicion }) => posicion);

    if (posiciones.length > 1) {
        L.polyline(posiciones, { className: "ruta-borde", interactive: false }).addTo(mapa);
        L.polyline(posiciones, { className: "ruta", interactive: false }).addTo(mapa);
    }

    let activa = null;
    const activar = (fila, marcador) => {
        if (activa) {
            activa.fila.classList.remove("tabla__fila--activa");
            marcarActivo(activa.marcador, false);
        }
        fila.classList.add("tabla__fila--activa");
        marcarActivo(marcador, true);
        activa = { fila, marcador };
    };

    paradas.forEach((parada, i) => {
        const marcador = L.marker(parada.posicion, {
            icon: iconoMarcador(parada.orden, modificadorDe(i, paradas.length)),
            title: parada.nombre,
        })
            .bindPopup(crearPopupTexto(`${parada.orden}. ${parada.nombre}`))
            .addTo(mapa);

        const seleccionar = () => {
            activar(parada.fila, marcador);
            mapa.setView(parada.posicion, 17);
            marcador.openPopup();
            contenedor.scrollIntoView({ block: "nearest" });
        };

        marcador.on("click", () => activar(parada.fila, marcador));
        parada.fila.addEventListener("click", seleccionar);
        parada.fila.addEventListener("keydown", (evento) => {
            if (evento.key === "Enter" || evento.key === " ") {
                evento.preventDefault();
                seleccionar();
            }
        });
    });

    if (posiciones.length > 1) {
        mapa.fitBounds(posiciones, { padding: [40, 40] });
    } else {
        mapa.setView(posiciones[0], 16);
    }
}

iniciar();