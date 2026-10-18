(function () {
    "use strict";

    var CENTRO_LIMA = [-12.0464, -77.0428];
    var URL_TILES = "https://tile.openstreetmap.org/{z}/{x}/{y}.png";
    var ATRIBUCION =
        '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors';

    var boton = document.getElementById("btn-ubicacion");
    var estado = document.getElementById("estado-ubicacion");
    var aviso = document.getElementById("aviso-ubicacion");
    var contenedorMapa = document.getElementById("mapa");

    function mostrarAviso(texto) {
        aviso.textContent = texto;
        aviso.hidden = false;
    }

    function ocultarAviso() {
        aviso.textContent = "";
        aviso.hidden = true;
    }

    function irA(latitud, longitud) {
        window.location.href =
            window.location.pathname + "?lat=" + latitud.toFixed(4) + "&lng=" + longitud.toFixed(4);
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
            mostrarAviso(
                "Tu navegador no permite obtener la ubicación. Toca el mapa para elegir un punto."
            );
            return;
        }

        boton.disabled = true;
        estado.textContent = "Buscando tu ubicación…";

        navigator.geolocation.getCurrentPosition(
            function (posicion) {
                irA(posicion.coords.latitude, posicion.coords.longitude);
            },
            function (error) {
                boton.disabled = false;
                estado.textContent = "";
                if (error.code === error.PERMISSION_DENIED) {
                    mostrarAviso(
                        "No diste permiso para usar tu ubicación. Puedes tocar el mapa para elegir un punto."
                    );
                } else if (error.code === error.TIMEOUT) {
                    mostrarAviso(
                        "Tardamos demasiado en obtener tu ubicación. Inténtalo de nuevo o toca el mapa."
                    );
                } else {
                    mostrarAviso(
                        "No pudimos determinar tu ubicación. Toca el mapa para elegir un punto."
                    );
                }
            },
            { enableHighAccuracy: true, timeout: 15000, maximumAge: 30000 }
        );
    }


    function iniciarMapa() {
        if (!contenedorMapa) {
            return;
        }
        if (typeof L === "undefined") {
            mostrarAviso(
                "No se pudo cargar el mapa. Los resultados siguen disponibles en la lista."
            );
            return;
        }

        var lat = parseFloat(contenedorMapa.dataset.lat);
        var lng = parseFloat(contenedorMapa.dataset.lng);
        var hayUbicacion = !isNaN(lat) && !isNaN(lng);

        var mapa = L.map(contenedorMapa).setView(
            hayUbicacion ? [lat, lng] : CENTRO_LIMA,
            hayUbicacion ? 15 : 12
        );
        L.tileLayer(URL_TILES, { maxZoom: 19, attribution: ATRIBUCION }).addTo(mapa);

        var limites = [];

        if (hayUbicacion) {
            L.marker([lat, lng], {
                icon: L.divIcon({ className: "marcador marcador--usuario", iconSize: [22, 22] }),
                title: "Tu ubicación"
            }).addTo(mapa);
            limites.push([lat, lng]);
        }

        var tarjetas = document.querySelectorAll(".punto");

        function activar(tarjetaActiva) {
            tarjetas.forEach(function (t) {
                t.classList.toggle("punto--activo", t === tarjetaActiva);
            });
        }

        tarjetas.forEach(function (tarjeta, i) {
            var posicion = [parseFloat(tarjeta.dataset.lat), parseFloat(tarjeta.dataset.lng)];
            var nombre = document.createElement("strong");
            nombre.textContent = tarjeta.dataset.nombre;

            var marcador = L.marker(posicion, {
                icon: L.divIcon({ className: "marcador", html: String(i + 1), iconSize: [28, 28] }),
                title: tarjeta.dataset.nombre
            })
                .bindPopup(nombre)
                .addTo(mapa);
            limites.push(posicion);

            marcador.on("click", function () {
                activar(tarjeta);
                tarjeta.scrollIntoView({ block: "nearest" });
            });
            tarjeta.addEventListener("click", function (evento) {
                if (evento.target.closest("a")) {
                    return; // los enlaces a la línea funcionan normalmente
                }
                activar(tarjeta);
                mapa.setView(posicion, 17);
                marcador.openPopup();
            });
        });

        if (limites.length > 1) {
            mapa.fitBounds(limites, { padding: [40, 40], maxZoom: 16 });
        }

        mapa.on("click", function (evento) {
            var contenido = document.createElement("div");
            var texto = document.createElement("p");
            texto.textContent = "¿Buscar paraderos desde este punto?";
            var aqui = document.createElement("button");
            aqui.type = "button";
            aqui.className = "btn btn--principal btn--peque";
            aqui.textContent = "Buscar aquí";
            aqui.addEventListener("click", function () {
                irA(evento.latlng.lat, evento.latlng.lng);
            });
            contenido.appendChild(texto);
            contenido.appendChild(aqui);
            L.popup().setLatLng(evento.latlng).setContent(contenido).openOn(mapa);
        });
    }

    if (boton) {
        boton.addEventListener("click", pedirUbicacion);
    }
    iniciarMapa();
})();