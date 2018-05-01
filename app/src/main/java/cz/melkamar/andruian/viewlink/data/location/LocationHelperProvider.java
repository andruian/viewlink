package cz.melkamar.andruian.viewlink.data.location;

import android.location.LocationListener;
import android.support.v7.app.AppCompatActivity;

/**
 * A provider class of {@link LocationHelper} instances.
 *
 * The default provided class is {@link LocationHelperImpl}, but other implementations may be used instead.
 * This is useful for mocking during testing.
 */
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
