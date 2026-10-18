package pe.cibertec.transportes.util;

public final class GeoUtils {
    private static final double RADIO_TIERRA_METROS = 6_371_000.0;

    private GeoUtils() {
    }

    public static double distanciaMetros(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * RADIO_TIERRA_METROS * Math.asin(Math.min(1.0, Math.sqrt(a)));
    }

    public static boolean coordenadasValidas(double latitud, double longitud) {
        return Double.isFinite(latitud) && Double.isFinite(longitud)
                && latitud >= -90 && latitud <= 90
                && longitud >= -180 && longitud <= 180;
    }
}
