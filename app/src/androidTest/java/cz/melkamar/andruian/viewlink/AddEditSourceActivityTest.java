package cz.melkamar.andruian.viewlink;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import cz.melkamar.andruian.viewlink.data.DataManager;
import cz.melkamar.andruian.viewlink.ui.addsrc.AddEditSourceActivity;


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
    DataManager dataManagerMock;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {
//        Mockito.when(dataManagerMock.getDataDefs(anyString(), any())).thenAnswer(i -> new DataDef(i.getArgument(0), i.getArgument(0),i.getArgument(0)));
//        DataManagerProvider.setDataManager(dataManagerMock);
    }

    /**
     * Test adding a new data source. Check the result the activity returns.
     * DataManager is mocked away.
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
