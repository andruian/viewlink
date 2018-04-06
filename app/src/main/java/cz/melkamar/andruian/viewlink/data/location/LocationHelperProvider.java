package cz.melkamar.andruian.viewlink.data.location;

import android.location.LocationListener;
import android.support.v7.app.AppCompatActivity;

public class LocationHelperProvider {
    private static LocationHelperProvider provider = new LocationHelperProvider();

    private LocationHelper.LocationHelperFactory factory = new LocationHelperImpl.LocationHelperImplFactory();

    public static LocationHelperProvider getProvider() {
        return provider;
    }

    public LocationHelper getInstance(AppCompatActivity activity, LocationListener listener) {
        return factory.create(activity, listener);
    }

    public void setFactory(LocationHelper.LocationHelperFactory factory) {
        this.factory = factory;
    }
}
