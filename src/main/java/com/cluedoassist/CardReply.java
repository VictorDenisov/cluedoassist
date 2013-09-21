package com.cluedoassist;

import java.io.Serializable;

public abstract class CardReply implements Serializable {

    private CardReply() {
        // disable direct instantiation
    }

    public static CardReply NoCard() {
        return noCard;
    }

    public static CardReply UnknownCard() {
        return unknownCard;
    }

    public static CardReply ActualCard(Card c) {
        return new ActualCard(c);
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

    public abstract int ordinal();

    public static final int NOCARD_INT = -2;

    public static final int UNKNOWN_INT = -1;

    public static final String NOCARD_S = "NoCard";

    public static final String UNKNOWN_S = "Unknown";

    private static final NoCard noCard = new NoCard();

    private static final UnknownCard unknownCard = new UnknownCard();

    static final class NoCard extends CardReply {
        public int ordinal() {
            return NOCARD_INT;
        }

        @Override
        public String toString() {
            return NOCARD_S;
        }
    }

    static final class UnknownCard extends CardReply {
        public int ordinal() {
            return UNKNOWN_INT;
        }

        @Override
        public String toString() {
            return UNKNOWN_S;
        }
    }

    static final class ActualCard extends CardReply {
        final Card card;

        ActualCard(Card c) {
            this.card = c;
        }

        public int ordinal() {
            return card.ordinal();
        }

        @Override
        public String toString() {
            return card.toString();
        }
    }

}
