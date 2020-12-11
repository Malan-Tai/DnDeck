package com.example.dndeck_a6.game;

public class Card {

    public String code;
    public String suit;
    public int value;
    public String deckID = "";
    public boolean selectedInDeck = true;
    public boolean selectedInHand = false;

    public Card(String code){
        this.code = code;
    }

    public Card(String code, String id){
        this.code = code;
        deckID = id;

        suit = code.substring(1);
        String valueString = code.substring(0, 1);
        if (valueString.equals("K")) value = 13;
        else if (valueString.equals("Q")) value = 12;
        else if (valueString.equals("J")) value = 11;
        else if (valueString.equals("A")) value = 1;
        else value = Integer.parseInt(valueString);
    }

}
