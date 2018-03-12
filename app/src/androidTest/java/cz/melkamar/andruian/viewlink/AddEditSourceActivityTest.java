package cz.melkamar.andruian.viewlink;

import android.app.Activity;
import android.support.test.espresso.contrib.ActivityResultMatchers;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import cz.melkamar.andruian.viewlink.data.DataManager;
import cz.melkamar.andruian.viewlink.data.DataManagerProvider;
import cz.melkamar.andruian.viewlink.model.DataSource;
import cz.melkamar.andruian.viewlink.ui.addsrc.AddEditSourceActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
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
    DataManager dataManagerMock;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {
        Mockito.when(dataManagerMock.getDataSource(anyString())).thenAnswer(i -> new DataSource(i.getArgument(0), i.getArgument(0),i.getArgument(0)));
        DataManagerProvider.setDataManager(dataManagerMock);
    }

    /**
     * Test adding a new data source. Check the result the activity returns.
     * DataManager is mocked away.
     */
    @Test
    public void addNewSourceTest() {
        String fakeUrl = "http://example.org";

        onView(withId(R.id.new_datasource_name)).perform(typeText("test name"));
        onView(withId(R.id.new_datasource_url))
                .perform(typeText(fakeUrl))
                .perform(closeSoftKeyboard());

        onView(withId(R.id.fab)).perform(click());
        assertThat(mActivityRule.getActivityResult(), ActivityResultMatchers.hasResultCode(Activity.RESULT_OK));

        DataSource expectedResult = new DataSource(fakeUrl, fakeUrl, fakeUrl);
        assertThat(mActivityRule.getActivityResult(), ActivityResultMatchers.hasResultData(hasExtra(AddEditSourceActivity.TAG_RESULT_DATASOURCE, expectedResult)));
    }
}
