package com.example.geoqr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class ProfileListTest {

    private ProfileList list;

    /**
     * Runs before all tests and creates list object.
     *
     */
    @BeforeEach
    public void createList() {
        list = new ProfileList(null, new ArrayList<ListEntry>());
    }

    /**
     * Tests removing of item from list
     * @throws Exception
     */
    @Test
    public void removeEntry() {
        ListEntry entry = new ListEntry("hdhasjdkhjknd", "223311664", "12", "3");
        ListEntry notInList = new ListEntry("hdfffdfdfdffd", "121212121", "321", "4");

        assertThrows(IllegalArgumentException.class, () -> {
            list.deleteEntry(notInList);
        });

        list.addEntry(entry);
        list.deleteEntry(entry);
        assertEquals(0, list.size());

    }

    /**
     * tests addition of entry to list
     * @throws Exception
     */
    @Test
    public void addEntry() {
        int size = list.size();
        list.addEntry(new ListEntry("hdfffdfdfdffd", "121212121", "321", "3"));
        assertEquals(list.size(), size+1);
    }

}
