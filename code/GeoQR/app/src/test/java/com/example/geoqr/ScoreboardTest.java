package com.example.geoqr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class ScoreboardTest {
    private Scoreboard scoreboard;
    private String[] playerNames =
            {"aaa", "abb", "abc", "abd", "adc",
             "bda", "bdc", "bdb", "bbb", "cba",
             "dba", "dda", "ddb", "dde", "ebd",
             "xyz", "zyx", "qwe", "rty", "key"}; // List of player names

    private class MockCallee implements Scoreboard.RankingUpdatable {
        @Override
        public void update(boolean isFilter) {
            return;
        }
    }

    /**
     * Runs before all tests and creates a scoreboard
     */
    @BeforeEach
    public void createScorboard() {
        RandomString rs = new RandomString();
        int n = 20; // No. of players
        int k = 100; // No. of QRs


        ArrayList<User> players = new ArrayList<>(); // List of players
        ArrayList<QR> qrs = new ArrayList<>(); // All QRs

        for (int i = 0; i < k; i += 5) {
            int pi = Math.floorDiv(i, 5);

            ArrayList<QR> playerQRs = new ArrayList<>();
            int highest = 0;
            int lowest = 0;

            for (int j = 0; j < 5; j++) {
                // Generate the Hex of the QR score
                String content = rs.generateAlphaNumeric(10);
                CalculateScore c = new CalculateScore(content);

                QR qr = new QR(c.getQRHex(), content, playerNames[pi], c.find_total());
                playerQRs.add(qr);
                qrs.add(qr);

                highest = Math.max(qr.getScore(), highest);
                lowest = lowest == 0 ? qr.getScore() : Math.min(qr.getScore(), lowest);
            }

            User user = new User(playerNames[pi], highest, lowest, playerQRs);
            players.add(user);
        }

        this.scoreboard = new Scoreboard(new MockCallee(), playerNames[0], players, qrs);
    }

    /**
     * Checks the filter method of the scoreboard
     */
    @Test
    public void testFilter() {

        // Specify search
        scoreboard.filterUsers("a");
        assertEquals(5, scoreboard.getUsers().size());

        // Clear search
        scoreboard.filterUsers("");
        assertEquals(20, scoreboard.getUsers().size());

        // More specific search
        scoreboard.filterUsers("ab");
        assertEquals(3, scoreboard.getUsers().size());

        // Exact search
        scoreboard.filterUsers("xyz");
        assertEquals(1, scoreboard.getUsers().size());

        // Clear search
        scoreboard.filterUsers("");
        assertEquals(20, scoreboard.getUsers().size());
    }
}
