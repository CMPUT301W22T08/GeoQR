package com.example.geoqr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AdminTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.geoqr", appContext.getPackageName());
    }

    private Admin admin;
    private ArrayList<QR> mockQRs;
    private ArrayList<AdminPlayerTuple> mockPlayers;

    @Before
    public void createAdmin() {
        admin = new Admin(InstrumentationRegistry.getInstrumentation().getTargetContext());

        mockQRs = new ArrayList<>();
        mockPlayers = new ArrayList<>();

        mockQRs.add(new QR("A","ABCD", "wUw", 20));
        mockQRs.add(new QR("B","DCBA", "uWu", 30));
        mockQRs.add(new QR("C", "BCDA", "wUw", 10));
        mockQRs.add(new QR("D", "ABCD", "uWu", 20));
        mockQRs.add(new QR("E", "AABC", "poppy", 40));
        mockQRs.add(new QR("F", "BBBD", "ursa", 60));
        mockQRs.add(new QR("G", "BCDA","ursa",10));
        mockQRs.add(new QR("H", "DDDD", "bat", 100));


        mockPlayers.add(new AdminPlayerTuple("wUw", 30));
        mockPlayers.add(new AdminPlayerTuple("uWu", 50));
        mockPlayers.add(new AdminPlayerTuple("bat", 100));
        mockPlayers.add(new AdminPlayerTuple("ursa", 70));
        mockPlayers.add(new AdminPlayerTuple("poppy", 40));

        admin.getQRAdapter().addAll(mockQRs);
        admin.getPlayerAdapter().addAll(mockPlayers);
    }

    @Test
    public void checkDeletePlayers() {
        int n = admin.getPlayerAdapter().getCount();

        assertTrue(admin.noPlayerSelected());
        admin.deletePlayers();

        assertTrue(n == admin.getPlayerAdapter().getCount());
        admin.addSelectedPlayerAt(0); // Selects to Delete
        admin.addSelectedPlayerAt(0); // "

        admin.deletePlayers();

        assertEquals(n, admin.getPlayerAdapter().getCount() + 1);
    }

    @Test
    public void checkDeleteQRCodes() {
        int n = admin.getQRAdapter().getCount();

        admin.deleteQRCodes();

        assertTrue(n == admin.getQRAdapter().getCount());

        admin.addSelectedQRAt(0); // Selects to Delete
        admin.addSelectedQRAt(1); // Selects to Delete

        admin.deleteQRCodes();

        assertEquals(n, admin.getQRAdapter().getCount() + 2);
    }

    @Test
    public void checkPlayerSelection() {
        admin.addSelectedPlayerAt(0);
        admin.addSelectedPlayerAt(2);

        assertEquals(2, admin.getSelectedPlayerIndices().size());

        admin.deletePlayers();

        assertEquals(0, admin.getSelectedPlayerIndices().size());
    }

    @Test
    public void checkQRSelection() {
        admin.addSelectedQRAt(1);
        admin.addSelectedQRAt(1);

        assertEquals(1, admin.getSelectedQRIndices().size());

        admin.deleteQRCodes();

        assertEquals(0, admin.getSelectedQRIndices().size());
    }

}