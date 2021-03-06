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

public final class SetCard implements LogEntry {

    static final long serialVersionUID = 1L;

    public final String player;

    public final Card card;

    public SetCard(String player, Card card) {
        this.player = player;
        this.card = card;
    }

    @Override
    public String toString() {
        return "SetCard : " + player + " " + card;
    }
}
