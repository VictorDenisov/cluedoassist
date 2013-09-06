package com.cluedoassist;

import java.util.*;

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

    private Resolution[][] backupTable;

    private ArrayList<LogEntry> backupLog;

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

        calculateCardCount();

        try {
            solvePlayerHasAllCards(OUT_COL);
        } catch (ContradictionException e) {
            // no contradiction is possible in this case
        }
    }

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
        return Collections.unmodifiableList(players);
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

    public Map<String, List<Card>> cardsShowedByMe() {
        HashMap<String, List<Card>> result = new HashMap<String, List<Card>>();
        for (int i = 0; i < playerCount; ++i) {
            if (!players.get(i).equals(ME)) {
                result.put(players.get(i), cardsShowedTo(players.get(i)));
            }
        }
        return result;
    }

    public void setCard(String asker, Card card) throws UnknownPlayerException
                                                      , ContradictionException {
        //log.add(new SetCard(asker, card));
        beginTransaction();
        try {
            log.add(new SetCard(asker, card));
            inferenceCycle();
        } catch (UnknownPlayerException upe) {
            rollBackTransaction();
            throw upe;
        } catch (ContradictionException ce) {
            rollBackTransaction();
            throw ce;
        }
        commitTransaction();
    }

    public void makeTurn( String asker
                        , List<Card> askedCards
                        , List<Reply> replies) throws UnknownPlayerException
                                                    , ContradictionException {
        makeTurn(new Suggestion(asker, askedCards, replies));
    }

    public void makeTurn(Suggestion l) throws UnknownPlayerException
                                          , ContradictionException {
        beginTransaction();
        log.add(l);
        try {
            inferenceCycle();
        } catch (UnknownPlayerException upe) {
            rollBackTransaction();
            throw upe;
        } catch (ContradictionException ce) {
            rollBackTransaction();
            throw ce;
        }
        commitTransaction();
    }

    public void makeAccusation(Accusation a) throws UnknownPlayerException
                                                  , ContradictionException {
        beginTransaction();

        try {
            log.add(a);
            inferenceCycle();
        } catch (UnknownPlayerException upe) {
            rollBackTransaction();
            throw upe;
        } catch (ContradictionException ce) {
            rollBackTransaction();
            throw ce;
        }
        commitTransaction();
    }

    private void beginTransaction() {
        backupTable = new Resolution[table.length][];
        for (int i = 0; i < table.length; ++i) {
            backupTable[i] = new Resolution[table[i].length];
            for (int j = 0; j < table[i].length; ++j) {
                backupTable[i][j] = table[i][j];
            }
        }
        backupLog = new ArrayList<LogEntry>(log);
    }

    private void commitTransaction() {
        backupTable = null;
        backupLog = null;
    }

    private void rollBackTransaction() {
        table = new Resolution[backupTable.length][];
        for (int i = 0; i < backupTable.length; ++i) {
            table[i] = new Resolution[backupTable[i].length];
            for (int j = 0; j < backupTable[i].length; ++j) {
                table[i][j] = backupTable[i][j];
            }
        }
        backupTable = null;
        log = new ArrayList<LogEntry>(backupLog);
        backupLog = null;
    }

    private void calculateCardCount() {
        int cardCountPerOnePlayer = (cardCount - 3) / playerCount;
        int outCount = (cardCount - 3) % playerCount;

        cardCountPerPlayer = new int[playerCount + 2];
        cardCountPerPlayer[ENV_COL] = 3;
        cardCountPerPlayer[OUT_COL] = outCount;
        for (int i = 2; i < cardCountPerPlayer.length; ++i) {
            cardCountPerPlayer[i] = cardCountPerOnePlayer;
        }
    }

    private List<Card> cardsShowedTo(String player) {
        ArrayList<Card> result = new ArrayList<Card>();
        for (LogEntry e : log) {
            if (e instanceof Suggestion) {
                Suggestion s = (Suggestion) e;
                if (s.asker.equals(player)) {
                    for (Reply r : s.replies) {
                        if (r.replier.equals(ME)) {
                            if (r.cardReply instanceof CardReply.ActualCard) {
                                result.add(((CardReply.ActualCard)r.cardReply).card);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private boolean setPlus(int cardNumber, int playerNumber)
                                                throws ContradictionException {
        if (table[cardNumber][playerNumber] == Resolution.Minus) {
            Card card = Card.values()[cardNumber];
            String player = getCompartments().get(playerNumber);
            throw new ContradictionException("Card : " + card
                                         + ", Player : " + player
                                         + ". Expected Unknown or Plus, "
                                           + "encountered Minus.");
        }
        boolean tableModified = false;
        if (table[cardNumber][playerNumber] == Resolution.Unknown) {
            tableModified = true;
        }
        table[cardNumber][playerNumber] = Resolution.Plus;
        for (int i = 0; i < table[cardNumber].length; ++i) {
            if (i != playerNumber) {
                boolean setMinusValue = setMinus(cardNumber, i);
                tableModified = tableModified || setMinusValue;
            }
        }
        return tableModified;
    }

    private boolean setMinus(int cardNumber, int playerNumber)
                                                throws ContradictionException {
        if (table[cardNumber][playerNumber] == Resolution.Plus) {
            Card card = Card.values()[cardNumber];
            String player = getCompartments().get(playerNumber);
            throw new ContradictionException("Card : " + card
                                         + ", Player : " + player
                                         + ". Expected Unknown or Minus, "
                                           + "encountered Plus.");
        }

        boolean tableModified = false;
        if (table[cardNumber][playerNumber] == Resolution.Unknown) {
            tableModified = true;
        }
        table[cardNumber][playerNumber] = Resolution.Minus;
        return tableModified;
    }

    private boolean processLog() throws UnknownPlayerException
                                      , ContradictionException {
        boolean tableModified = false;
        for (LogEntry logEntry : log) {
            if (logEntry instanceof Accusation) {
                boolean doAccusation = doAccusation((Accusation)logEntry);
                boolean solveAccusationValue = solveAccusation((Accusation)logEntry);
                tableModified = tableModified || solveAccusationValue;
                tableModified = tableModified || doAccusation;
            } else if (logEntry instanceof SetCard) {
                SetCard s = (SetCard) logEntry;
                int playerNumber = playerOrd(s.player);
                int cardNumber = s.card.cardNumber();
                setPlus(cardNumber, playerNumber);
            } else {
                Suggestion suggestion = (Suggestion) logEntry;
                boolean solveRepliersHaveValue = solveRepliersHave(suggestion);
                boolean solveReplierHasNoCardsValue = solveReplierHasNoCards(suggestion);
                boolean solveOnlyOneUnknownValue = solveOnlyOneUnknown(suggestion);

                tableModified = tableModified || solveRepliersHaveValue;
                tableModified = tableModified || solveReplierHasNoCardsValue;
                tableModified = tableModified || solveOnlyOneUnknownValue;
            }
        }
        return tableModified;
    }

    private boolean doAccusation(Accusation a) throws UnknownPlayerException
                                                    , ContradictionException {
        int playerNumber = playerOrd(a.asker);
        boolean tableModified = false;
        for (Card c : a.cards) {
            int cardNumber = c.cardNumber();
            boolean result = setMinus(cardNumber, playerNumber);
            tableModified = tableModified || result;
        }
        return tableModified;
    }

    private boolean solveAccusation(Accusation a)
                                    throws ContradictionException {
        int plusCount = 0;
        for (Card c : a.cards) {
            int cardNumber = c.cardNumber();
            if (table[cardNumber][ENV_COL] == Resolution.Plus) {
                ++plusCount;
            }
        }
        if (plusCount == 2) {
            for (Card c : a.cards) {
                int cardNumber = c.cardNumber();
                if (table[cardNumber][ENV_COL] == Resolution.Unknown) {
                    setMinus(cardNumber, ENV_COL);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean rectifyTable() throws ContradictionException {
        boolean tableModified = false;
        for (int i = 0; i < table[0].length; ++i) {
            boolean solvePlayerHasAllCardsValue = solvePlayerHasAllCards(i);
            tableModified = tableModified || solvePlayerHasAllCardsValue;
        }
        for (int i = 0; i < table[0].length; ++i) {
            boolean solveCountOfNonnegativeEqualsCardCountValue =
                                      solveCountOfNonnegativeEqualsCardCount(i);
            tableModified = tableModified || solveCountOfNonnegativeEqualsCardCountValue;
        }
        for (int i = 0; i < table.length; ++i) {
            boolean solveLineOneNonNegativeValue = solveLineOneNonNegative(i);
            tableModified = tableModified || solveLineOneNonNegativeValue;
        }
        boolean solveEnvHasOneUnknownCardInGroup06Value =
                                         solveEnvHasOneUnknownCardInGroup(0, 6);
        tableModified = tableModified || solveEnvHasOneUnknownCardInGroup06Value;

        boolean solveEnvHasOneUnknownCardInGroup612Value =
                                        solveEnvHasOneUnknownCardInGroup(6, 12);
        tableModified = tableModified || solveEnvHasOneUnknownCardInGroup612Value;

        boolean solveEnvHasOneUnknownCardInGroup12LengthValue =
                             solveEnvHasOneUnknownCardInGroup(12, table.length);
        tableModified = tableModified || solveEnvHasOneUnknownCardInGroup12LengthValue;

        boolean solvePlusInGroup06Value = solvePlusInGroup(0, 6);
        tableModified = tableModified || solvePlusInGroup06Value;

        boolean solvePlusInGroup612Value = solvePlusInGroup(6, 12);
        tableModified = tableModified || solvePlusInGroup612Value;

        boolean solvePlusInGroup12LengthValue = solvePlusInGroup(12, table.length);
        tableModified = tableModified || solvePlusInGroup12LengthValue;
        return tableModified;
    }

    private void inferenceCycle() throws UnknownPlayerException
                                       , ContradictionException {
        for (;;) {
            boolean tableModified = false;

            boolean processLogValue = processLog();
            tableModified = tableModified || processLogValue;

            boolean rectifyTableValue = rectifyTable();
            tableModified = tableModified || rectifyTableValue;
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
    private boolean solveRepliersHave(Suggestion le)
                                                throws UnknownPlayerException
                                                     , ContradictionException {
        boolean tableModified = false;
        for (Reply r : le.replies) {
            int playerNumber = playerOrd(r.replier);

            int cardNumber = r.cardReply.cardNumber();
            if (cardNumber < 0) {
                continue;
            }
            boolean setPlusValue = setPlus(cardNumber, playerNumber);
            tableModified = tableModified || setPlusValue;
        }
        return tableModified;
    }

    /* Processes the situation when replier shows no cards. */
    private boolean solveReplierHasNoCards(Suggestion le)
                                                throws UnknownPlayerException
                                                     , ContradictionException {
        boolean tableModified = false;
        for (Reply r : le.replies) {
            int playerNumber = playerOrd(r.replier);

            if (r.cardReply.cardNumber() == -2) {
                for (Card c : le.askedCards) {
                    int cardNumber = c.cardNumber();
                    boolean setMinusValue = setMinus(cardNumber, playerNumber);
                    tableModified = tableModified || setMinusValue;
                }
            }
        }
        return tableModified;
    }

    private boolean solveOnlyOneUnknown(Suggestion le)
                                                throws UnknownPlayerException
                                                     , ContradictionException {
        boolean tableModified = false;
        for (Reply r : le.replies) {
            int playerNumber = playerOrd(r.replier);

            if (r.cardReply.cardNumber() == -1) {
                List<Card> s = allPlusCards(playerNumber);
                s.addAll(allUnknownCards(playerNumber));
                s.retainAll(le.askedCards);
                if (s.size() == 1) {
                    int cardNumber = s.get(0).cardNumber();
                    boolean setPlusValue = setPlus(cardNumber, playerNumber);
                    tableModified = tableModified || setPlusValue;
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

    private boolean solveLineOneNonNegative(int cardNumber)
                                                throws ContradictionException {
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
                    boolean setPlusValue = setPlus(cardNumber, i);
                    tableModified = tableModified || setPlusValue;
                }
            }
        }
        return tableModified;
    }

    private boolean solveCountOfNonnegativeEqualsCardCount(int playerNumber)
                                                throws ContradictionException {
        boolean tableModified = false;
        int nonNegativeCount = 0;
        for (int i = 0; i < table.length; ++i) {
            if (table[i][playerNumber] != Resolution.Minus) {
                ++nonNegativeCount;
            }
        }
        if (nonNegativeCount == cardCountPerPlayer[playerNumber]) {
            for (int i = 0; i < table.length; ++i) {
                if (table[i][playerNumber] == Resolution.Unknown) {
                    setPlus(i, playerNumber);
                    tableModified = true;
                }
            }
        }
        return tableModified;
    }

    private boolean solvePlusInGroup(int l, int r)
                                        throws ContradictionException {
        boolean tableModified = false;
        int pluses = 0;
        for (int i = l; i < r; ++i) {
            if (table[i][0] == Resolution.Plus) {
                ++pluses;
            }
        }
        if (pluses == 1) {
            for (int i = l; i < r; ++i) {
                if (table[i][0] != Resolution.Plus) {
                    boolean setMinusValue = setMinus(i, 0);
                    tableModified = tableModified || setMinusValue;
                }
            }
        }
        return tableModified;
    }

    private boolean solveEnvHasOneUnknownCardInGroup(int l, int r)
                                                throws ContradictionException {
        boolean tableModified = false;
        int minuses = 0;
        for (int i = l; i < r; ++i) {
            if (table[i][0] == Resolution.Minus) {
                ++minuses;
            }
        }
        if (minuses == (r - l - 1)) {
            for (int i = l; i < r; ++i) {
                if (table[i][0] != Resolution.Minus) {
                    boolean setPlusValue = setPlus(i, 0);
                    tableModified = tableModified || setPlusValue;
                }
            }
        }
        return tableModified;
    }

    private boolean solvePlayerHasAllCards(int playerNumber)
                                                throws ContradictionException {
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
