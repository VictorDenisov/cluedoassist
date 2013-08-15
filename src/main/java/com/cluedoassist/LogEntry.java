package com.cluedoassist;

import java.util.List;
import java.util.ArrayList;

public final class LogEntry {

    final Player asker;

    final List<Card> askedCards;

    final List<Reply> replies;

    public LogEntry(Player asker, List<Card> askedCards, List<Reply> replies) {
        this.asker = asker;

        this.askedCards = new ArrayList<Card>(askedCards);

        this.replies = new ArrayList<Reply>(replies);
    }
}
