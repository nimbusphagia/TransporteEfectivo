import { hayLeaflet, crearMapa, iconoMarcador } from "./mapa-base.js";

const CENTRO_LIMA = [-12.0464, -77.0428];
const ZOOM_INICIAL = 12;
const ZOOM_PUNTO = 17;

const contenedor = document.getElementById("mapa-paradero");
const campoLat = document.getElementById("latitud");
const campoLng = document.getElementById("longitud");
const campoOrden = document.getElementById("orden");
const botonQuitar = document.getElementById("btn-quitar-ubicacion");

const leer = (campo) => {
    const valor = Number.parseFloat(campo.value);
    return Number.isNaN(valor) ? null : valor;
};

function posicionDeCampos() {
    const lat = leer(campoLat);
    const lng = leer(campoLng);
    if (lat === null || lng === null) return null;
    if (Math.abs(lat) > 90 || Math.abs(lng) > 180) return null;
    return [lat, lng];
}

const camposVacios = () => !campoLat.value.trim() && !campoLng.value.trim();

function iniciar() {
    if (!contenedor || !campoLat || !campoLng) return;
    if (!hayLeaflet()) {
        contenedor.parentElement.hidden = true;
        return;
    }

    const mapa = crearMapa(contenedor, CENTRO_LIMA, ZOOM_INICIAL);
    let marcador = null;

    const crearIcono = () => iconoMarcador(campoOrden?.value ?? "", "marcador--activo");

    const escribirCampos = ({ lat, lng }) => {
        campoLat.value = lat.toFixed(6);
        campoLng.value = lng.toFixed(6);
    };

    const colocar = (posicion) => {
        if (marcador) {
            marcador.setLatLng(posicion);
            return;
        }
        marcador = L.marker(posicion, {
            draggable: true,
            icon: crearIcono(),
            title: "Arrastra para ajustar la ubicación",
        }).addTo(mapa);
        marcador.on("dragend", () => escribirCampos(marcador.getLatLng().wrap()));
    };

    const quitar = () => {
        marcador?.remove();
        marcador = null;
    };

    mapa.on("click", ({ latlng }) => {
        const punto = latlng.wrap();
        escribirCampos(punto);
        colocar(punto);
    });

    const sincronizar = () => {
        const posicion = posicionDeCampos();
        if (posicion) {
            colocar(posicion);
            mapa.panTo(posicion);
        } else if (camposVacios()) {
            quitar();
        }
    };
    campoLat.addEventListener("change", sincronizar);
    campoLng.addEventListener("change", sincronizar);

    campoOrden?.addEventListener("input", () => marcador?.setIcon(crearIcono()));

    botonQuitar?.addEventListener("click", () => {
        campoLat.value = "";
        campoLng.value = "";
        quitar();
    });

    const inicial = posicionDeCampos();
    if (inicial) {
        colocar(inicial);
        mapa.setView(inicial, ZOOM_PUNTO);
    }
}

iniciar();