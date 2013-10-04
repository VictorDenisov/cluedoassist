package com.cluedoassist;

import java.util.*;

import java.io.Serializable;

import java.util.Collections;

public class CluedoSmart extends CluedoDumb {

    private Resolution[][] backupTable;

    private ArrayList<LogEntry> backupLog;

    public CluedoSmart(List<String> players) {
        super(players);

        try {
            solvePlayerHasAllCards(OUT_COL);
        } catch (ContradictionException e) {
            // no contradiction is possible in this case
        }
    }

    public void setCard(String asker, Card card) throws UnknownPlayerException
                                                      , ContradictionException {
        beginTransaction();
        try {
            super.setCard(asker, card);
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

    public void makeTurn(Suggestion l) throws UnknownPlayerException
                                          , ContradictionException {
        beginTransaction();
        try {
            super.makeTurn(l);
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
            super.makeAccusation(a);
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

    public List<CardReply> possibleCardReplies( String replier
                                              , Card[] askedCards
                                              ) throws UnknownPlayerException {
        int playerNumber = playerOrd(replier);
        ArrayList<CardReply> result = new ArrayList<CardReply>();
        if (hasNoneOf(playerNumber, askedCards)) {
            result.add(CardReply.NoCard());
        }
        if (hasNonNegativeOf(playerNumber, askedCards)) {
            result.add(CardReply.UnknownCard());
        }
        for (Card c : askedCards) {
            if (table[c.ordinal()][playerNumber] != Resolution.Minus) {
                result.add(CardReply.ActualCard(c));
            }
        }
        return Collections.unmodifiableList(result);
    }

    private boolean hasNoneOf(int playerNumber, Card[] cards) {
        for (Card c : cards) {
            if (table[c.ordinal()][playerNumber] == Resolution.Plus) {
                return false;
            }
        }
        return true;
    }

    private boolean hasNonNegativeOf(int playerNumber, Card[] cards) {
        for (Card c : cards) {
            if (table[c.ordinal()][playerNumber] != Resolution.Minus) {
                return true;
            }
        }
        return false;
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
                boolean doAccusation = solveAskerHasNoCardsFromAccusation((Accusation)logEntry);
                boolean solveAccusationValue = solveUnsuccessfulAccusation((Accusation)logEntry);
                tableModified = tableModified || solveAccusationValue;
                tableModified = tableModified || doAccusation;
            } else if (logEntry instanceof SetCard) {
                SetCard s = (SetCard) logEntry;
                int playerNumber = playerOrd(s.player);
                int cardNumber = s.card.ordinal();
                setPlus(cardNumber, playerNumber);
            } else {
                Suggestion suggestion = (Suggestion) logEntry;
                boolean solveRepliersHaveValue = solveRepliersHave(suggestion);
                boolean solveReplierHasNoCardsValue = solveReplierHasNoCards(suggestion);
                boolean solveOnlyOneUnknownValue = solveOnlyOneUnknown(suggestion);
                boolean solveThreeCardsReplied = solveThreeCardsReplied(suggestion);

                tableModified = tableModified || solveRepliersHaveValue;
                tableModified = tableModified || solveReplierHasNoCardsValue;
                tableModified = tableModified || solveOnlyOneUnknownValue;
                tableModified = tableModified || solveThreeCardsReplied;
            }
        }
        return tableModified;
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

    private boolean solveAskerHasNoCardsFromAccusation(Accusation a)
                                               throws UnknownPlayerException
                                                    , ContradictionException {
        int playerNumber = playerOrd(a.asker);
        boolean tableModified = false;
        for (Card c : a.cards) {
            int cardNumber = c.ordinal();
            boolean result = setMinus(cardNumber, playerNumber);
            tableModified = tableModified || result;
        }
        return tableModified;
    }

    private boolean solveUnsuccessfulAccusation(Accusation a)
                                            throws ContradictionException {
        if (plusCountOfPlayer(ENV_COL) == 2) {
            for (Card c : a.cards) {
                int cardNumber = c.ordinal();
                if (table[cardNumber][ENV_COL] == Resolution.Unknown) {
                    setMinus(cardNumber, ENV_COL);
                    return true;
                }
            }
        }
        return false;
    }

    private void inferenceCycle() throws UnknownPlayerException
                                       , ContradictionException {
        for (;;) {
            boolean tableModified = false;

            boolean processLogValue = processLog();
            tableModified = tableModified || processLogValue;

            boolean rectifyTableValue = rectifyTable();
            verifyEveryLineHasQuestionOrPlus();
            tableModified = tableModified || rectifyTableValue;
            if (!tableModified) {
                break;
            }
        }
    }

    private void verifyEveryLineHasQuestionOrPlus()
                                        throws ContradictionException {
        for (int i = 0; i < table.length; ++i) {
            verifyLineHasQuestionOrPlus(i);
        }
    }

    private void verifyLineHasQuestionOrPlus(int lineNum)
                                        throws ContradictionException {
        for (int i = 0; i < table[lineNum].length; ++i) {
            if (table[lineNum][i] != Resolution.Minus) {
                return;
            }
        }
        throw new ContradictionException("Line has minuses only");
    }

    public void replaceLog(List<LogEntry> l) throws UnknownPlayerException
                                                  , ContradictionException {
        beginTransaction();
        try {
            super.replaceLog(l);
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

    /* Processes the situation when replier shows known card. */
    private boolean solveRepliersHave(Suggestion le)
                                                throws UnknownPlayerException
                                                     , ContradictionException {
        boolean tableModified = false;
        for (Reply r : le.replies) {
            int playerNumber = playerOrd(r.replier);

            int cardNumber = r.cardReply.ordinal();
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

            if (r.cardReply.ordinal() == CardReply.NOCARD_INT) {
                for (Card c : le.askedCards) {
                    int cardNumber = c.ordinal();
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

            if (r.cardReply.ordinal() == CardReply.UNKNOWN_INT) {
                List<Card> s = allPlusCards(playerNumber);
                s.addAll(allUnknownCards(playerNumber));
                s.retainAll(le.askedCards);
                if (s.size() == 1) {
                    int cardNumber = s.get(0).ordinal();
                    boolean setPlusValue = setPlus(cardNumber, playerNumber);
                    tableModified = tableModified || setPlusValue;
                }
            }
        }
        return tableModified;
    }

    private boolean solveThreeCardsReplied(Suggestion le)
                                                throws UnknownPlayerException
                                                     , ContradictionException {
        boolean tableModified = false;
        int countCardReplies = 0;
        for (Reply r : le.replies) {
            if (r.cardReply.ordinal() != CardReply.NOCARD_INT) {
                ++countCardReplies;
            }
        }
        if (countCardReplies == 3) {
            for (Card c : le.askedCards) {
                int cardNumber = c.ordinal();
                boolean value = setMinus(cardNumber, ENV_COL);
                tableModified = tableModified || value;
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
                    boolean setMinusValue = setMinus(i, ENV_COL);
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
                    boolean setPlusValue = setPlus(i, ENV_COL);
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
