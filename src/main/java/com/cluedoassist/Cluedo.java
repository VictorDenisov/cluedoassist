package com.cluedoassist;

import java.util.ArrayList;
import java.util.List;

public class Cluedo {
    private ArrayList<Player> players;

    private static int ENV_COL = 0;
    private static int OUT_COL = 1;

    private int currentPlayer;

    private Resolution[][] table;

    public int cardCount = Card.values().length;

    ArrayList<LogEntry> log;

    public Cluedo(List<Player> players) {
        this.players = new ArrayList<Player>(players);
        this.currentPlayer = 0;
        table = new Resolution[cardCount][];
        for (int i = 0; i < cardCount; ++i) {
            table[i] = new Resolution[players.size() + 2];
        }
        log = new ArrayList<LogEntry>();
    }

    public void makeTurn(Player asker, List<Card> askedCards, List<Reply> replies) throws UnknownPlayerException {
        log.add(new LogEntry(asker, askedCards, replies));
        rectifyTable();
    }

    private void rectifyTable() throws UnknownPlayerException {
        for (LogEntry logEntry : log) {
            markRepliersHave(logEntry);
            markReplierHasNoCards(logEntry);
            markOnlyOneUnknown(logEntry);
        }
    }

    private void markRepliersHave(LogEntry le) throws UnknownPlayerException {
        for (Reply r : le.replies) {
            int playerNumber = r.replier.ord(players);

            int cardNumber = r.cardReply.cardNumber();
            if (cardNumber < 0) {
                continue;
            }
            table[cardNumber][playerNumber] = Resolution.Plus;
        }
    }

    private void markReplierHasNoCards(LogEntry le) throws UnknownPlayerException {
        for (Reply r : le.replies) {
            int playerNumber = r.replier.ord(players);

            if (r.cardReply == CardReply.NoCard()) {
                for (Card c : le.askedCards) {
                    int cardNumber = c.cardNumber();
                    table[cardNumber][playerNumber] = Resolution.Minus;
                }
            }
        }
    }

    private void markOnlyOneUnknown(LogEntry le) throws UnknownPlayerException {
        for (Reply r : le.replies) {
            int playerNumber = r.replier.ord(players);

            if (r.cardReply != CardReply.NoCard()) {
                List<Card> s = allPlusCards(playerNumber);
                s.addAll(allUnknownCards(playerNumber));
                s.retainAll(le.askedCards);
                if (s.size() == 1) {
                    int cardNumber = s.get(0).cardNumber();
                    table[cardNumber][playerNumber] = Resolution.Plus;
                }
            }
        }
    }

    private List<Card> allPlusCards(int playerNumber) {
        ArrayList<Card> result = new ArrayList<Card>();
        for (int i = 0; i < cardCount; ++i) {
            if (table[i][playerNumber] == Resolution.Plus) {
                result.add(Card.values()[i]);
            }
        }
        return result;
    }

    private List<Card> allUnknownCards(int playerNumber) {
        ArrayList<Card> result = new ArrayList<Card>();
        for (int i = 0; i < cardCount; ++i) {
            if (table[i][playerNumber] == Resolution.Unknown) {
                result.add(Card.values()[i]);
            }
        }
        return result;
    }

}
