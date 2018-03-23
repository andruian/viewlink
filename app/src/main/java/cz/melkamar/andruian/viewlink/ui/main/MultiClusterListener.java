package cz.melkamar.andruian.viewlink.ui.main;

import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import java.util.HashMap;
import java.util.Map;

import cz.melkamar.andruian.viewlink.model.datadef.DataDef;

public class MultiClusterListener<T> implements GoogleMap.OnCameraIdleListener, GoogleMap.OnMarkerClickListener {
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
    public boolean onMarkerClick(Marker marker) {
        Log.d("onMarkerClick", "onCameraIdle");
        for (ClusterManager clusterManager : managers.values()) {
            clusterManager.onMarkerClick(marker);
        }
        return true;
    }
}
