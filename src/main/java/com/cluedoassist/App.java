package com.cluedoassist;

import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        ArrayList<String> players = new ArrayList<String>();
        players.add("p1");
        Cluedo c = new Cluedo(players);
        ArrayList<Card> askedCards = new ArrayList<Card>();
        askedCards.add(Card.Scarlett);
        ArrayList<Reply> replies = new ArrayList<Reply>();
        replies.add(new Reply("p1", CardReply.NoCard()));
        try {
        c.makeTurn("me", askedCards, replies);
        } catch (Exception e) {
            System.out.println("Exception");
        }
        String[][] table = c.getTable();
        for (int i = 0; i < table.length; ++i) {
            for (int j = 0; j < table[i].length; ++j) {
                System.out.print(table[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println(c.getTable()[Card.Scarlett.cardNumber() + 1][4]);
    }
}
