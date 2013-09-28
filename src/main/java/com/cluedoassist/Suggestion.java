package com.cluedoassist;

import java.util.*;

public final class Suggestion implements LogEntry {

    public final String asker;

    public final List<Card> askedCards;

    public final List<Reply> replies;

    /**
     * Constructs Suggestion.
     *
     * @throws IllegalArgumentException
     */
    public Suggestion( String asker
                     , List<Card> askedCards
                     , List<Reply> replies) {

        if (asker == null || "".equals(asker)) {
            throw new IllegalArgumentException("asker can't be null or empty");
        }
        if (askedCards == null) {
            throw new IllegalArgumentException("askedCards can't be null");
        }
        if (new HashSet<Card>(askedCards).size() != 3) {
            throw new IllegalArgumentException(
                        "There should be exactly three different asked cards");
        }
        if (new HashSet<String>(Reply.repliers(replies)).size()
                != replies.size()) {
            throw new IllegalArgumentException("Repliers should be different");
        }
        if (Reply.cardCountInReplies(replies) > 3) {
            throw new IllegalArgumentException(
                               "There can not be more than 3 cards in replies");
        }

        this.asker = asker;

        this.askedCards = new ArrayList<Card>(askedCards);

        this.replies = new ArrayList<Reply>(replies);
    }

    @Override
    public String toString() {
        return "Suggestion : " + asker + " " + askedCards + " " + replies;
    }
}
