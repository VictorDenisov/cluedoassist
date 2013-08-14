package com.cluedoassist;

public abstract class CardReply {

    public static CardReply NoCard() {
        return noCard;
    }

    public static CardReply UnknownCard() {
        return unknownCard;
    }

    public static CardReply ActualCard(Card c) {
        return new ActualCard(c);
    }

    public abstract int cardNumber();

    private CardReply() {
        // disable direct instantiation
    }

    private static final NoCard noCard = new NoCard();

    private static final UnknownCard unknownCard = new UnknownCard();

    static class NoCard extends CardReply {
        public int cardNumber() {
            return -1;
        }
    }

    static class UnknownCard extends CardReply {
        public int cardNumber() {
            return -1;
        }
    }

    static class ActualCard extends CardReply {
        private Card card;

        ActualCard(Card c) {
            this.card = c;
        }

        public int cardNumber() {
            Card[] cs = Card.values();
            for (int i = 0; i < cs.length; ++i) {
                if (cs[i] == card) {
                    return i;
                }
            }
            throw new RuntimeException("Unknown card : " + card);
        }
    }
}
