package com.cluedoassist;

import org.testng.annotations.*;

import static org.testng.AssertJUnit.*;

import java.util.*;

public class AccusationTest {

    @Test
    public void testCardsImmutable() {
        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(Card.Kitchen);
        cards.add(Card.Plum);
        cards.add(Card.Pipe);
        Accusation a = new Accusation("Mike", cards);
        boolean exceptionOccured = false;
        try {
            a.cards.add(Card.Study);
            assertEquals(3, a.cards.size());
        } catch (Exception e) {
            exceptionOccured = true;
        }
        assertTrue(exceptionOccured);
    }
}
