package com.cluedoassist;

import org.testng.annotations.*;

import static org.testng.AssertJUnit.*;

import java.util.ArrayList;

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
        cluedo = new Cluedo(players);
        askedCards = new ArrayList<Card>();
        replies = new ArrayList<Reply>();
    }

    @Test
    public void testTurn() throws Exception {
        askedCards.add(Card.Scarlett);
        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(Cluedo.ME, askedCards, replies);
        String[][] table = cluedo.getTable();
        assertEquals("-", table[Card.Scarlett.cardNumber() + 1][4]);
    }

    @Test
    public void testTurnLogEntry() throws Exception {
        askedCards.add(Card.Scarlett);
        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(new Suggestion(Cluedo.ME, askedCards, replies));
        String[][] table = cluedo.getTable();
        assertEquals("-", table[Card.Scarlett.cardNumber() + 1][4]);
    }

    @Test
    public void testTurnLogEntryBig() throws Exception {
        askedCards.add(Card.Scarlett);
        askedCards.add(Card.Plum);
        askedCards.add(Card.Candle);

        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(new Suggestion(Cluedo.ME, askedCards, replies));
        String[][] table = cluedo.getTable();
        assertEquals("-", table[Card.Scarlett.cardNumber() + 1][4]);
        assertEquals("-", table[Card.Plum.cardNumber() + 1][4]);
        assertEquals("-", table[Card.Candle.cardNumber() + 1][4]);
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

        assertEquals("+", table[Card.Knife.cardNumber() + 1][4]);
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

        assertEquals("+", table[Card.Knife.cardNumber() + 1][4]);
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
        assertEquals("+", table[Card.Scarlett.cardNumber() + 1][1]);
        assertEquals("+", table[Card.Revolver.cardNumber() + 1][1]);
        assertEquals("+", table[Card.Kitchen.cardNumber() + 1][1]);
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
        assertEquals("-", table[Card.Mustard.cardNumber() + 1][1]);
        assertEquals("-", table[Card.White.cardNumber() + 1][1]);
        assertEquals("-", table[Card.Green.cardNumber() + 1][1]);
        assertEquals("-", table[Card.Peacock.cardNumber() + 1][1]);
        assertEquals("-", table[Card.Plum.cardNumber() + 1][1]);
        assertEquals("+", table[Card.Scarlett.cardNumber() + 1][1]);
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
        assertEquals("+", table[Card.Kitchen.cardNumber() + 1][1]);
        assertEquals("-", table[Card.Billiard.cardNumber() + 1][1]);
        assertEquals("-", table[Card.Dining.cardNumber() + 1][1]);
        assertEquals("-", table[Card.Bathroom.cardNumber() + 1][1]);
        assertEquals("-", table[Card.Study.cardNumber() + 1][1]);
        assertEquals("-", table[Card.Garage.cardNumber() + 1][1]);
        assertEquals("-", table[Card.Bedroom.cardNumber() + 1][1]);
        assertEquals("-", table[Card.Yard.cardNumber() + 1][1]);
        assertEquals("-", table[Card.Guestroom.cardNumber() + 1][1]);
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
        assertEquals("-", table[Card.Mustard.cardNumber() + 1][1]);
        assertEquals("-", table[Card.Mustard.cardNumber() + 1][2]);
        assertEquals("+", table[Card.Mustard.cardNumber() + 1][3]);
        assertEquals("-", table[Card.Mustard.cardNumber() + 1][4]);
        assertEquals("-", table[Card.Mustard.cardNumber() + 1][5]);
        assertEquals("-", table[Card.Mustard.cardNumber() + 1][6]);
    }
}
