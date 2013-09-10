package com.cluedoassist;

import java.io.Serializable;

public abstract class CardReply implements Serializable {

    public static CardReply NoCard() {
        return noCard;
    }

    public static CardReply UnknownCard() {
        return unknownCard;
    }

    public static CardReply ActualCard(Card c) {
        return new ActualCard(c);
    }

    public abstract int ordinal();

    private CardReply() {
        // disable direct instantiation
    }

    public static final int NO_CARD = -2;

    public static final int UNKNOWN = -1;

    private static final NoCard noCard = new NoCard();

    private static final UnknownCard unknownCard = new UnknownCard();

    static class NoCard extends CardReply {
        public int ordinal() {
            return NO_CARD;
        }

        @Override
        public String toString() {
            return "NoCard";
        }
    }

    static class UnknownCard extends CardReply {
        public int ordinal() {
            return UNKNOWN;
        }

        @Override
        public String toString() {
            return "UnknownCard";
        }
    }

    static class ActualCard extends CardReply {
        Card card;

        ActualCard(Card c) {
            this.card = c;
        }

        public int ordinal() {
            return card.ordinal();
        }

        @Override
        public String toString() {
            return card + "";
        }
    }

}
