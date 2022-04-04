package com.example.geoqr;

import static org.junit.Assert.assertEquals;

import android.widget.EditText;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.zxing.client.android.Intents;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Run test for addQR preview page
 * check if can go to activities correctly if button are pressed
 *
 */

@RunWith(AndroidJUnit4.class)
public class addQRTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<addQR> rule = new ActivityTestRule<>(addQR.class, true, true);

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

        solo.assertCurrentActivity("Wrong Activity", addQR.class);
    }

    /**
     * test if cancle pressed switch back to camera
     * @throws Exception
     */
    @Test
    public void cancleswitchActiviyt() throws Exception{
        solo.assertCurrentActivity("Wrong Activity", addQR.class);
        solo.clickOnView(solo.getView(com.example.geoqr.R.id.CancelBtn));
        solo.assertCurrentActivity("Wrong Activity", ScanQR.class);

    }

    /**
     * test if add pressed switch back to camera
     * @throws Exception
     */
    @Test
    public void switchActivity() throws Exception{
        solo.assertCurrentActivity("Wrong Activity", addQR.class);
        solo.clickOnView(solo.getView(com.example.geoqr.R.id.AddBtn));
        solo.assertCurrentActivity("Wrong Activity", ScanQR.class);

    }

}
