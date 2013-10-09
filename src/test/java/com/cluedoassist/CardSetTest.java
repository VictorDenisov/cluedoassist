package com.cluedoassist;

import org.testng.annotations.*;

import static org.testng.AssertJUnit.*;

import java.util.List;
import java.util.Arrays;

public class CardSetTest {

    @Test
    public void testBuilder() {
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

        CardSet cs = CardSet.suspects(suspects)
                            .weapons(weapons)
                            .rooms(rooms)
                            .create();
        assertEquals(9, cs.cards.size());
        assertEquals(3, cs.suspectCount);
        assertEquals(2, cs.weaponCount);
        assertEquals(4, cs.roomCount);
    }
}
