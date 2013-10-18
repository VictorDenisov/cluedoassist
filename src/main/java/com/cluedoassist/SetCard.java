package com.cluedoassist;

public final class SetCard implements LogEntry {

    static final long serialVersionUID = 1L;

    public final String player;

    public final Card card;

    public SetCard(String player, Card card) {
        this.player = player;
        this.card = card;
    }

    @Override
    public String toString() {
        return "SetCard : " + player + " " + card;
    }
}
