package com.cluedoassist;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.util.Collections;

public class Cluedo implements Serializable {
    private ArrayList<String> players;
    private int playerCount;

    private static int ENV_COL = 0;
    private static int OUT_COL = 1;
    private static int ME_COL = 2;

    public static String ENVELOPE = "Env";
    public static String OUT = "Out";
    public static String ME = "Me";

    private int currentPlayer;

    private Resolution[][] table;

    private int[] cardCountPerPlayer;

    public int cardCount = Card.values().length;

    ArrayList<LogEntry> log;

    public List<LogEntry> getLog() {
        return Collections.unmodifiableList(log);
    }

    public List<String> getCompartments() {
        ArrayList<String> result = new ArrayList<String>();
        result.add(ENVELOPE);
        result.add(OUT);
        result.addAll(getPlayers());
        return result;
    }

    public List<String> getPlayers() {
        return new ArrayList<String>(players);
    }

    public Cluedo(List<String> players) {
        this.playerCount = players.size() + 1;
        this.players = new ArrayList<String>();
        this.players.add(ME);
        this.players.addAll(players);
        this.currentPlayer = 0;
        table = new Resolution[cardCount][];
        for (int i = 0; i < cardCount; ++i) {
            table[i] = new Resolution[playerCount + 2];
            for (int j = 0; j < table[i].length; ++j) {
                table[i][j] = Resolution.Unknown;
            }
        }
        log = new ArrayList<LogEntry>();

        setCardCount();

        solvePlayerHasAllCards(OUT_COL);
    }

    private void setCardCount() {
        int cardCountPerOnePlayer = (cardCount - 3) / playerCount;
        int outCount = (cardCount - 3) % playerCount;

        cardCountPerPlayer = new int[playerCount + 2];
        cardCountPerPlayer[ENV_COL] = 3;
        cardCountPerPlayer[OUT_COL] = outCount;
        for (int i = 2; i < cardCountPerPlayer.length; ++i) {
            cardCountPerPlayer[i] = cardCountPerOnePlayer;
        }
    }

    public String[][] getTable() {
        String[][] result = new String[table.length + 1][];
        for (int i = 0; i < result.length; ++i) {
            result[i] = new String[playerCount + 3];
        }
        // write titles
        result[0][ENV_COL + 1] = "Env";
        result[0][OUT_COL + 1] = "Out";
        for (int j = 0; j < playerCount; ++j) {
            result[0][j + OUT_COL + 2] = players.get(j);
        }
        for (int i = 0; i < table.length; ++i) {
            result[i + 1][0] = Card.values()[i].toString();
            for (int j = 0; j < table[i].length; ++j) {
                switch (table[i][j]) {
                case Plus : result[i + 1][j + 1] = "+"; break;
                case Minus : result[i + 1][j + 1] = "-"; break;
                case Unknown : result[i + 1][j + 1] = "?"; break;
                }
            }
        }
        return result;
    }

    private boolean setPlus(int cardNumber, int playerNumber) {
        boolean tableModified = false;
        if (table[cardNumber][playerNumber] == Resolution.Unknown) {
            tableModified = true;
        }
        table[cardNumber][playerNumber] = Resolution.Plus;
        for (int i = 0; i < table[cardNumber].length; ++i) {
            if (i != playerNumber) {
                setMinus(cardNumber, i);
            }
        }
        return tableModified;
    }

    private boolean setMinus(int cardNumber, int playerNumber) {
        boolean tableModified = false;
        if (table[cardNumber][playerNumber] == Resolution.Unknown) {
            tableModified = true;
        }
        table[cardNumber][playerNumber] = Resolution.Minus;
        return tableModified;
    }

    public void setCard(String asker, Card card) throws UnknownPlayerException {
        //log.add(new SetCard(asker, card));
        int playerNumber = playerOrd(asker);
        int cardNumber = card.cardNumber();
        setPlus(cardNumber, playerNumber);
        inferenceCycle();
    }

    public void makeTurn(String asker, List<Card> askedCards, List<Reply> replies) throws UnknownPlayerException {
        log.add(new LogEntry(asker, askedCards, replies));
        inferenceCycle();
    }

    public void makeTurn(LogEntry l) throws UnknownPlayerException {
        log.add(l);
        inferenceCycle();
    }

