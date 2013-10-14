package com.cluedoassist;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public final class Reply implements Serializable {

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
