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

import org.testng.annotations.*;

import static org.testng.AssertJUnit.*;

import java.util.*;

public class AccusationTest {

    @Test
    public void testCardsImmutable() {
        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(Card.valueOf("Kitchen"));
        cards.add(Card.valueOf("Plum"));
        cards.add(Card.valueOf("Pipe"));
        Accusation a = new Accusation("Mike", cards);
        boolean exceptionOccured = false;
        try {
            a.cards.add(Card.valueOf("Study"));
            assertEquals(3, a.cards.size());
        } catch (Exception e) {
            exceptionOccured = true;
        }
        assertTrue(exceptionOccured);
    }
}
