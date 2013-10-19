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

import java.util.*;

public final class Suggestion implements LogEntry {

    static final long serialVersionUID = 1L;

    public final String asker;

    public final List<Card> askedCards;

    public final List<Reply> replies;

    /**
     * Constructs Suggestion.
     *
     * @throws IllegalArgumentException
     */
    public Suggestion( String asker
                     , List<Card> askedCards
                     , List<Reply> replies) {

        if (asker == null || "".equals(asker)) {
            throw new IllegalArgumentException("asker can't be null or empty");
        }
        if (askedCards == null) {
            throw new IllegalArgumentException("askedCards can't be null");
        }
        if (new HashSet<Card>(askedCards).size() != 3) {
            throw new IllegalArgumentException(
                        "There should be exactly three different asked cards");
        }
        if (new HashSet<String>(Reply.repliers(replies)).size()
                != replies.size()) {
            throw new IllegalArgumentException("Repliers should be different");
        }
        if (Reply.cardCountInReplies(replies) > 3) {
            throw new IllegalArgumentException(
                               "There can not be more than 3 cards in replies");
        }

        this.asker = asker;

        this.askedCards = Collections.unmodifiableList(
                                            new ArrayList<Card>(askedCards));

        this.replies = Collections.unmodifiableList(
                                            new ArrayList<Reply>(replies));
    }

    @Override
    public String toString() {
        return "Suggestion : " + asker + " " + askedCards + " " + replies;
    }
}
