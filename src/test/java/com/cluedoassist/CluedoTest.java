package com.cluedoassist;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;

/**
 * Unit test for simple App.
 */
public class CluedoTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CluedoTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( CluedoTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testTurn() throws UnknownPlayerException {
        ArrayList<String> players = new ArrayList<String>();
        players.add("p1");
        Cluedo c = new Cluedo(players);
        ArrayList<Card> askedCards = new ArrayList<Card>();
        askedCards.add(Card.Scarlett);
        ArrayList<Reply> replies = new ArrayList<Reply>();
        replies.add(new Reply("p1", CardReply.NoCard()));
        c.makeTurn("me", askedCards, replies);
        String[][] table = c.getTable();
        assertEquals("-", table[Card.Scarlett.cardNumber() + 1][4]);
    }

    public void testTurnLogEntry() throws UnknownPlayerException {
        ArrayList<String> players = new ArrayList<String>();
        players.add("p1");
        Cluedo c = new Cluedo(players);
        ArrayList<Card> askedCards = new ArrayList<Card>();
        askedCards.add(Card.Scarlett);
        ArrayList<Reply> replies = new ArrayList<Reply>();
        replies.add(new Reply("p1", CardReply.NoCard()));
        c.makeTurn(new LogEntry("me", askedCards, replies));
        String[][] table = c.getTable();
        assertEquals("-", table[Card.Scarlett.cardNumber() + 1][4]);
    }
}
