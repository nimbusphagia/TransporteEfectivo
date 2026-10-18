const URL_TILES = "https://tile.openstreetmap.org/{z}/{x}/{y}.png";
const ATRIBUCION =
    '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors';
const TAMANO_MARCADOR = 32;

export const hayLeaflet = () => typeof L !== "undefined";

export function crearMapa(contenedor, centro, zoom) {
    const mapa = L.map(contenedor);
    if (centro) {
        mapa.setView(centro, zoom);
    }
    L.tileLayer(URL_TILES, { maxZoom: 19, attribution: ATRIBUCION }).addTo(mapa);
    return mapa;
}

export function iconoMarcador(texto, modificador = "", tamano = TAMANO_MARCADOR) {
    return L.divIcon({
        className: `marcador ${modificador}`.trim(),
        html: String(texto),
        iconSize: [tamano, tamano],
        popupAnchor: [0, -tamano / 2],
    });
}

export function crearPopupTexto(texto) {
    const negrita = document.createElement("strong");
    negrita.textContent = texto;
    return negrita;
}

export function marcarActivo(marcador, activo) {
    marcador.getElement()?.classList.toggle("marcador--activo", activo);
    marcador.setZIndexOffset(activo ? 1000 : 0);
}