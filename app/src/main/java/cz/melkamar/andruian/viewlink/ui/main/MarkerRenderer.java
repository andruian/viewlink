package cz.melkamar.andruian.viewlink.ui.main;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.place.Place;
import cz.melkamar.andruian.viewlink.util.Util;


public class MarkerRenderer extends DefaultClusterRenderer<Place> {
    private final DataDef dataDef;

    public MarkerRenderer(DataDef dataDef, Context context, GoogleMap map, ClusterManager<Place> clusterManager) {
        super(context, map, clusterManager);
        this.dataDef = dataDef;
    }

    @Override
    protected void onBeforeClusterItemRendered(Place place, MarkerOptions markerOptions) {
        markerOptions.title(place.getDisplayName())
                    .icon(BitmapDescriptorFactory.defaultMarker(place.getParentDatadef().getMarkerColor()));

        super.onBeforeClusterItemRendered(place, markerOptions);
    }

    @Override
    protected int getColor(int clusterSize) {
        return Util.colorFromHue(dataDef.getMarkerColor());
    }
}
