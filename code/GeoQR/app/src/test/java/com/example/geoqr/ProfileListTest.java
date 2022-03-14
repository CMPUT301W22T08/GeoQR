package com.example.geoqr;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;



import android.app.Activity;
import android.app.Fragment;
import android.util.Log;

import androidx.annotation.NonNull;

import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileListTest {


    private ProfileList list;

    @Before
    public void createList() {
        list = new ProfileList(null, new ArrayList<ListEntry>());
    }

    @Test
    public void removeEntry() {
        ListEntry entry = new ListEntry("hdhasjdkhjknd", "223311664", "12", "(22,22)", "8:12");
        ListEntry notInList = new ListEntry("hdfffdfdfdffd", "121212121", "321", "(21,33)", "9:56");

        assertThrows(IllegalArgumentException.class, () -> {
            list.deleteEntry(notInList);
        });

        list.addEntry(entry);
        list.deleteEntry(entry);
        assertEquals(0, list.size());

    }

    @Test
    public void addEntry() {
        int size = list.size();
        list.addEntry(new ListEntry("hdfffdfdfdffd", "121212121", "321", "(21,33)", "9:56"));
        assertEquals(list.size(), size+1);
    }


}
