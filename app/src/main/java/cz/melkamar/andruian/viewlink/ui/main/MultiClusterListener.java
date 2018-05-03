package cz.melkamar.andruian.viewlink.ui.main;

import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import java.util.HashMap;
import java.util.Map;

import cz.melkamar.andruian.viewlink.model.datadef.DataDef;

/**
 * A class dispatching map events to multiple listeners.
 *
 * {@link GoogleMap} only allows one listener for events at a time. However, the application requires multiple
 * listeners, as there is one separate {@link ClusterManager} per each data definition shown. This class may be
 * registered as the single listener of {@link GoogleMap} and it will dispatch events to arbitrary number of listeners.
 */
public class MultiClusterListener implements GoogleMap.OnCameraIdleListener, GoogleMap.OnInfoWindowClickListener {
    Map<DataDef, ClusterManager> managers;

    public MultiClusterListener() {
        this.managers = new HashMap<>();
    }

    public void addListener(DataDef dataDef, ClusterManager manager){
        this.managers.put(dataDef, manager);
    }

    @Override
    public void onCameraIdle() {
        Log.d("MultiClusterListener", "onCameraIdle");
        for (ClusterManager clusterManager : managers.values()) {
            clusterManager.onCameraIdle();
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Log.d("MultiClusterListener", "onInfoWindowClick");
        for (ClusterManager clusterManager : managers.values()) {
            clusterManager.onMarkerClick(marker);
            clusterManager.onInfoWindowClick(marker);
        }
    }
}
