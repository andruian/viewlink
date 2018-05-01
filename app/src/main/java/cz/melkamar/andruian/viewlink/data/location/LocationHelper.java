package cz.melkamar.andruian.viewlink.data.location;

import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import cz.melkamar.andruian.viewlink.exception.PermissionException;

/**
 * An interface for a helper class providing access to the device's location.
 */
public interface LocationHelper {
    int LOC_REQUEST = 1;

    void startReportingGps() throws PermissionException;

    void stopReportingGps();

    boolean checkPermissions();

    void requestPermissions();

    boolean isReportingGps();

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) throws PermissionException;

    Location getLastKnownLocation();

    interface LocationHelperFactory {
        LocationHelper create(AppCompatActivity activity, LocationListener listener);
    }
}
