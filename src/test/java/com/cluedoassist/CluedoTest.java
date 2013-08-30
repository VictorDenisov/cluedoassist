package com.cluedoassist;

import org.testng.annotations.*;

import static org.testng.AssertJUnit.*;

import java.util.ArrayList;

public class CluedoTest {

    private ArrayList<String> players;

    private ArrayList<Card> askedCards;

    private ArrayList<Reply> replies;

    private String P1 = "P1";

    private Cluedo cluedo;

    @BeforeMethod
    public void setUp() {
        ArrayList<String> players = new ArrayList<String>();
        players.add(P1);
        cluedo = new Cluedo(players);
        askedCards = new ArrayList<Card>();
        replies = new ArrayList<Reply>();
    }

    @Test
    public void testTurn() throws UnknownPlayerException {
        askedCards.add(Card.Scarlett);
        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(Cluedo.ME, askedCards, replies);
        String[][] table = cluedo.getTable();
        assertEquals("-", table[Card.Scarlett.cardNumber() + 1][4]);
    }

    @Test
    public void testTurnLogEntry() throws UnknownPlayerException {
        askedCards.add(Card.Scarlett);
        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(new LogEntry(Cluedo.ME, askedCards, replies));
        String[][] table = cluedo.getTable();
        assertEquals("-", table[Card.Scarlett.cardNumber() + 1][4]);
    }

    @Test
    public void testTurnLogEntryBig() throws UnknownPlayerException {
        askedCards.add(Card.Scarlett);
        askedCards.add(Card.Plum);
        askedCards.add(Card.Candle);

        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(new LogEntry(Cluedo.ME, askedCards, replies));
        String[][] table = cluedo.getTable();
        assertEquals("-", table[Card.Scarlett.cardNumber() + 1][4]);
        assertEquals("-", table[Card.Plum.cardNumber() + 1][4]);
        assertEquals("-", table[Card.Candle.cardNumber() + 1][4]);
    }

    @Test
    public void testTurnOneUnknown() throws UnknownPlayerException {
        // Turn 1
        askedCards.add(Card.Scarlett);
        askedCards.add(Card.Plum);
        askedCards.add(Card.Candle);

        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(new LogEntry(Cluedo.ME, askedCards, replies));

        // Turn 2
        askedCards = new ArrayList<Card>();
        replies = new ArrayList<Reply>();
        askedCards.add(Card.Scarlett);
        askedCards.add(Card.Plum);
        askedCards.add(Card.Knife); // One card different

        replies.add(new Reply(P1, CardReply.UnknownCard()));
        cluedo.makeTurn(new LogEntry(Cluedo.ME, askedCards, replies));
        String[][] table = cluedo.getTable();

        assertEquals("+", table[Card.Knife.cardNumber() + 1][4]);
    }

    @Test
    public void testTurnOneUnknownInTheBeginning() throws UnknownPlayerException {
        // Turn 1
        askedCards.add(Card.Scarlett);
        askedCards.add(Card.Plum);
        askedCards.add(Card.Knife); // One card different

        replies.add(new Reply(P1, CardReply.UnknownCard()));
        cluedo.makeTurn(new LogEntry(Cluedo.ME, askedCards, replies));

        // Turn 2
        askedCards = new ArrayList<Card>();
        replies = new ArrayList<Reply>();
        askedCards.add(Card.Scarlett);
        askedCards.add(Card.Plum);
        askedCards.add(Card.Candle);

        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(new LogEntry(Cluedo.ME, askedCards, replies));
        String[][] table = cluedo.getTable();

        assertEquals("+", table[Card.Knife.cardNumber() + 1][4]);
    }
}
