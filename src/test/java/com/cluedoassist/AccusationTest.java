package com.cluedoassist;

import org.testng.annotations.*;

import static org.testng.AssertJUnit.*;

import java.util.*;

public class AccusationTest {

    @Test
    public void testCardsImmutable() {
        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(Card.valueOf("Kitchen"));
        cards.add(Card.valueOf("Plum"));
        cards.add(Card.valueOf("Pipe"));
        Accusation a = new Accusation("Mike", cards);
        boolean exceptionOccured = false;
        try {
            a.cards.add(Card.valueOf("Study"));
            assertEquals(3, a.cards.size());
        } catch (Exception e) {
            exceptionOccured = true;
        }
        assertTrue(exceptionOccured);
    }
}
