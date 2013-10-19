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

public final class Accusation implements LogEntry {

    static final long serialVersionUID = 1L;

    public final String asker;

    public final List<Card> cards;

    public Accusation(String asker, List<Card> cards) {
        if (asker == null || "".equals(asker)) {
            throw new IllegalArgumentException("asker can't be null or empty");
        }
        if (cards == null) {
            throw new IllegalArgumentException("cards can't be null");
        }
        if (cards.size() != 3) {
            throw new IllegalArgumentException(
                                        "there should be exactly three cards");
        }
        this.asker = asker;

        this.cards = Collections.unmodifiableList(new ArrayList<Card>(cards));
    }

    @Override
    public String toString() {
        return "Accusation: " + asker + " " + cards;
    }
}
