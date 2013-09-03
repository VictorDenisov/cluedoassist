package com.cluedoassist;

import java.util.List;
import java.util.ArrayList;

import java.io.Serializable;

public class LogEntry implements Serializable {

    final String asker;

    final List<Card> askedCards;

    final List<Reply> replies;

    public LogEntry(String asker) {
        this.asker = asker;
        askedCards = null;
        replies = null;
    }

    public LogEntry(String asker, List<Card> askedCards, List<Reply> replies) {
        this.asker = asker;

        this.askedCards = new ArrayList<Card>(askedCards);

        this.replies = new ArrayList<Reply>(replies);
    }

    @Override
    public String toString() {
        return asker + " " + askedCards + " " + replies;
    }
}
