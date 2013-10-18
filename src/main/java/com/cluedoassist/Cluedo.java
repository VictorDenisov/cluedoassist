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
