package com.cluedoassist;

import java.util.*;

public class Suggestion implements LogEntry {

    final String asker;

    final List<Card> askedCards;

    final List<Reply> replies;

    public Suggestion(String asker, List<Card> askedCards, List<Reply> replies) {
        this.asker = asker;

        this.askedCards = new ArrayList<Card>(askedCards);

        this.replies = new ArrayList<Reply>(replies);
    }

    @Override
    public String toString() {
        return asker + " " + askedCards + " " + replies;
    }
}
