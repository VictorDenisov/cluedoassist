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

import java.util.Map;
import java.util.List;

import java.io.Serializable;

public interface Cluedo extends Serializable {

    static String ENVELOPE = "Env";
    static String OUT = "Out";
    static String ME = "Me";

    List<LogEntry> getLog();

    List<String> getCompartments();

    List<String> getPlayers();

    CardSet getCardSet();

    String[][] getTable();

    Map<String, List<Card>> cardsShowedByMe();

    List<Card> cardsSuggestedBy(String player);

    void setCard(String asker, Card card) throws UnknownPlayerException
                                               , UnknownCardException
                                               , ContradictionException;

    void makeTurn( String asker
                 , List<Card> askedCards
                 , List<Reply> replies) throws UnknownPlayerException
                                             , UnknownCardException
                                             , ContradictionException;

    void makeTurn(Suggestion l) throws UnknownPlayerException
                                     , UnknownCardException
                                     , ContradictionException;

    void makeAccusation(Accusation a) throws UnknownPlayerException
                                           , UnknownCardException
                                           , ContradictionException;

    List<CardReply> possibleCardReplies( String replier
                                       , Card[] askedCards
                                       ) throws UnknownPlayerException
                                              , UnknownCardException;

    int playerOrd(String player) throws UnknownPlayerException;

    public void replaceLog(List<LogEntry> l) throws UnknownPlayerException
                                                  , UnknownCardException
                                                  , ContradictionException;

}
