package com.cluedoassist;

import java.io.Serializable;

public final class Card implements Serializable {

    static final long serialVersionUID = 1L;

    private Card() {
    }

    private String cardName;

    public String toString() {
        return cardName;
    }

    public static Card valueOf(String s) {
        Card c = new Card();
        c.cardName = s;
        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Card)) {
            return false;
        }
        Card c = (Card)o;
        return cardName.equals(c.cardName);
    }

    @Override
    public int hashCode() {
        return cardName.hashCode();
    }
}
