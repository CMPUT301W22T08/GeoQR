package com.example.geoqr;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class LoginPageTest{
    private Solo solo;

    @Test
    public void isLoginPage() throws Exception{
        solo.assertCurrentActivity("wrong Activity",LoginPage.class);
    }

    @Test
    public void loginToQR() throws Exception{
        solo.clickOnButton(solo.getString(R.id.btn_Generate));
        solo.clickOnButton(solo.getString(R.id.btn_Login));
        solo.assertCurrentActivity("wrong activity",ScanQR.class);
    }
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}