    private boolean processLog() throws UnknownPlayerException {
        boolean tableModified = false;
        for (LogEntry logEntry : log) {
            tableModified = tableModified || solveRepliersHave(logEntry);
            tableModified = tableModified || solveReplierHasNoCards(logEntry);
            tableModified = tableModified || solveOnlyOneUnknown(logEntry);
        }
        return tableModified;
    }

    private boolean rectifyTable() {
        boolean tableModified = false;
        for (int i = 0; i < table[0].length; ++i) {
            tableModified = tableModified || solvePlayerHasAllCards(i);
        }
        for (int i = 0; i < table.length; ++i) {
            tableModified = tableModified || solveLineOneNonNegative(i);
        }
        return tableModified;
    }

    private void inferenceCycle() throws UnknownPlayerException {
        for (;;) {
            boolean tableModified = false;
            tableModified = tableModified || processLog();
            tableModified = tableModified || rectifyTable();
            if (!tableModified) {
                break;
            }
        }
    }

    public int playerOrd(String player) throws UnknownPlayerException {
        if (ENVELOPE.equals(player)) {
            return ENV_COL;
        } else if (OUT.equals(player)) {
            return OUT_COL;
        } else {
            for (int i = 0; i < playerCount; ++i) {
                if (player.equals(players.get(i))) {
                    return i + 2;
                }
            }
            throw new UnknownPlayerException("Unknown player : " + player);
        }
    }

    /* Processes the situation when replier shows known card. */
    private boolean solveRepliersHave(LogEntry le) throws UnknownPlayerException {
        boolean tableModified = false;
        for (Reply r : le.replies) {
            int playerNumber = playerOrd(r.replier);

            int cardNumber = r.cardReply.cardNumber();
            if (cardNumber < 0) {
                continue;
            }
            tableModified = tableModified || setPlus(cardNumber, playerNumber);
        }
        return tableModified;
    }

    /* Processes the situation when replier shows no cards. */
    private boolean solveReplierHasNoCards(LogEntry le) throws UnknownPlayerException {
        boolean tableModified = false;
        for (Reply r : le.replies) {
            int playerNumber = playerOrd(r.replier);

            if (r.cardReply.cardNumber() == -2) {
                for (Card c : le.askedCards) {
                    int cardNumber = c.cardNumber();
                    tableModified = tableModified || setMinus(cardNumber, playerNumber);
                }
            }
        }
        return tableModified;
    }

    private boolean solveOnlyOneUnknown(LogEntry le) throws UnknownPlayerException {
        boolean tableModified = false;
        for (Reply r : le.replies) {
            int playerNumber = playerOrd(r.replier);

            if (r.cardReply.cardNumber() == -1) {
                List<Card> s = allPlusCards(playerNumber);
                s.addAll(allUnknownCards(playerNumber));
                s.retainAll(le.askedCards);
                if (s.size() == 1) {
                    int cardNumber = s.get(0).cardNumber();
                    tableModified = tableModified || setPlus(cardNumber, playerNumber);
                }
            }
        }
        return tableModified;
    }

    private int plusCountOfPlayer(int playerNumber) {
        int count = 0;
        for (int i = 0; i < cardCount; ++i) {
            if (table[i][playerNumber] == Resolution.Plus) {
                ++count;
            }
        }
        return count;
    }

    private boolean solveLineOneNonNegative(int cardNumber) {
        int nonNegativeCount = 0;
        for (int i = 0; i < table[cardNumber].length; ++i) {
            if (table[cardNumber][i] != Resolution.Minus) {
                ++nonNegativeCount;
            }
        }
        boolean tableModified = false;
        if (nonNegativeCount == 1) {
            for (int i = 0; i < table[cardNumber].length; ++i) {
                if (table[cardNumber][i] == Resolution.Unknown) {
                    tableModified = tableModified || setPlus(cardNumber, i);
                }
            }
        }
        return tableModified;
    }

    private boolean solvePlayerHasAllCards(int playerNumber) {
        boolean tableModified = false;
        if (plusCountOfPlayer(playerNumber)
                    == cardCountPerPlayer[playerNumber]) {

            for (int i = 0; i < cardCount; ++i) {
                if (table[i][playerNumber] == Resolution.Unknown) {
                    tableModified = true;
                    setMinus(i, playerNumber);
                }
            }
        }
        return tableModified;
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
