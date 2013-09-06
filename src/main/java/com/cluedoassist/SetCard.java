package com.cluedoassist;

public class SetCard implements LogEntry {

    public final String player;

    public final Card card;

    public SetCard(String player, Card card) {
        this.player = player;
        this.card = card;
    }
}
