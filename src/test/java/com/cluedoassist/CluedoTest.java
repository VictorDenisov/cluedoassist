/*
 * Copyright 2013 Victor Denisov
 *
 * This file is part of Cluedo Assistant.
 *
 * Cluedo Assistant is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cluedo Assistant is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Cluedo Assistant.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cluedoassist;

import org.testng.annotations.*;

import static org.testng.AssertJUnit.*;

import java.util.*;

public class CluedoTest {

    private ArrayList<String> players;

    private CardSet sampleCardSet = CardSet.suspects(Arrays.asList(new String[]{
    "Scarlett",
    "Mustard",
    "White",
    "Green",
    "Peacock",
    "Plum"}))
        .weapons(Arrays.asList(new String[]{
    "Candle",
    "Knife",
    "Pipe",
    "Revolver",
    "Rope",
    "Wrench"}))
        .rooms(Arrays.asList(new String[]{
    "Kitchen",
    "Billiard",
    "Dining",
    "Bathroom",
    "Study",
    "Garage",
    "Bedroom",
    "Yard",
    "Guestroom"})).create();

    private ArrayList<Card> askedCards;

    private ArrayList<Reply> replies;

    private String P1 = "P1";
    private String P2 = "P2";
    private String P3 = "P3";

    private Cluedo cluedo;

    @BeforeMethod
    public void setUp() {
        ArrayList<String> players = new ArrayList<String>();
        players.add(P1);
        players.add(P2);
        players.add(P3);
        cluedo = new CluedoSmart(sampleCardSet, players);
        askedCards = new ArrayList<Card>();
        replies = new ArrayList<Reply>();
    }

    @Test
    public void testTurn() throws Exception {
        askedCards.add(Card.valueOf("Scarlett"));
        askedCards.add(Card.valueOf("Knife"));
        askedCards.add(Card.valueOf("Kitchen"));
        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(Cluedo.ME, askedCards, replies);
        String[][] table = cluedo.getTable();
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Scarlett")) + 1][4]);
    }

    @Test
    public void testUnsuccessfulAccusation() throws Exception {
        cluedo.setCard(Cluedo.ENVELOPE, Card.valueOf("Plum"));
        cluedo.setCard(Cluedo.ENVELOPE, Card.valueOf("Wrench"));

        askedCards.add(Card.valueOf("Plum"));
        askedCards.add(Card.valueOf("Wrench"));
        askedCards.add(Card.valueOf("Kitchen"));
        cluedo.makeAccusation(new Accusation(Cluedo.ME, askedCards));
        String[][] table = cluedo.getTable();
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Kitchen")) + 1][1]);
    }

    @Test
    public void testTurnLogEntry() throws Exception {
        askedCards.add(Card.valueOf("Scarlett"));
        askedCards.add(Card.valueOf("Knife"));
        askedCards.add(Card.valueOf("Kitchen"));
        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(new Suggestion(Cluedo.ME, askedCards, replies));
        String[][] table = cluedo.getTable();
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Scarlett")) + 1][4]);
    }

    @Test
    public void testTurnLogEntryBig() throws Exception {
        askedCards.add(Card.valueOf("Scarlett"));
        askedCards.add(Card.valueOf("Plum"));
        askedCards.add(Card.valueOf("Candle"));

        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(new Suggestion(Cluedo.ME, askedCards, replies));
        String[][] table = cluedo.getTable();
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Scarlett")) + 1][4]);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Plum")) + 1][4]);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Candle")) + 1][4]);
    }

    @Test
    public void testTurnOneUnknown() throws Exception {
        // Turn 1
        askedCards.add(Card.valueOf("Scarlett"));
        askedCards.add(Card.valueOf("Plum"));
        askedCards.add(Card.valueOf("Candle"));

        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(new Suggestion(Cluedo.ME, askedCards, replies));

        // Turn 2
        askedCards = new ArrayList<Card>();
        replies = new ArrayList<Reply>();
        askedCards.add(Card.valueOf("Scarlett"));
        askedCards.add(Card.valueOf("Plum"));
        askedCards.add(Card.valueOf("Knife")); // One card different

        replies.add(new Reply(P1, CardReply.UnknownCard()));
        cluedo.makeTurn(new Suggestion(Cluedo.ME, askedCards, replies));
        String[][] table = cluedo.getTable();

        assertEquals("+", table[sampleCardSet.ordinal(Card.valueOf("Knife")) + 1][4]);
    }

    @Test
    public void testTurnOneUnknownInTheBeginning() throws Exception {
        // Turn 1
        askedCards.add(Card.valueOf("Scarlett"));
        askedCards.add(Card.valueOf("Plum"));
        askedCards.add(Card.valueOf("Knife")); // One card different

        replies.add(new Reply(P1, CardReply.UnknownCard()));
        cluedo.makeTurn(new Suggestion(Cluedo.ME, askedCards, replies));

        // Turn 2
        askedCards = new ArrayList<Card>();
        replies = new ArrayList<Reply>();
        askedCards.add(Card.valueOf("Scarlett"));
        askedCards.add(Card.valueOf("Plum"));
        askedCards.add(Card.valueOf("Candle"));

        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(new Suggestion(Cluedo.ME, askedCards, replies));
        String[][] table = cluedo.getTable();

        assertEquals("+", table[sampleCardSet.ordinal(Card.valueOf("Knife")) + 1][4]);
    }

    @Test
    public void testAllReplyNo() throws Exception {
        cluedo.setCard(Cluedo.OUT, Card.valueOf("Plum"));
        cluedo.setCard(Cluedo.OUT, Card.valueOf("Pipe"));

        cluedo.setCard(Cluedo.ME, Card.valueOf("White"));
        cluedo.setCard(Cluedo.ME, Card.valueOf("Peacock"));
        cluedo.setCard(Cluedo.ME, Card.valueOf("Bathroom"));
        cluedo.setCard(Cluedo.ME, Card.valueOf("Billiard"));

        askedCards.add(Card.valueOf("Scarlett"));
        askedCards.add(Card.valueOf("Revolver"));
        askedCards.add(Card.valueOf("Kitchen"));

        replies.add(new Reply(P1, CardReply.NoCard()));
        replies.add(new Reply(P2, CardReply.NoCard()));
        replies.add(new Reply(P3, CardReply.NoCard()));

        cluedo.makeTurn(new Suggestion(Cluedo.ME, askedCards, replies));

        String[][] table = cluedo.getTable();
        assertEquals("+", table[sampleCardSet.ordinal(Card.valueOf("Scarlett")) + 1][1]);
        assertEquals("+", table[sampleCardSet.ordinal(Card.valueOf("Revolver")) + 1][1]);
        assertEquals("+", table[sampleCardSet.ordinal(Card.valueOf("Kitchen")) + 1][1]);
    }

    @Test
    public void testAllButOneSuspectsEliminated() throws Exception {
        // All but Scarlett
        cluedo.setCard(Cluedo.ME, Card.valueOf("Mustard"));
        cluedo.setCard(Cluedo.ME, Card.valueOf("White"));
        cluedo.setCard(Cluedo.ME, Card.valueOf("Green"));
        cluedo.setCard(Cluedo.ME, Card.valueOf("Peacock"));
        cluedo.setCard(Cluedo.OUT, Card.valueOf("Plum"));

        String[][] table = cluedo.getTable();
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Mustard")) + 1][1]);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("White")) + 1][1]);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Green")) + 1][1]);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Peacock")) + 1][1]);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Plum")) + 1][1]);
        assertEquals("+", table[sampleCardSet.ordinal(Card.valueOf("Scarlett")) + 1][1]);
    }

    @Test
    public void testOnePlusInAGroup() throws Exception {
        // All but Scarlett
        cluedo.setCard(Cluedo.ME, Card.valueOf("Mustard"));
        cluedo.setCard(Cluedo.ME, Card.valueOf("White"));
        cluedo.setCard(Cluedo.ME, Card.valueOf("Green"));
        cluedo.setCard(Cluedo.ME, Card.valueOf("Peacock"));
        cluedo.setCard(Cluedo.OUT, Card.valueOf("Plum"));
        cluedo.setCard(Cluedo.OUT, Card.valueOf("Wrench"));

        askedCards.add(Card.valueOf("Mustard"));
        askedCards.add(Card.valueOf("Plum"));
        askedCards.add(Card.valueOf("Kitchen"));

        replies.add(new Reply(P1, CardReply.NoCard()));
        replies.add(new Reply(P2, CardReply.NoCard()));
        replies.add(new Reply(P3, CardReply.NoCard()));

        cluedo.makeTurn(new Suggestion(Cluedo.ME, askedCards, replies));

        String[][] table = cluedo.getTable();
        assertEquals("+", table[sampleCardSet.ordinal(Card.valueOf("Kitchen")) + 1][1]);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Billiard")) + 1][1]);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Dining")) + 1][1]);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Bathroom")) + 1][1]);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Study")) + 1][1]);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Garage")) + 1][1]);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Bedroom")) + 1][1]);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Yard")) + 1][1]);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Guestroom")) + 1][1]);
    }

    @Test
    public void testContradiction() throws Exception {
        cluedo.setCard(Cluedo.ME, Card.valueOf("Mustard"));
        boolean exceptionHadPlace = false;
        try {
            cluedo.setCard(Cluedo.OUT, Card.valueOf("Mustard"));
        } catch (ContradictionException e) {
            exceptionHadPlace = true;
        }
        String[][] table = cluedo.getTable();

        assertTrue(exceptionHadPlace);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Mustard")) + 1][1]);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Mustard")) + 1][2]);
        assertEquals("+", table[sampleCardSet.ordinal(Card.valueOf("Mustard")) + 1][3]);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Mustard")) + 1][4]);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Mustard")) + 1][5]);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Mustard")) + 1][6]);
    }

    @Test
    public void testThreeCardsReplied() throws Exception {
        askedCards.add(Card.valueOf("Mustard"));
        askedCards.add(Card.valueOf("Knife"));
        askedCards.add(Card.valueOf("Kitchen"));

        replies.add(new Reply(P1, CardReply.UnknownCard()));
        replies.add(new Reply(P2, CardReply.UnknownCard()));
        replies.add(new Reply(P3, CardReply.UnknownCard()));
        cluedo.makeTurn(new Suggestion(Cluedo.ME, askedCards, replies));

        String[][] table = cluedo.getTable();

        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Mustard")) + 1][1]);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Knife")) + 1][1]);
        assertEquals("-", table[sampleCardSet.ordinal(Card.valueOf("Kitchen")) + 1][1]);
    }

    @Test
    public void testPossibleCardReplies() throws Exception {
        cluedo.setCard(P1, Card.valueOf("Mustard"));
        cluedo.setCard(Cluedo.ME, Card.valueOf("Kitchen"));

        Card[] cards = new Card[3];
        cards[0] = Card.valueOf("Mustard");
        cards[1] = Card.valueOf("Knife");
        cards[2] = Card.valueOf("Kitchen");

        List<CardReply> r = cluedo.possibleCardReplies(P1, cards);
        assertEquals(CardReply.UNKNOWN_INT, r.get(0).ordinal(sampleCardSet));
        assertEquals(sampleCardSet.ordinal(Card.valueOf("Mustard")), r.get(1).ordinal(sampleCardSet));
        assertEquals(sampleCardSet.ordinal(Card.valueOf("Knife")), r.get(2).ordinal(sampleCardSet));
        assertEquals(3, r.size()); // There is no Kitchen card.
    }
}
