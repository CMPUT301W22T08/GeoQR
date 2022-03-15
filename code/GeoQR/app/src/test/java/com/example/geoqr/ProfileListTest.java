package com.example.geoqr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class ProfileListTest {

    private ProfileList list;

    @BeforeEach
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
