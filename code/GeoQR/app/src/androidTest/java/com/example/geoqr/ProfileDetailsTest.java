package com.example.geoqr;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Class for running tests of ProfilePage
 * Must be logged into an account before running tests
 * Else, will get error that info in ProfilePage is null
 *
 */

@RunWith(AndroidJUnit4.class)
public class ProfileDetailsTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<ProfilePage> rule =
            new ActivityTestRule<>(ProfilePage.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * clicks an item in the profile list to bring to details page
     * Only works if items in list, otherwise will automatically fail
     *
     * @throws Exception
     */
    @Test
    public void correctActivity() throws Exception {

        solo.clickInList(0);
        solo.assertCurrentActivity("Wrong Activity", ProfilePage.class);
    }


    /**
     * adds a string, commits it, tests if it is saved
     * else, throws exception
     *
     * @throws Exception
     */
    @Test
    public void setComment() throws Exception {

        correctActivity();

        // adds a test string, checks if has been added
        solo.clickOnButton("Edit Comment");
        solo.enterText((EditText) solo.getView(R.id.detail_edit_bar), "Test String");
        solo.clickOnButton("O");
        assertTrue(solo.searchText("Test String"));
    }

    /**
     * adds a string, then presses cancel button and tests
     * to see that it is not saved, else throws exception
     *
     * @throws Exception
     */
    @Test
    public void cancelComment() throws Exception {

        correctActivity();

        // clears comment, types text but then presses cancel, checks to see if has been added
        solo.clickOnButton("Edit Comment");
        solo.enterText((EditText) solo.getView(R.id.detail_edit_bar), "this string should not be saved");
        solo.clickOnButton("X");
        solo.sleep(4);
        assertFalse(solo.searchText("this string should not be saved"));
    }

    /**
     * goes back to profile page,
     * if does not work, throws exception
     *
     * @throws Exception
     */
    @Test
    public void returnToProfile() throws Exception {

        correctActivity();

        // presses back arrow, checks if back in profile pag
        solo.clickOnView(solo.getView(R.id.detail_back));
        solo.assertCurrentActivity("Wrong activity", ProfilePage.class);
    }
}