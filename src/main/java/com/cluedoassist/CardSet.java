/*
 * Copyright 2013 Victor Denisov
 *
 * This file is part of Cluedo Assistant.
 *
 * Cluedo Assistant is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cluedo Assistant is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Cluedo Assistant.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cluedoassist;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.io.Serializable;

public class CardSet implements Serializable {

    static final long serialVersionUID = 1L;

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
