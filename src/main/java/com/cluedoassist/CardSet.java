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
            ArrayList<String> cards = new ArrayList<String>();
            cards.addAll(suspects);
            cards.addAll(weapons);
            cards.addAll(rooms);

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

    public final List<String> cards;

    public final int suspectCount;

    public final int weaponCount;

    public final int roomCount;

    private CardSet( ArrayList<String> cards
                   , int suspectCount
                   , int weaponCount
                   , int roomCount) {
        this.cards = Collections.unmodifiableList(cards);
        this.suspectCount = suspectCount;
        this.weaponCount = weaponCount;
        this.roomCount = roomCount;
    }

    public int ordinal(String card) throws UnknownCardException {
        for (int i = 0; i < cards.size(); ++i) {
            if (cards.get(i).equals(card)) {
                return i;
            }
        }
        throw new UnknownCardException("Card " + card + " is unknown");
    }
}
