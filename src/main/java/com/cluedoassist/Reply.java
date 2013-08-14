package com.cluedoassist;

public final class Reply {

    final Player replier;

    final CardReply cardReply;

    public Reply(Player replier, CardReply cardReply) {
        this.replier = replier;
        this.cardReply = cardReply;
    }
}
