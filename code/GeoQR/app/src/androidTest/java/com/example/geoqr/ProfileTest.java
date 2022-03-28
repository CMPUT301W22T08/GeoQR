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
public class ProfileTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<ProfilePage> rule =
            new ActivityTestRule<>(ProfilePage.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void correctActivity() throws Exception {

        solo.assertCurrentActivity("Wrong Activity", ProfilePage.class);
    }



}
