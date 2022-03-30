package com.example.geoqr;

import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


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
     * clicks an item in the profile list to bring to details page
     * @throws Exception
     */
    @Test
    public void getDetails() throws Exception {

        // click item from list in profile page, check if moves to details
        solo.clickInList(0);
        solo.assertCurrentActivity("Wrong activity", ProfileDetails.class);

        // click on edit comment, edit comment, click O button
        solo.clickOnButton("Edit Comment");
        solo.enterText((EditText) solo.getView(R.id.detail_edit_bar), "Test String");
        solo.clickOnButton("O");


        // presses back arrow, checks if back in profile pag
        solo.clickOnView(solo.getView(R.id.detail_back));
        solo.assertCurrentActivity("Wrong activity", ProfilePage.class);
    }


}
