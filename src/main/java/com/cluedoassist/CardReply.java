package com.cluedoassist;

import java.io.Serializable;

public class CardReply implements Serializable {

    static final long serialVersionUID = 1L;

    private CardReply() {
        // disable direct instantiation
    }

    private String value;

    public static CardReply NoCard() {
        CardReply r = new CardReply();
        r.value = NOCARD_S;
        return r;
    }

    public static CardReply UnknownCard() {
        CardReply r = new CardReply();
        r.value = UNKNOWN_S;
        return r;
    }

    public static CardReply ActualCard(Card c) {
        CardReply r = new CardReply();
        r.value = c.toString();
        return r;
    }

    public static CardReply valueOf(String s) {
        if (NOCARD_S.equals(s)) {
            return NoCard();
        } else if (UNKNOWN_S.equals(s)) {
            return UnknownCard();
        } else {
            return ActualCard(Card.valueOf(s));
        }
    }

    public int ordinal(CardSet cs) throws UnknownCardException {
        if (NOCARD_S.equals(value)) {
            return NOCARD_INT;
        } else if (UNKNOWN_S.equals(value)) {
            return UNKNOWN_INT;
        } else {
            return cs.ordinal(Card.valueOf(value));
        }
    }

    @Override
    public String toString() {
        return value;
    }

    public boolean isActualCard() {
        if (NOCARD_S.equals(value)) {
            return false;
        } else if (UNKNOWN_S.equals(value)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isNoCard() {
        if (NOCARD_S.equals(value)) {
            return true;
        }
        return false;
    }

    public boolean isUnknown() {
        if (UNKNOWN_S.equals(value)) {
            return true;
        }
        return false;
    }

    public Card getCard() {
        if (isActualCard()) {
            return Card.valueOf(value);
        } else {
            return null;
        }
    }

    public static final int NOCARD_INT = -2;

    public static final int UNKNOWN_INT = -1;

    public static final String NOCARD_S = "NoCard";

    public static final String UNKNOWN_S = "Unknown";

}
