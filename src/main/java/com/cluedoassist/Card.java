package com.cluedoassist;

public enum Card {
    Scarlett,
    Mustard,
    White,
    Green,
    Peacock,
    Plum,
    Candle,
    Knife,
    Pipe,
    Revolver,
    Rope,
    Wrench,
    Kitchen,
    Billiard,
    //Library,
    Dining,
    Bathroom,
    Study,
    Garage,
    Bedroom,
    Yard,
    Guestroom;
    public int cardNumber() {
        Card[] values = Card.values();
        for (int i = 0; i < values.length; ++i) {
            if (values[i] == this) {
                return i;
            }
        }
        throw new RuntimeException("No card found");
    }
}
