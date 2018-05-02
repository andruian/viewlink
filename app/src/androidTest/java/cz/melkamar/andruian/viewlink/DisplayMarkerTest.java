package cz.melkamar.andruian.viewlink;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.DrawerMatchers;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cz.melkamar.andruian.ddfparser.model.ClassToLocPath;
import cz.melkamar.andruian.ddfparser.model.DataDef;
import cz.melkamar.andruian.ddfparser.model.IndexServer;
import cz.melkamar.andruian.ddfparser.model.LocationClassDef;
import cz.melkamar.andruian.ddfparser.model.PropertyPath;
import cz.melkamar.andruian.ddfparser.model.SelectProperty;
import cz.melkamar.andruian.ddfparser.model.SourceClassDef;
import cz.melkamar.andruian.viewlink.data.DataDefHelper;
import cz.melkamar.andruian.viewlink.data.DataDefHelperProvider;
import cz.melkamar.andruian.viewlink.data.persistence.ParserDatadefPersistor;
import cz.melkamar.andruian.viewlink.data.place.PlaceFetcher;
import cz.melkamar.andruian.viewlink.data.place.PlaceFetcherProvider;
import cz.melkamar.andruian.viewlink.model.place.Place;
import cz.melkamar.andruian.viewlink.ui.main.MainActivity;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static cz.melkamar.andruian.viewlink.TestUtil.getActivityInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;

/**
 * Automated UI test of the following scenario:
 * - (DataDefHelper and PlaceFetcher are mocked)
 * .
 * - No data definitions to start with
 * - Open Manage datasources screen
 * - Open Add datasource screen
 * - Write a mocked URL, confirm
 * - Assert that a datasource was added
 * - Go back to main activity
 * - Assert that it contains a single marker - returned by the mocked PlaceFetcher
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DisplayMarkerTest {
    @Rule public IntentsTestRule<MainActivity> mActivityRule =
            new IntentsTestRule<MainActivity>(MainActivity.class);

    @Mock DataDefHelper dataDefHelperMock;
    @Mock PlaceFetcher placeFetcherMock;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);


    private String ddfUrl = "http://fake.url/something";
    private List<DataDef> fakeDdfs;

    @BeforeClass
    public static void beforeClass() {
        InstrumentationRegistry.getTargetContext().deleteDatabase(ViewLinkApplication.DB_NAME);
    }

    @Before
    public void setUp() throws Exception {
        fakeDdfs = createFakeDdf();
        Mockito
                .when(dataDefHelperMock.getDataDefs(ddfUrl))
                .thenReturn(new AsyncTaskResult<>(fakeDdfs));
        DataDefHelperProvider.getProvider().setInstance(dataDefHelperMock);

        Mockito
                .when(placeFetcherMock.fetchPlaces(any(), any(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(
                        new PlaceFetcher.FetchPlacesResult(PlaceFetcher.FetchPlacesResult.RESULT_TYPE_PLACES,
                                new ArrayList<>(Arrays.stream(new Place[]{new Place(
                                        "http://fake.place/uri",
                                        "http://fake.location.place/uri",
                                        50,
                                        14,
                                        "http://source.class",
                                        ParserDatadefPersistor.transDataDefToLocal(fakeDdfs.get(0)),
                                        null
                                )}).collect(Collectors.toList())
                                )));
//        ;
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

    /**
     * The actual test.
     */
    @Test
    public void displayTest() {
        onView(withId(R.id.drawer_layout))
                .check(matches(DrawerMatchers.isClosed()))
                .perform(DrawerActions.open());

        // Open up the manage datasources activity
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_manage_sources));

        // Check no data defs shown
//        onView(withId(R.id.datadefs_rv)).check(new RecyclerViewItemCountAssertion(0));
        onView(withId(R.id.datadefs_rv)).check(matches(Matchers.not(isDisplayed())));


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

        // Get the current visible activity and assert the expected marker is shown
        MainActivity activity = (MainActivity) getActivityInstance();
        Assert.assertEquals(1, activity.getPlacesStoredMap().size());
        Assert.assertEquals(1, activity.getPlacesStoredMap().values().iterator().next().size());

        Place place = activity.getPlacesStoredMap().values().iterator().next().iterator().next();
        Assert.assertEquals(50, place.getPosition().latitude, 0.000001);
        Assert.assertEquals(14, place.getPosition().longitude, 0.000001);
        Assert.assertEquals("http://fake.place/uri", place.getTitle());
    }


}
