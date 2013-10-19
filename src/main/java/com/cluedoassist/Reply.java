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
import java.util.List;
import java.util.ArrayList;

public final class Reply implements Serializable {

    static final long serialVersionUID = 1L;

    public final String replier;

    public final CardReply cardReply;

    /**
     * Constructs Reply.
     *
     * @throws IllegalArgumentException
     */
    public Reply(String replier, CardReply cardReply) {
        if (replier == null || "".equals(replier)) {
            throw new IllegalArgumentException(
                                    "replier can't be null or empty");
        }
        this.replier = replier;
        this.cardReply = cardReply;
    }

    @Override
    public String toString() {
        return "Reply : " + replier + " " + cardReply;
    }

    public static List<String> repliers(List<Reply> replies) {
        ArrayList<String> result = new ArrayList<String>();
        for (Reply r : replies) {
            result.add(r.replier);
        }
        return result;
    }

    public static int cardCountInReplies(List<Reply> replies) {
        int result = 0;
        for (Reply r : replies) {
            if (!r.cardReply.isNoCard()) {
                ++result;
            }
        }
        return result;
    }
}
