package com.example.geoqr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;



@RunWith(AndroidJUnit4.class)
public class LoginPageTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<LoginPage> rule = new ActivityTestRule<>(LoginPage.class, true, true);
    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }
    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        solo.assertCurrentActivity("Wrong Activity",LoginPage.class);
    }

    @Test
    public void noUsername() {
        solo.assertCurrentActivity("Wrong Activity", LoginPage.class);
        solo.clickOnView(solo.getView(com.example.geoqr.R.id.btn_Login));
        solo.assertCurrentActivity("Wrong Activity", LoginPage.class);
    }

    @Test
    public void changeActivity(){
        solo.assertCurrentActivity("Not Login Page", LoginPage.class);
        solo.clickOnView(solo.getView(com.example.geoqr.R.id.btn_Generate));
        solo.clickOnView(solo.getView(com.example.geoqr.R.id.btn_Login));
        solo.assertCurrentActivity("Wrong Activity", ScanQR.class);
    }
}
