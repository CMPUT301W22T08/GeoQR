package com.example.geoqr;

import android.app.Activity;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.robotium.solo.Solo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;

public class Test_addQR {
    private Solo solo;
    @Rule
    public ActivityTestRule<Camera> rule =
            new ActivityTestRule<>(Camera.class, true, true);

    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    @Test
    public void camera_to_add(){
        solo.assertCurrentActivity("Wrong Activity", Camera.class);
        solo.clickOnButton("SCAN");
        solo.assertCurrentActivity("Wrong Activity", addQR.class);
        // could add the switch of add_location and add_photo and comment
        solo.clickOnButton("ADD");
        solo.assertCurrentActivity("Wrong Activity", Camera.class);
    }


}
