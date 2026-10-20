const carruseles = document.querySelectorAll("[data-carrusel]");

carruseles.forEach((carrusel) => {
  const diapositivas = Array.from(
    carrusel.querySelectorAll(".carrusel__diapositiva"),
  );
  const indicadores = Array.from(
    carrusel.querySelectorAll("[data-carrusel-ir]"),
  );
  const anterior = carrusel.querySelector("[data-carrusel-anterior]");
  const siguiente = carrusel.querySelector("[data-carrusel-siguiente]");

  if (diapositivas.length < 2 || !anterior || !siguiente) {
    return;
  }

  let indiceActual = diapositivas.findIndex((diapositiva) =>
    diapositiva.classList.contains("carrusel__diapositiva--activa"),
  );
  if (indiceActual < 0) {
    indiceActual = 0;
  }

  const mostrar = (indice) => {
    indiceActual = (indice + diapositivas.length) % diapositivas.length;
    diapositivas.forEach((diapositiva, posicion) => {
      const activa = posicion === indiceActual;
      diapositiva.classList.toggle("carrusel__diapositiva--activa", activa);
      diapositiva.setAttribute("aria-hidden", String(!activa));
      indicadores[posicion]?.classList.toggle(
        "carrusel__indicador--activo",
        activa,
      );
      indicadores[posicion]?.setAttribute("aria-pressed", String(activa));
    });
  };

  anterior.addEventListener("click", () => mostrar(indiceActual - 1));
  siguiente.addEventListener("click", () => mostrar(indiceActual + 1));
  indicadores.forEach((indicador) => {
    indicador.addEventListener("click", () => {
      mostrar(Number(indicador.dataset.carruselIr));
    });
  });

  if (!window.matchMedia("(prefers-reduced-motion: reduce)").matches) {
    window.setInterval(() => mostrar(indiceActual + 1), 6000);
  }
});
