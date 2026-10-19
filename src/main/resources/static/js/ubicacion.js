import {
    hayLeaflet,
    crearMapa,
    iconoMarcador,
    crearPopupTexto,
    marcarActivo,
} from "./mapa-base.js";

const CENTRO_LIMA = [-12.0464, -77.0428];
const TAMANO_USUARIO = 22;

const boton = document.getElementById("btn-ubicacion");
const estado = document.getElementById("estado-ubicacion");
const aviso = document.getElementById("aviso-ubicacion");
const contenedorMapa = document.getElementById("mapa");

const mostrarAviso = (texto) => {
    aviso.textContent = texto;
    aviso.hidden = false;
};

const ocultarAviso = () => {
    aviso.textContent = "";
    aviso.hidden = true;
};

const irA = (latitud, longitud) => {
    window.location.href = `${window.location.pathname}?lat=${latitud.toFixed(4)}&lng=${longitud.toFixed(4)}`;
};

function mensajeDeError(error) {
    switch (error.code) {
        case error.PERMISSION_DENIED:
            return "No diste permiso para usar tu ubicación. Puedes tocar el mapa para elegir un punto.";
        case error.TIMEOUT:
            return "Tardamos demasiado en obtener tu ubicación. Inténtalo de nuevo o toca el mapa.";
        default:
            return "No pudimos determinar tu ubicación. Toca el mapa para elegir un punto.";
    }
}

function pedirUbicacion() {
    ocultarAviso();

    if (!window.isSecureContext) {
        mostrarAviso(
            "La ubicación solo funciona en una conexión segura (HTTPS) o en localhost. " +
            "Mientras tanto, toca el mapa para elegir un punto."
        );
        return;
    }
    if (!("geolocation" in navigator)) {
        mostrarAviso("Tu navegador no permite obtener la ubicación. Toca el mapa para elegir un punto.");
        return;
    }

    boton.disabled = true;
    estado.textContent = "Buscando tu ubicación…";

    navigator.geolocation.getCurrentPosition(
        ({ coords }) => irA(coords.latitude, coords.longitude),
        (error) => {
            boton.disabled = false;
            estado.textContent = "";
            mostrarAviso(mensajeDeError(error));
        },
        { enableHighAccuracy: true, timeout: 15000, maximumAge: 30000 }
    );
}

function crearPopupBuscar(latlng) {
    const contenido = document.createElement("div");
    const texto = document.createElement("p");
    texto.textContent = "¿Buscar paraderos desde este punto?";
    const aqui = document.createElement("button");
    aqui.type = "button";
    aqui.className = "btn btn--principal btn--peque";
    aqui.textContent = "Buscar aquí";
    aqui.addEventListener("click", () => irA(latlng.lat, latlng.lng));
    contenido.append(texto, aqui);
    return contenido;
}

function iniciarMapa() {
    if (!contenedorMapa) return;
    if (!hayLeaflet()) {
        mostrarAviso("No se pudo cargar el mapa. Los resultados siguen disponibles en la lista.");
        return;
    }

    const lat = Number.parseFloat(contenedorMapa.dataset.lat);
    const lng = Number.parseFloat(contenedorMapa.dataset.lng);
    const usuario = Number.isNaN(lat) || Number.isNaN(lng) ? null : [lat, lng];

    const mapa = crearMapa(contenedorMapa, usuario ?? CENTRO_LIMA, usuario ? 15 : 12);
    const limites = [];

    if (usuario) {
        L.marker(usuario, {
            icon: iconoMarcador("", "marcador--usuario", TAMANO_USUARIO),
            title: "Tu ubicación",
            keyboard: false,
        }).addTo(mapa);
        limites.push(usuario);
    }

    const trazo = usuario
        ? L.polyline([], { className: "ruta-usuario", interactive: false }).addTo(mapa)
        : null;

    let activo = null;
    const activar = (tarjeta, marcador, posicion) => {
        if (activo) {
            activo.tarjeta.classList.remove("punto--activo");
            marcarActivo(activo.marcador, false);
        }
        tarjeta.classList.add("punto--activo");
        marcarActivo(marcador, true);
        activo = { tarjeta, marcador };
        trazo?.setLatLngs([usuario, posicion]);
    };

    document.querySelectorAll(".punto").forEach((tarjeta, i) => {
        const posicion = [Number.parseFloat(tarjeta.dataset.lat), Number.parseFloat(tarjeta.dataset.lng)];
        const marcador = L.marker(posicion, {
            icon: iconoMarcador(i + 1),
            title: tarjeta.dataset.nombre,
        })
            .bindPopup(crearPopupTexto(tarjeta.dataset.nombre))
            .addTo(mapa);
        limites.push(posicion);

        marcador.on("click", () => {
            activar(tarjeta, marcador, posicion);
            tarjeta.scrollIntoView({ block: "nearest" });
        });
        tarjeta.addEventListener("click", (evento) => {
            if (evento.target.closest("a")) return; // los enlaces a la línea funcionan normalmente
            activar(tarjeta, marcador, posicion);
            mapa.setView(posicion, 17);
            marcador.openPopup();
        });
    });

    if (limites.length > 1) {
        mapa.fitBounds(limites, { padding: [40, 40], maxZoom: 16 });
    }

    mapa.on("click", ({ latlng }) => {
        L.popup().setLatLng(latlng).setContent(crearPopupBuscar(latlng)).openOn(mapa);
    });
}

boton?.addEventListener("click", pedirUbicacion);
iniciarMapa();