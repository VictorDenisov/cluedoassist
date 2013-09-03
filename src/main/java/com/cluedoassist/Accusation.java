package com.cluedoassist;

import java.util.List;
import java.util.ArrayList;

import java.io.Serializable;

public final class Accusation extends LogEntry {

    final List<Card> cards;

    public Accusation(String asker, List<Card> cards) {
        super(asker);

        this.cards = new ArrayList<Card>(cards);
    }

    @Override
    public String toString() {
        return asker + " " + cards;
    }
}
