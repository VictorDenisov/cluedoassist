/*
 * Copyright 2013 Victor Denisov
 *
 * This file is part of Cluedo Assistant.
 *
 * Cluedo Assistant is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cluedo Assistant is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Cluedo Assistant.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cluedoassist;

import java.util.*;

import java.io.Serializable;

import java.util.Collections;

public class CluedoDumb implements Cluedo {

    static final long serialVersionUID = 1L;

    protected int playerCount;

    protected static int ENV_COL = 0;
    protected static int OUT_COL = 1;
    protected static int ME_COL = 2;

    protected ArrayList<String> players;

    protected CardSet cardSet;

    protected ArrayList<LogEntry> log;

    protected Resolution[][] table;

    protected int[] cardCountPerPlayer;

    public CluedoDumb(CardSet cs, List<String> players) {
        this.cardSet = cs;
        this.playerCount = players.size() + 1;
        this.players = new ArrayList<String>();
        this.players.add(ME);
        this.players.addAll(players);
        log = new ArrayList<LogEntry>();

        calculateCardCount();

        newTable();
    }

    private void calculateCardCount() {
        int cardCountPerOnePlayer = (cardSet.cardCount - 3) / playerCount;
        int outCount = (cardSet.cardCount - 3) % playerCount;

        cardCountPerPlayer = new int[playerCount + 2];
        cardCountPerPlayer[ENV_COL] = 3;
        cardCountPerPlayer[OUT_COL] = outCount;
        for (int i = 2; i < cardCountPerPlayer.length; ++i) {
            cardCountPerPlayer[i] = cardCountPerOnePlayer;
        }
    }

    private void newTable() {
        table = new Resolution[cardSet.cardCount][];
        for (int i = 0; i < cardSet.cardCount; ++i) {
            table[i] = new Resolution[playerCount + 2];
            for (int j = 0; j < table[i].length; ++j) {
                table[i][j] = Resolution.Unknown;
            }
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
        return Collections.unmodifiableList(result);
    }

    public List<String> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public CardSet getCardSet() {
        return cardSet;
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
            result[i + 1][0] = cardSet.cards.get(i).toString();
            for (int j = 0; j < table[i].length; ++j) {
                switch (table[i][j]) {
                case Plus : result[i + 1][j + 1] = "+"; break;
                case Minus : result[i + 1][j + 1] = "-"; break;
                case Unknown : result[i + 1][j + 1] = "?"; break;
                }
            }
        }
        try {
            for (String player : players) {
                List<Card> suggestedCards = cardsSuggestedBy(player);
                int playerNumber = playerOrd(player);
                for (Card c : suggestedCards) {
                    try {
                        int cardNumber = cardSet.ordinal(c);
                        result[cardNumber + 1][playerNumber + 1] += ",";
                    } catch (UnknownCardException e) {
                        // Ignore exception.
                        // if suggested card is unknown it just will not be
                        // refelected in table.
                    }
                }
            }
        } catch (UnknownPlayerException e) {
            // Unknown player is impossible in this case.
            // All players are from players list.
        }

        return result;
    }

    public List<Card> cardsSuggestedBy(String player) {
        ArrayList<Card> result = new ArrayList<Card>();
        for (LogEntry e : log) {
            if (e instanceof Suggestion) {
                Suggestion s = (Suggestion) e;
                if (s.asker.equals(player)) {
                    result.addAll(s.askedCards);
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
                                                      , UnknownCardException
                                                      , ContradictionException {
        log.add(new SetCard(asker, card));
    }

    public void makeTurn( String asker
                        , List<Card> askedCards
                        , List<Reply> replies) throws UnknownPlayerException
                                                    , UnknownCardException
                                                    , ContradictionException {
        makeTurn(new Suggestion(asker, askedCards, replies));
    }

    public void makeTurn(Suggestion l) throws UnknownPlayerException
                                            , UnknownCardException
                                            , ContradictionException {
        log.add(l);
    }

    public void makeAccusation(Accusation a) throws UnknownPlayerException
                                                  , UnknownCardException
                                                  , ContradictionException {
        log.add(a);
    }

    public List<CardReply> possibleCardReplies( String replier
                                              , Card[] askedCards
                                              ) throws UnknownPlayerException
                                                     , UnknownCardException {
        ArrayList<CardReply> result = new ArrayList<CardReply>();
        result.add(CardReply.NoCard());
        result.add(CardReply.UnknownCard());
        for (Card c : askedCards) {
            result.add(CardReply.ActualCard(c));
        }
        return Collections.unmodifiableList(result);
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

    public void replaceLog(List<LogEntry> l) throws UnknownPlayerException
                                                  , UnknownCardException
                                                  , ContradictionException {
        newTable();
        log.clear();
        log.addAll(l);
    }

    private List<Card> cardsShowedTo(String player) {
        ArrayList<Card> result = new ArrayList<Card>();
        for (LogEntry e : log) {
            if (e instanceof Suggestion) {
                Suggestion s = (Suggestion) e;
                if (s.asker.equals(player)) {
                    for (Reply r : s.replies) {
                        if (r.replier.equals(ME)) {
                            if (r.cardReply.isActualCard()) {
                                result.add(r.cardReply.getCard());
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

}
