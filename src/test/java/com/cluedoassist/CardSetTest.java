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

import java.util.List;
import java.util.Arrays;

public class CardSetTest {
    List<String> suspects = Arrays.asList(new String[]{ "suspect1"
                                                      , "suspect2"
                                                      , "suspect3"
                                                      });
    List<String> weapons = Arrays.asList(new String[]{ "weapon1"
                                                     , "weapon2"
                                                     });
    List<String> rooms = Arrays.asList(new String[]{ "room1"
                                                   , "room2"
                                                   , "room3"
                                                   , "room4"
                                                   });

    @Test
    public void testBuilder() {
        CardSet cs = CardSet.suspects(suspects)
                            .weapons(weapons)
                            .rooms(rooms)
                            .create();
        assertEquals(9, cs.cards.size());
        assertEquals(3, cs.suspectCount);
        assertEquals(2, cs.weaponCount);
        assertEquals(4, cs.roomCount);
    }

    @Test
    public void testOrdinal() throws Exception {
        CardSet cs = CardSet.suspects(suspects)
                            .weapons(weapons)
                            .rooms(rooms)
                            .create();

        assertEquals(3, cs.ordinal(Card.valueOf("weapon1")));
    }
}
