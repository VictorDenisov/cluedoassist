package com.cluedoassist;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class CardSet {

    public static class CardSetBuilder {
        private List<String> suspects;

        private List<String> weapons;

        private List<String> rooms;

        public CardSetBuilder weapons(List<String> weapons) {
            this.weapons = weapons;
            return this;
        }

        public CardSetBuilder rooms(List<String> rooms) {
            this.rooms = rooms;
            return this;
        }

        public CardSet create() {
            ArrayList<Card> cards = new ArrayList<Card>();
            for (String s : suspects) {
                cards.add(Card.valueOf(s));
            }
            for (String s : weapons) {
                cards.add(Card.valueOf(s));
            }
            for (String s : rooms) {
                cards.add(Card.valueOf(s));
            }

            return new CardSet( cards
                              , suspects.size()
                              , weapons.size()
                              , rooms.size()
                              );
        }
    }

    public static CardSetBuilder suspects(List<String> suspects) {
        CardSetBuilder b = new CardSetBuilder();
        b.suspects = suspects;
        return b;
    }

    public final List<Card> cards;

    public final int suspectCount;

    public final int weaponCount;

    public final int roomCount;

    public final int cardCount;

    private CardSet( ArrayList<Card> cards
                   , int suspectCount
                   , int weaponCount
                   , int roomCount) {
        this.cards = Collections.unmodifiableList(cards);
        this.suspectCount = suspectCount;
        this.weaponCount = weaponCount;
        this.roomCount = roomCount;
        this.cardCount = cards.size();
    }

    public int ordinal(Card card) throws UnknownCardException {
        for (int i = 0; i < cards.size(); ++i) {
            if (cards.get(i).equals(card)) {
                return i;
            }
        }
        throw new UnknownCardException("Card " + card + " is unknown");
    }
}
