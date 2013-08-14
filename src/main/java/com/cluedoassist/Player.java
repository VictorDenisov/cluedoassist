package com.cluedoassist;

public final class Player {

    final String name;

    private Player(String name) {
        this.name = name;
    }

    public static Player fromName(String name) {
        return new Player(name);
    }

    public int ord(Player[] ps) {
        for (int i = 0; i < ps.length; ++i) {
            if (ps[i] == this) {
                return i;
            }
        }
        return -1;
    }
}
