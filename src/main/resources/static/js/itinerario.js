(function () {
    "use strict";

    // Mismo proveedor que ubicacion.js: si cambian de proveedor, cámbienlo en ambos archivos.
    var URL_TILES = "https://tile.openstreetmap.org/{z}/{x}/{y}.png";
    var ATRIBUCION =
        '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors';

    var contenedor = document.getElementById("mapa-itinerario");
    if (!contenedor) {
        return;
    }
    if (typeof L === "undefined") {
        contenedor.hidden = true; // la tabla del itinerario sigue disponible
        return;
    }

    var paradas = [];
    document.querySelectorAll("tr[data-lat]").forEach(function (fila) {
        var lat = parseFloat(fila.dataset.lat);
        var lng = parseFloat(fila.dataset.lng);
        if (isNaN(lat) || isNaN(lng)) {
            return; // paradero sin ubicación
        }
        paradas.push({
            fila: fila,
            posicion: [lat, lng],
            orden: parseInt(fila.dataset.orden, 10),
            nombre: fila.dataset.nombre
        });
    });

    if (paradas.length === 0) {
        contenedor.hidden = true;
        return;
    }

    var mapa = L.map(contenedor);
    L.tileLayer(URL_TILES, { maxZoom: 19, attribution: ATRIBUCION }).addTo(mapa);

    var posiciones = paradas.map(function (p) {
        return p.posicion;
    });

    if (posiciones.length > 1) {
        L.polyline(posiciones, { className: "ruta", interactive: false }).addTo(mapa);
    }

    function activar(filaActiva) {
        paradas.forEach(function (p) {
            p.fila.classList.toggle("tabla__fila--activa", p.fila === filaActiva);
        });
    }

    paradas.forEach(function (parada) {
        var titulo = document.createElement("strong");
        titulo.textContent = parada.orden + ". " + parada.nombre;

        var marcador = L.marker(parada.posicion, {
            icon: L.divIcon({
                className: "marcador",
                html: String(parada.orden),
                iconSize: [28, 28]
            }),
            title: parada.nombre
        })
            .bindPopup(titulo)
            .addTo(mapa);

        marcador.on("click", function () {
            activar(parada.fila);
        });
        parada.fila.addEventListener("click", function () {
            activar(parada.fila);
            mapa.setView(parada.posicion, 17);
            marcador.openPopup();
            contenedor.scrollIntoView({ block: "nearest" });
        });
    });

    if (posiciones.length > 1) {
        mapa.fitBounds(posiciones, { padding: [40, 40] });
    } else {
        mapa.setView(posiciones[0], 16);
    }
})();