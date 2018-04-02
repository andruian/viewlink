package cz.melkamar.andruian.viewlink;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import cz.melkamar.andruian.ddfparser.model.*;
import cz.melkamar.andruian.viewlink.data.DataDefHelperProvider;
import cz.melkamar.andruian.viewlink.util.AsyncTaskResult;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import cz.melkamar.andruian.viewlink.data.DataDefHelper;
import cz.melkamar.andruian.viewlink.ui.addsrc.AddEditSourceActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;


/**
 * Created by Martin Melka on 12.03.2018.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddEditSourceActivityTest {
    @Rule
    public IntentsTestRule<AddEditSourceActivity> mActivityRule =
            new IntentsTestRule<>(AddEditSourceActivity.class);

    @Mock
    DataDefHelper dataDefHelperMock;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();


    private String ddfUrl = "http://fake.url/something";

    @Before
    public void setUp() throws Exception {
//        Mockito.when(dataDefHelperMock.getDataDefs(anyString(), any())).thenAnswer(i -> new DataDef(i.getArgument(0), i.getArgument(0),i.getArgument(0)));

//        Mockito.when(dataDefHelperMock.getDataDefs(ddfUrl)).thenReturn(new AsyncTaskResult<>(createFakeDdf()));
//        DataDefHelperProvider.getProvider().setInstance(dataDefHelperMock);
    }

//    private List<DataDef> createFakeDdf() {
//        Map<String, ClassToLocPath> gpsPathMap = new HashMap<>();
//        gpsPathMap.put("http://location.class", new ClassToLocPath(
//                new PropertyPath("http://path.lat"),
//                new PropertyPath("http://path.lat"),
//                "http://location.class"));
//
//        List<DataDef> resultList = new ArrayList<>();
//        resultList.add(new DataDef(
//                "http://fake.ddf",
//                new LocationClassDef("http://sparql.endpoint/location", "http://location.class", gpsPathMap),
//                new SourceClassDef("http://sparql.endpoint/source", "http://source.class", new PropertyPath("http://pth.ex"), new SelectProperty[0]),
//                new IndexServer("http://index.server", 1),
//                new HashMap<>()
//        ));
//        return resultList;
//    }

    /**
     * Test adding a new data source. Check the result the activity returns.
     * DataDefHelper is mocked away.
     */
    @Test
    public void addNewSourceTest() {
//        String fakeUrl = "http://example.org";
//
//        onView(withId(R.id.new_datasource_name)).perform(typeText("test name"));
//        onView(withId(R.id.new_datasource_url))
//                .perform(typeText(fakeUrl))
//                .perform(closeSoftKeyboard());
//
//        onView(withId(R.id.fab)).perform(click());
//        assertThat(mActivityRule.getActivityResult(), ActivityResultMatchers.hasResultCode(Activity.RESULT_OK));
//
//        DataDef expectedResult = new DataDef(fakeUrl, fakeUrl, fakeUrl);
//        assertThat(mActivityRule.getActivityResult(), ActivityResultMatchers.hasResultData(hasExtra(AddEditSourceActivity.TAG_RESULT_DATASOURCE, expectedResult)));
    }
}
