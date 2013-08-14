package com.cluedoassist;

public final class LogEntry {

    final Player asker;

    final Card[] askedCards;

    final Reply[] replies;

    public LogEntry(Player asker, Card[] askedCards, Reply[] replies) {
        this.asker = asker;

        this.askedCards = new Card[askedCards.length];
        System.arraycopy(askedCards, 0, this.askedCards, 0, askedCards.length);

        this.replies = new Reply[replies.length];
        System.arraycopy(replies, 0, this.replies, 0, replies.length);
    }
}
