package cz.melkamar.andruian.viewlink.ui.main;

import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import java.util.HashMap;
import java.util.Map;

import cz.melkamar.andruian.viewlink.model.datadef.DataDef;

public class MultiClusterListener<T> implements GoogleMap.OnCameraIdleListener, GoogleMap.OnInfoWindowClickListener {
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