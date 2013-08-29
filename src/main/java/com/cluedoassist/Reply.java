package com.cluedoassist;

import java.io.Serializable;

public final class Reply implements Serializable {

    final String replier;

    final CardReply cardReply;

    public Reply(String replier, CardReply cardReply) {
        this.replier = replier;
        this.cardReply = cardReply;
    }

    @Override
    public String toString() {
        return "Reply : " + replier + " " + cardReply;
    }
}
