package pe.cibertec.transportes.dto;

import java.math.BigDecimal;

public record LineaEnPunto(
        Long lineaId,
        String lineaNombre,
        BigDecimal costoPasaje,
        Long paraderoId,
        String paraderoNombre,
        Integer orden) {
}