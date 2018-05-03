package cz.melkamar.andruian.viewlink.model.place;

import com.google.android.gms.maps.model.LatLng;

import cz.melkamar.andruian.viewlink.model.datadef.DataDef;

/**
 * A cluster of places generated server-side. The cluster contains one or more places, has a defined geospatial location
 * and holds a reference to its parent data definition.
 *
 * This class is different from the clusters generated by the
 * Android Map Util library dynamically.
 */
public class PlaceCluster extends MapElement {
    public final double latitude;
    public final double longitude;
    public final int placesCount;
    public final DataDef parentDataDef;

    public PlaceCluster(double latitude, double longitude, int placesCount, DataDef parentDataDef) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.placesCount = placesCount;
        this.parentDataDef = parentDataDef;
    }

    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }
}
