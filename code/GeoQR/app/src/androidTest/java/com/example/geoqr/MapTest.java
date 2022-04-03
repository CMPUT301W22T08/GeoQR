package com.example.geoqr;

import android.view.View;
import android.widget.Button;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.zxing.client.android.Intents;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;


@RunWith(AndroidJUnit4.class)
public class MapTest {
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
    public void changeToHome(){
        // tests changing from HomePage to Map and back to Map
        solo.sleep(200);
        solo.assertCurrentActivity("Failed A", ScanQR.class);
        solo.sleep(200);
        solo.scrollToSide(Solo.LEFT, 0.6F, 20);
        solo.waitForView(solo.getView(R.id.scan),1,1400);
        solo.assertCurrentActivity("Failed B", MapActivity.class);
        solo.clickOnView(solo.getView(R.id.scan));
        solo.sleep(1);
        solo.assertCurrentActivity("Failed C", ScanQR.class);
    }

    @Test
    public void changeToMap(){
        // tests changing from Homepage to Map
        solo.assertCurrentActivity("Not ScanQR", ScanQR.class);
        solo.scrollToSide(Solo.LEFT, 0.6F, 20);
        solo.sleep(100);
        solo.assertCurrentActivity("Not Maps",MapActivity.class);
    }



}
