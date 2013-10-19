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
