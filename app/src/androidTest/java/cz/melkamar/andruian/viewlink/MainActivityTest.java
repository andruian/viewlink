package cz.melkamar.andruian.viewlink;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.DrawerMatchers;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import cz.melkamar.andruian.viewlink.ui.MainActivity;
import cz.melkamar.andruian.viewlink.ui.srcmgr.DatasourcesActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Martin Melka on 12.03.2018.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    @Rule
    public IntentsTestRule<MainActivity> mActivityRule =
            new IntentsTestRule<MainActivity>(MainActivity.class);

    @Test
    public void manageDatasourcesShows(){
        onView(withId(R.id.drawer_layout))
                .check(matches(DrawerMatchers.isClosed()))
                .perform(DrawerActions.open());

        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_manage_sources));

        intended(hasComponent(DatasourcesActivity.class.getName()));
    }

    @Test
    public void foobar(){
        onView(withId(R.id.fab))
                .perform(ViewActions.click());
    }
}
