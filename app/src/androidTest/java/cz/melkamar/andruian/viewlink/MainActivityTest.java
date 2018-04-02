package cz.melkamar.andruian.viewlink;

import android.app.Activity;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.ActivityResultMatchers;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.DrawerMatchers;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import cz.melkamar.andruian.ddfparser.model.*;
import cz.melkamar.andruian.viewlink.data.DataDefHelper;
import cz.melkamar.andruian.viewlink.data.DataDefHelperProvider;
import cz.melkamar.andruian.viewlink.data.persistence.ParserDatadefPersistor;
import cz.melkamar.andruian.viewlink.data.place.PlaceFetcher;
import cz.melkamar.andruian.viewlink.data.place.PlaceFetcherProvider;
import cz.melkamar.andruian.viewlink.model.place.Place;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import cz.melkamar.andruian.viewlink.ui.main.MainActivity;
import cz.melkamar.andruian.viewlink.ui.srcmgr.DatasourcesActivity;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.*;
import java.util.stream.Collectors;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.any;

/**
 * Created by Martin Melka on 12.03.2018.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    @Rule public IntentsTestRule<MainActivity> mActivityRule =
            new IntentsTestRule<MainActivity>(MainActivity.class);

    @Mock DataDefHelper dataDefHelperMock;
    @Mock PlaceFetcher placeFetcherMock;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();


    private String ddfUrl = "http://fake.url/something";
    private List<DataDef> fakeDdfs;

    @Before
    public void setUp() throws Exception {
        fakeDdfs = createFakeDdf();
        Mockito
                .when(dataDefHelperMock.getDataDefs(ddfUrl))
                .thenReturn(new AsyncTaskResult<>(fakeDdfs));
        DataDefHelperProvider.getProvider().setInstance(dataDefHelperMock);

        Mockito
                .when(placeFetcherMock.fetchPlaces(any(), any(), any(), any(), any()))
                .thenReturn(new ArrayList<>(
                        Arrays.stream(new Place[]{new Place(
                                "http://fake.place/uri",
                                "http://fake.location.place/uri",
                                50,
                                14,
                                "http://source.class",
                                ParserDatadefPersistor.transDataDefToLocal(fakeDdfs.get(0)),
                                null
                        )}).collect(Collectors.toList())
                ));
        PlaceFetcherProvider.getProvider().setInstance(placeFetcherMock);
    }

    private List<DataDef> createFakeDdf() {
        Map<String, ClassToLocPath> gpsPathMap = new HashMap<>();
        gpsPathMap.put("http://location.class", new ClassToLocPath(
                new PropertyPath("http://path.lat"),
                new PropertyPath("http://path.lat"),
                "http://location.class"));

        List<DataDef> resultList = new ArrayList<>();
        resultList.add(new DataDef(
                "http://fake.ddf",
                new LocationClassDef("http://sparql.endpoint/location", "http://location.class", gpsPathMap),
                new SourceClassDef("http://sparql.endpoint/source", "http://source.class", new PropertyPath("http://pth.ex"), new SelectProperty[0]),
                new IndexServer("http://index.server", 1),
                new HashMap<>()
        ));
        return resultList;
    }

    @Test
    public void manageDatasourcesShows() {
        onView(withId(R.id.drawer_layout))
                .check(matches(DrawerMatchers.isClosed()))
                .perform(DrawerActions.open());

        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_manage_sources));

        intended(hasComponent(DatasourcesActivity.class.getName()));
    }

    @Test
    public void displayTest() {
        onView(withId(R.id.drawer_layout))
                .check(matches(DrawerMatchers.isClosed()))
                .perform(DrawerActions.open());

        // Open up the manage datasources activity
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_manage_sources));

        // Check no data defs shown
        onView(withId(R.id.datadefs_rv)).check(new RecyclerViewItemCountAssertion(0));

        // Open up the add datasource activity
        onView(withId(R.id.fab))
                .perform(click());
        onView(withId(R.id.new_datasource_url))
                .perform(typeText(ddfUrl))
                .perform(closeSoftKeyboard());
        onView(withId(R.id.fab)).perform(click());

        // Check one data def shown
        onView(withId(R.id.datadefs_rv)).check(new RecyclerViewItemCountAssertion(1));

        // Return to main activity
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());

        // TODO mock away places fetcher

        // Check that a marker is shown by reading the ClusterManager
        Assert.assertEquals(1, mActivityRule.getActivity().getClusterMgrs().values().size());
        Assert.assertEquals(1, mActivityRule.getActivity().getClusterMgrs().values().iterator().next().getMarkerCollection().getMarkers().size());
    }
}
