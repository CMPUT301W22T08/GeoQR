package com.example.geoqr;

import static org.junit.Assert.assertEquals;

import android.widget.EditText;
import android.widget.TextView;

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
public class ProfileTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<ProfilePage> rule =
            new ActivityTestRule<>(ProfilePage.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * checks if in correct activity
     * @throws Exception
     */
    @Test
    public void correctActivity() throws Exception {

        solo.assertCurrentActivity("Wrong Activity", ProfilePage.class);
    }

    /**
     * clicks on help button, checks if the manual activity is displayed
     * if not, raise error, if so, return to camera screen, then back to
     * profile and check if back in profile page
     * @throws Exception
     */
    @Test
    public void clickManual() throws Exception {

        // presses back arrow, checks if back in profile pag
        solo.clickOnView(solo.getView(R.id.manual));
        solo.assertCurrentActivity("Wrong activity", Manual.class);

        solo.clickOnView(solo.getView(R.id.manual_scan_btn));
        solo.assertCurrentActivity("Wrong activity", ScanQR.class);

        solo.clickOnView(solo.getView(R.id.profile_btn));
        solo.assertCurrentActivity("Wrong activity", ProfilePage.class);

    }

    /**
     * clears contact bar and checks that its been cleared,
     * else throws exception
     * @throws Exception
     */
    @Test
    public void clearContact() throws Exception {

        // click on contacts edit button, clears, and checks if has been cleared
        solo.clickOnButton("Edit");
        solo.enterText((EditText) solo.getView(R.id.contact_edit), "");
        solo.clickOnButton("O");
        String emptyString = ((EditText) solo.getView(R.id.contact_edit)).getText().toString();
        assertEquals(emptyString, "");
    }

    /**
     * adds contact information and commits them, checks if saved
     * If not, throw exception
     * @throws Exception
     */
    @Test
    public void commitEditToContact() throws Exception {

        // clicks edit button, adds contact string then checks if that string is displayed
        solo.clickOnButton("Edit");
        solo.enterText((EditText) solo.getView(R.id.contact_edit), "contact tests String");
        solo.clickOnButton("O");
        String testString = ((EditText) solo.getView(R.id.contact_edit)).getText().toString();
        assertEquals(testString, "contact tests String");
    }

    /**
     * add contact info, then press cancel, check if saved
     * if it is, throw exception
     * @throws Exception
     */
    @Test
    public void cancelEditToContact() throws Exception {

        // clicks edit button, adds contact string but clicks cancel, makes sure it has not been added
        solo.clickOnButton("Edit");
        solo.enterText((EditText) solo.getView(R.id.contact_edit), "this string should not be saved");
        solo.clickOnButton("X");
        String cancelString = ((TextView) solo.getView(R.id.show_contact)).getText().toString();
        assertEquals(cancelString, "contact tests String");
    }

    /**
     * Checks length of contact string, makes sure does not exceed max
     * Else, throws exception
     * @throws Exception
     */
    @Test
    public void checkContactLength() throws Exception {

        // clicks edit button, checks to see if string does not exceed max length of 20 characters
        solo.clickOnButton("Edit");
        String lengthTest = ((TextView) solo.getView(R.id.show_contact)).getText().toString();
        assertEquals(lengthTest.length(), 20);

    }



}
