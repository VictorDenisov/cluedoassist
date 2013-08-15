package com.cluedoassist;

import java.util.List;

public final class Player {

    final String name;

    private Player(String name) {
        this.name = name;
    }

    public static Player fromName(String name) {
        return new Player(name);
    }

    public int ord(List<Player> ps) throws UnknownPlayerException {
        int n = ps.size();
        for (int i = 0; i < n; ++i) {
            if (ps.get(i) == this) {
                return i;
            }
        }
        throw new UnknownPlayerException("Player " + name + " is missing from the list");
    }
}
