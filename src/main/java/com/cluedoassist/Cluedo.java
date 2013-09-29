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

    String[][] getTable();

    Map<String, List<Card>> cardsShowedByMe();

    void setCard(String asker, Card card) throws UnknownPlayerException
                                                      , ContradictionException;

    void makeTurn( String asker
                 , List<Card> askedCards
                 , List<Reply> replies) throws UnknownPlayerException
                                             , ContradictionException;

    void makeTurn(Suggestion l) throws UnknownPlayerException
                                     , ContradictionException;

    void makeAccusation(Accusation a) throws UnknownPlayerException
                                           , ContradictionException;

    List<CardReply> possibleCardReplies( String replier
                                       , Card[] askedCards
                                       ) throws UnknownPlayerException;

    int playerOrd(String player) throws UnknownPlayerException;

    public void replaceLog(List<LogEntry> l) throws UnknownPlayerException
                                                  , ContradictionException;

}
