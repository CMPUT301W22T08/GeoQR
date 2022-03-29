package com.example.geoqr;

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
    public ActivityTestRule<LoginPage> rule =
            new ActivityTestRule<>(LoginPage.class, true, true);
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
    public void changeActivity(){
        solo.assertCurrentActivity("Not Login Page", LoginPage.class);
        solo.clickOnButton("auto username");
        solo.sleep(1);
        solo.clickOnButton("Login");
        solo.sleep(1);
        solo.assertCurrentActivity("Not ScanQR", ScanQR.class);
    }
    @Test
    public void noUsername(){
        solo.assertCurrentActivity("Not Login Page", LoginPage.class);
        solo.clickOnButton("Login");
        solo.assertCurrentActivity("Not Login Page", LoginPage.class);
    }

}
