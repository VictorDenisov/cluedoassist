package com.cluedoassist;

import org.testng.annotations.*;

import static org.testng.AssertJUnit.*;

import java.util.ArrayList;

public class CluedoTest {

    private ArrayList<String> players;

    private String P1 = "P1";

    private Cluedo cluedo;

    @BeforeTest
    public void setUp() {
        ArrayList<String> players = new ArrayList<String>();
        players.add(P1);
        cluedo = new Cluedo(players);
    }

    @Test
    public void testTurn() throws UnknownPlayerException {
        ArrayList<Card> askedCards = new ArrayList<Card>();
        askedCards.add(Card.Scarlett);
        ArrayList<Reply> replies = new ArrayList<Reply>();
        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(Cluedo.ME, askedCards, replies);
        String[][] table = cluedo.getTable();
        assertEquals("-", table[Card.Scarlett.cardNumber() + 1][4]);
    }

    @Test
    public void testTurnLogEntry() throws UnknownPlayerException {
        ArrayList<Card> askedCards = new ArrayList<Card>();
        askedCards.add(Card.Scarlett);
        ArrayList<Reply> replies = new ArrayList<Reply>();
        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(new LogEntry(Cluedo.ME, askedCards, replies));
        String[][] table = cluedo.getTable();
        assertEquals("-", table[Card.Scarlett.cardNumber() + 1][4]);
    }

    @Test
    public void testTurnLogEntryBig() throws UnknownPlayerException {
        ArrayList<Card> askedCards = new ArrayList<Card>();
        askedCards.add(Card.Scarlett);
        askedCards.add(Card.Plum);
        askedCards.add(Card.Candle);
        ArrayList<Reply> replies = new ArrayList<Reply>();
        replies.add(new Reply(P1, CardReply.NoCard()));
        cluedo.makeTurn(new LogEntry(Cluedo.ME, askedCards, replies));
        String[][] table = cluedo.getTable();
        assertEquals("-", table[Card.Scarlett.cardNumber() + 1][4]);
        assertEquals("-", table[Card.Plum.cardNumber() + 1][4]);
        assertEquals("-", table[Card.Candle.cardNumber() + 1][4]);
    }

    @Test
    public void testTurnOneUnknown() throws UnknownPlayerException {
    }
}
