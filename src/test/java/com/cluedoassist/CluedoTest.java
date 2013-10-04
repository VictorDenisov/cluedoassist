package com.cluedoassist;

import org.testng.annotations.*;

import static org.testng.AssertJUnit.*;

import java.util.*;

public class CluedoTest {

    private ArrayList<String> players;

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
        cluedo = new CluedoSmart(players);
        askedCards = new ArrayList<Card>();
        replies = new ArrayList<Reply>();
    }

    @Test
    public void testTurn() throws Exception {
        askedCards.add(Card.Scarlett);
        askedCards.add(Card.Knife);
        askedCards.add(Card.Kitchen);
        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(Cluedo.ME, askedCards, replies);
        String[][] table = cluedo.getTable();
        assertEquals("-", table[Card.Scarlett.ordinal() + 1][4]);
    }

    @Test
    public void testUnsuccessfulAccusation() throws Exception {
        cluedo.setCard(Cluedo.ENVELOPE, Card.Plum);
        cluedo.setCard(Cluedo.ENVELOPE, Card.Wrench);

        askedCards.add(Card.Plum);
        askedCards.add(Card.Wrench);
        askedCards.add(Card.Kitchen);
        cluedo.makeAccusation(new Accusation(Cluedo.ME, askedCards));
        String[][] table = cluedo.getTable();
        assertEquals("-", table[Card.Kitchen.ordinal() + 1][1]);
    }

    @Test
    public void testTurnLogEntry() throws Exception {
        askedCards.add(Card.Scarlett);
        askedCards.add(Card.Knife);
        askedCards.add(Card.Kitchen);
        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(new Suggestion(Cluedo.ME, askedCards, replies));
        String[][] table = cluedo.getTable();
        assertEquals("-", table[Card.Scarlett.ordinal() + 1][4]);
    }

    @Test
    public void testTurnLogEntryBig() throws Exception {
        askedCards.add(Card.Scarlett);
        askedCards.add(Card.Plum);
        askedCards.add(Card.Candle);

        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(new Suggestion(Cluedo.ME, askedCards, replies));
        String[][] table = cluedo.getTable();
        assertEquals("-", table[Card.Scarlett.ordinal() + 1][4]);
        assertEquals("-", table[Card.Plum.ordinal() + 1][4]);
        assertEquals("-", table[Card.Candle.ordinal() + 1][4]);
    }

    @Test
    public void testTurnOneUnknown() throws Exception {
        // Turn 1
        askedCards.add(Card.Scarlett);
        askedCards.add(Card.Plum);
        askedCards.add(Card.Candle);

        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(new Suggestion(Cluedo.ME, askedCards, replies));

        // Turn 2
        askedCards = new ArrayList<Card>();
        replies = new ArrayList<Reply>();
        askedCards.add(Card.Scarlett);
        askedCards.add(Card.Plum);
        askedCards.add(Card.Knife); // One card different

        replies.add(new Reply(P1, CardReply.UnknownCard()));
        cluedo.makeTurn(new Suggestion(Cluedo.ME, askedCards, replies));
        String[][] table = cluedo.getTable();

        assertEquals("+", table[Card.Knife.ordinal() + 1][4]);
    }

    @Test
    public void testTurnOneUnknownInTheBeginning() throws Exception {
        // Turn 1
        askedCards.add(Card.Scarlett);
        askedCards.add(Card.Plum);
        askedCards.add(Card.Knife); // One card different

        replies.add(new Reply(P1, CardReply.UnknownCard()));
        cluedo.makeTurn(new Suggestion(Cluedo.ME, askedCards, replies));

        // Turn 2
        askedCards = new ArrayList<Card>();
        replies = new ArrayList<Reply>();
        askedCards.add(Card.Scarlett);
        askedCards.add(Card.Plum);
        askedCards.add(Card.Candle);

        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(new Suggestion(Cluedo.ME, askedCards, replies));
        String[][] table = cluedo.getTable();

        assertEquals("+", table[Card.Knife.ordinal() + 1][4]);
    }

    @Test
    public void testAllReplyNo() throws Exception {
        cluedo.setCard(Cluedo.OUT, Card.Plum);
        cluedo.setCard(Cluedo.OUT, Card.Pipe);

        cluedo.setCard(Cluedo.ME, Card.White);
        cluedo.setCard(Cluedo.ME, Card.Peacock);
        cluedo.setCard(Cluedo.ME, Card.Bathroom);
        cluedo.setCard(Cluedo.ME, Card.Billiard);

        askedCards.add(Card.Scarlett);
        askedCards.add(Card.Revolver);
        askedCards.add(Card.Kitchen);

        replies.add(new Reply(P1, CardReply.NoCard()));
        replies.add(new Reply(P2, CardReply.NoCard()));
        replies.add(new Reply(P3, CardReply.NoCard()));

        cluedo.makeTurn(new Suggestion(Cluedo.ME, askedCards, replies));

        String[][] table = cluedo.getTable();
        assertEquals("+", table[Card.Scarlett.ordinal() + 1][1]);
        assertEquals("+", table[Card.Revolver.ordinal() + 1][1]);
        assertEquals("+", table[Card.Kitchen.ordinal() + 1][1]);
    }

    @Test
    public void testAllButOneSuspectsEliminated() throws Exception {
        // All but Scarlett
        cluedo.setCard(Cluedo.ME, Card.Mustard);
        cluedo.setCard(Cluedo.ME, Card.White);
        cluedo.setCard(Cluedo.ME, Card.Green);
        cluedo.setCard(Cluedo.ME, Card.Peacock);
        cluedo.setCard(Cluedo.OUT, Card.Plum);

        String[][] table = cluedo.getTable();
        assertEquals("-", table[Card.Mustard.ordinal() + 1][1]);
        assertEquals("-", table[Card.White.ordinal() + 1][1]);
        assertEquals("-", table[Card.Green.ordinal() + 1][1]);
        assertEquals("-", table[Card.Peacock.ordinal() + 1][1]);
        assertEquals("-", table[Card.Plum.ordinal() + 1][1]);
        assertEquals("+", table[Card.Scarlett.ordinal() + 1][1]);
    }

    @Test
    public void testOnePlusInAGroup() throws Exception {
        // All but Scarlett
        cluedo.setCard(Cluedo.ME, Card.Mustard);
        cluedo.setCard(Cluedo.ME, Card.White);
        cluedo.setCard(Cluedo.ME, Card.Green);
        cluedo.setCard(Cluedo.ME, Card.Peacock);
        cluedo.setCard(Cluedo.OUT, Card.Plum);
        cluedo.setCard(Cluedo.OUT, Card.Wrench);

        askedCards.add(Card.Mustard);
        askedCards.add(Card.Plum);
        askedCards.add(Card.Kitchen);

        replies.add(new Reply(P1, CardReply.NoCard()));
        replies.add(new Reply(P2, CardReply.NoCard()));
        replies.add(new Reply(P3, CardReply.NoCard()));

        cluedo.makeTurn(new Suggestion(Cluedo.ME, askedCards, replies));

        String[][] table = cluedo.getTable();
        assertEquals("+", table[Card.Kitchen.ordinal() + 1][1]);
        assertEquals("-", table[Card.Billiard.ordinal() + 1][1]);
        assertEquals("-", table[Card.Dining.ordinal() + 1][1]);
        assertEquals("-", table[Card.Bathroom.ordinal() + 1][1]);
        assertEquals("-", table[Card.Study.ordinal() + 1][1]);
        assertEquals("-", table[Card.Garage.ordinal() + 1][1]);
        assertEquals("-", table[Card.Bedroom.ordinal() + 1][1]);
        assertEquals("-", table[Card.Yard.ordinal() + 1][1]);
        assertEquals("-", table[Card.Guestroom.ordinal() + 1][1]);
    }

    @Test
    public void testContradiction() throws Exception {
        cluedo.setCard(Cluedo.ME, Card.Mustard);
        boolean exceptionHadPlace = false;
        try {
            cluedo.setCard(Cluedo.OUT, Card.Mustard);
        } catch (ContradictionException e) {
            exceptionHadPlace = true;
        }
        String[][] table = cluedo.getTable();

        assertTrue(exceptionHadPlace);
        assertEquals("-", table[Card.Mustard.ordinal() + 1][1]);
        assertEquals("-", table[Card.Mustard.ordinal() + 1][2]);
        assertEquals("+", table[Card.Mustard.ordinal() + 1][3]);
        assertEquals("-", table[Card.Mustard.ordinal() + 1][4]);
        assertEquals("-", table[Card.Mustard.ordinal() + 1][5]);
        assertEquals("-", table[Card.Mustard.ordinal() + 1][6]);
    }

    @Test
    public void testThreeCardsReplied() throws Exception {
        askedCards.add(Card.Mustard);
        askedCards.add(Card.Knife);
        askedCards.add(Card.Kitchen);

        replies.add(new Reply(P1, CardReply.UnknownCard()));
        replies.add(new Reply(P2, CardReply.UnknownCard()));
        replies.add(new Reply(P3, CardReply.UnknownCard()));
        cluedo.makeTurn(new Suggestion(Cluedo.ME, askedCards, replies));

        String[][] table = cluedo.getTable();

        assertEquals("-", table[Card.Mustard.ordinal() + 1][1]);
        assertEquals("-", table[Card.Knife.ordinal() + 1][1]);
        assertEquals("-", table[Card.Kitchen.ordinal() + 1][1]);
    }

    @Test
    public void testPossibleCardReplies() throws Exception {
        cluedo.setCard(P1, Card.Mustard);
        cluedo.setCard(Cluedo.ME, Card.Kitchen);

        Card[] cards = new Card[3];
        cards[0] = Card.Mustard;
        cards[1] = Card.Knife;
        cards[2] = Card.Kitchen;

        List<CardReply> r = cluedo.possibleCardReplies(P1, cards);
        assertEquals(CardReply.UNKNOWN_INT, r.get(0).ordinal());
        assertEquals(Card.Mustard.ordinal(), r.get(1).ordinal());
        assertEquals(Card.Knife.ordinal(), r.get(2).ordinal());
        assertEquals(3, r.size()); // There is no Kitchen card.
    }
}
