package cz.melkamar.andruian.viewlink;

import android.location.Location;
import android.location.LocationListener;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.app.AppCompatActivity;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import cz.melkamar.andruian.viewlink.data.location.LocationHelper;
import cz.melkamar.andruian.viewlink.data.location.LocationHelperProvider;
import cz.melkamar.andruian.viewlink.data.place.PlaceFetcher;
import cz.melkamar.andruian.viewlink.data.place.PlaceFetcherProvider;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.ui.main.MainActivity;
import cz.melkamar.andruian.viewlink.ui.main.MainPresenterImpl;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static cz.melkamar.andruian.viewlink.TestUtil.getActivityInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

public class MainPresenterImplTest {
    @Rule public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    static LocationHelper locationHelperMock;
    static AppCompatActivity locActivity;
    static LocationListener locListener;
    static PlaceFetcher placeFetcherMock;

    @BeforeClass
    public static void beforeClass() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(ViewLinkApplication.DB_NAME);

        locationHelperMock = Mockito.mock(LocationHelper.class);
        Mockito
                .when(locationHelperMock.checkPermissions()).thenReturn(true);
        Mockito
                .when(locationHelperMock.isReportingGps()).thenReturn(true);
        LocationHelperProvider.getProvider().setFactory((activity, listener) -> {
            locActivity = activity;
            locListener = listener;
            return locationHelperMock;
        });

        placeFetcherMock = Mockito.mock(PlaceFetcher.class);
        PlaceFetcherProvider.getProvider().setInstance(placeFetcherMock);
    }

    /**
     * Check that the camera follows the user location when the center button is turned on.
     */
    @Test
    public void cameraFollows() throws Throwable {
        SystemClock.sleep(1000);
        onView(withId(R.id.fab)).perform(ViewActions.click());

        Location location = new Location("");
        location.setLatitude(0.5);
        location.setLongitude(1.4);
        Mockito.when(locationHelperMock.getLastKnownLocation()).thenReturn(location);

        mActivityRule.runOnUiThread(() -> locListener.onLocationChanged(location));
        SystemClock.sleep(2500);


        MainActivity activity = (MainActivity) getActivityInstance();
        MainPresenterImpl presenter = (MainPresenterImpl) activity.getPresenter();
        Assert.assertNotNull(presenter.lastCameraUpdatePosition);
        Assert.assertEquals(0.5, presenter.lastCameraUpdatePosition.latitude, 0.1);
        Assert.assertEquals(1.4, presenter.lastCameraUpdatePosition.longitude, 0.1);

    }

    /**
     * Check that the camera follows the user location and in addition to that the markers to be shown
     * are refreshed.
     */
    @Test
    public void cameraFollowsAndRefreshesMarkers() throws Throwable {
        MainActivity activity = (MainActivity) getActivityInstance();
        MainPresenterImpl presenter = (MainPresenterImpl) activity.getPresenter();

        SystemClock.sleep(1000);
        onView(withId(R.id.fab)).perform(ViewActions.click());


        // Change location to somewhere to clear map refresh viewport
        Location location = new Location("");
        location.setLatitude(50.5);
        location.setLongitude(25.4);
        Mockito.when(locationHelperMock.getLastKnownLocation()).thenReturn(location);

        mActivityRule.runOnUiThread(() -> locListener.onLocationChanged(location));
        SystemClock.sleep(2500);

        // Set up fake datadef
        List<DataDef> fakeDdfs = new ArrayList<>();
        DataDef fakeDdf = new DataDef("http://fake.org",
                null, null, null, 0, true);
        fakeDdfs.add(fakeDdf);
        presenter.setDataDefsShownInDrawer(fakeDdfs);
        Mockito
                .when(placeFetcherMock.fetchPlaces(any(), any(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(new ArrayList<>());

        // Change location to tested position
        Location testLocation = new Location("");
        testLocation.setLatitude(0.5);
        testLocation.setLongitude(1.4);
        Mockito.when(locationHelperMock.getLastKnownLocation()).thenReturn(testLocation);

        mActivityRule.runOnUiThread(() -> locListener.onLocationChanged(testLocation));
        SystemClock.sleep(2500);

        Assert.assertNotNull(presenter.lastCameraUpdatePosition);
        Assert.assertEquals(0.5, presenter.lastCameraUpdatePosition.latitude, 0.1);
        Assert.assertEquals(1.4, presenter.lastCameraUpdatePosition.longitude, 0.1);

        Mockito.verify(placeFetcherMock, times(1))
                .fetchPlaces(any(),
                        eq(fakeDdf),
                        AdditionalMatchers.eq(0.5, 0.001),
                        AdditionalMatchers.eq(1.4, 0.001),
                        anyDouble());
    }


}