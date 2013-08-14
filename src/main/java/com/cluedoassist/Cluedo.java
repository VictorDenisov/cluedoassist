package com.cluedoassist;

import java.util.ArrayList;

public class Cluedo {
    private Player[] players;

    private static int ENV_COL = 0;
    private static int OUT_COL = 1;

    private int currentPlayer;

    private Resolution[][] table;

    public int cardCount = Card.values().length;

    ArrayList<LogEntry> log;

    public Cluedo(Player[] players) {
        this.players = new Player[players.length];
        System.arraycopy(players, 0, this.players, 0, players.length);
        this.currentPlayer = 0;
        table = new Resolution[cardCount][];
        for (int i = 0; i < cardCount; ++i) {
            table[i] = new Resolution[players.length + 2];
        }
        log = new ArrayList<LogEntry>();
    }

    public void makeTurn(Player asker, Card[] askedCards, Reply[] replies) throws UnknownPlayerException {
        log.add(new LogEntry(asker, askedCards, replies));
        rectifyTable();
    }

    private void rectifyTable() throws UnknownPlayerException {
        for (LogEntry logEntry : log) {
            markRepliersHave(logEntry);
        }
    }

    private void markRepliersHave(LogEntry le) throws UnknownPlayerException {
        for (Reply r : le.replies) {
            int playerNumber = r.replier.ord(players);
            if (playerNumber < 0) {
                throw new UnknownPlayerException("Unknown replier : " + r.replier.name);
            }

            int cardNumber = r.cardReply.cardNumber();
            if (cardNumber < 0) {
                continue;
            }
            table[cardNumber][playerNumber] = Resolution.Plus;
        }
    }

}
