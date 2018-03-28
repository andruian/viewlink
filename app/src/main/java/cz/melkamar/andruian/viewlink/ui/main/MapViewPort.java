package cz.melkamar.andruian.viewlink.ui.main;

import com.google.android.gms.maps.model.LatLng;

public class MapViewPort {
    private final double neLat;
    private final double neLng;
    private final double swLat;
    private final double swLng;

    private static final double MUST_BE_VISIBLE_PERCENTAGE = 0.85;

    /**
     * Create a {@link MapViewPort} with "normalized" boundaries, where the left edge always
     * has a lower longitude than the right edge. It does not "wrap around" like regular latlng does.
     * <p>
     * If the left edge of the viewport is higher number than the right edge, it means it overflowed.
     * To fix that, add 360 to the right edge so that it appears on the right side of the left edge.
     *
     * @param northeast
     * @param southwest
     */
    public MapViewPort(LatLng northeast, LatLng southwest) {
        neLat = northeast.latitude;
        swLat = southwest.latitude;

        double _neLng = northeast.longitude;
        swLng = southwest.longitude;

        if (swLng > _neLng) {
            neLng = _neLng + 360;
        } else {
            neLng = _neLng;
        }
    }

    public MapViewPort(double neLat, double neLng, double swLat, double swLng) {
        this.swLat = swLat;
        this.swLng = swLng;
        this.neLat = neLat;

        double _neLng = neLng;
        if (swLng > _neLng) {
            this.neLng = _neLng + 360;
        } else {
            this.neLng = _neLng;
        }
    }

    /**
     * Create a sub-{@link MapViewPort} that must be visible on the map. If not visible, the
     * map should be refreshed.
     */
    public MapViewPort getMustBeVisiblePort() {
        double width = neLng - swLng;
        double height = neLat - swLat;

        return new MapViewPort(
                neLat - (1 - MUST_BE_VISIBLE_PERCENTAGE) * height,
                neLng - (1 - MUST_BE_VISIBLE_PERCENTAGE) * width,
                swLat + (1 - MUST_BE_VISIBLE_PERCENTAGE) * height,
                swLng + (1 - MUST_BE_VISIBLE_PERCENTAGE) * width
        );
    }

    /**
     * Check if the otherPort is completely visible (=is inside) in this {@link MapViewPort}.
     */
    public boolean contains(MapViewPort otherPort) {
        return otherPort.neLat <= this.neLat && otherPort.neLng <= this.neLng &&
                otherPort.swLat >= this.swLat && otherPort.swLng >= this.swLng;
    }
}
