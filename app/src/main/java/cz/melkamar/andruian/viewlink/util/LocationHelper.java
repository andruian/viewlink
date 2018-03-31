package cz.melkamar.andruian.viewlink.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import cz.melkamar.andruian.viewlink.exception.PermissionException;

/**
 * A helper class for Location management. It handles asking for permissions and sending updates
 * about location change to a specified {@link LocationListener}.
 */
public class LocationHelper implements LocationListener {
    private final AppCompatActivity activity;
    private final LocationListener listener;
    private boolean reportingGps = false;
    private Location lastKnownLocation = null;
    private LocationManager locationManager = null;

    public final static int LOC_REQUEST = 1;

    /**
     * Create a new {@link LocationHelper}.
     *
     * @param activity         The activity requesting location. It must override the {@link AppCompatActivity#onRequestPermissionsResult(int, String[], int[])} method.
     *                         This method shall call {@link LocationHelper#onRequestPermissionsResult(int, String[], int[])} with the given parameters.
     * @param locationListener A listener where GPS updates will be sent to.
     */
    public LocationHelper(AppCompatActivity activity, LocationListener locationListener) {
        this.activity = activity;
        this.listener = locationListener;
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Start sending GPS updates to the {@link LocationListener} provided in constructor.
     *
     * @throws PermissionException Thrown when permissions are denied.
     */
    public void startReportingGps() throws PermissionException {
        Log.v("LocationHelper", "startReportingGps");

        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions();
            throw new PermissionException("GPS not allowed.");
        }

        locationManager.requestLocationUpdates(provider, 1000, 5, this);
        reportingGps = true;
    }

    public void stopReportingGps() {
        Log.i("LocationHelper", "stopReportingGps");
        if (locationManager != null) {
            Log.d("LocationHelper", "removing updates");
            locationManager.removeUpdates(this);
        }
    }

    public boolean checkPermissions() {
        return !(ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Request Location permissions. The activity will be called back by the Android framework.
     */
    public void requestPermissions() {
        Log.v("Locationhelper", "requestPermissions");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOC_REQUEST);
        }
    }

    /**
     * Indicate whether this {@link LocationHelper} is sending location updates to its listener. In other words,
     * indicate whether {@link LocationHelper#startReportingGps()} was called.
     */
    public boolean isReportingGps() {
        return reportingGps;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) throws PermissionException {
        Log.v("Locationhelper", "onRequestPermissionsResult "+requestCode);
        switch (requestCode) {
            case LOC_REQUEST:
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    throw new PermissionException("GPS not allowed.");
                } else {
                    startReportingGps();
                }
                break;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        lastKnownLocation = location;
        listener.onLocationChanged(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }
}
