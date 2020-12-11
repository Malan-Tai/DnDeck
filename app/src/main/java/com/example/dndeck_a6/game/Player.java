package com.example.dndeck_a6.game;

import java.util.ArrayList;

public class Player extends GameCharacter {
    private PlayerClass playerClass;
    private PlayerRace playerRace;

    private ArrayList<Card> deck;

    public Player(String n, PlayerClass cl, PlayerRace race, Spell[] sp, int[] attr, int hits){
        super(n, sp, attr, hits);
        playerClass = cl;
        playerRace = race;

        deck = new ArrayList<>();
        for (int i = 0; i < 4; i++){
            String suit = "";
            switch (i){
                case 0:
                    suit = "C";
                    break;
                case 1:
                    suit = "S";
                    break;
                case 2:
                    suit = "H";
                    break;
                case 3:
                    suit = "D";
                    break;
                default:
                    break;
            }

            for (int j = 0; j < 3; j++){
                if (j == 0) deck.add(new Card("A" + suit));
                else deck.add(new Card((j + 1) + suit));
            }
        }
    }

    public void setSpells(Spell[] sp){
        spells = sp;
    }

    public void setWeapons(Weapon[] weapons) {
        Spell[] newSpells = new Spell[4];
        int n = 0;
        if (spells != null) {
            for (int i = 0; i < spells.length; i++) {
                newSpells[n] = spells[i];
                n++;
            }
        }
        for (int i = 0; i < weapons.length; i++){
            newSpells[n] = weapons[i];
            n++;
        }

        spells = newSpells;
    }

    public Card getCard(String code){
        for (int i = 0; i < deck.size(); i++){
            Card c = deck.get(i);
            if (c.code.equals(code)){
                return c;
            }
        }
        return null;
    }

    public ArrayList<Card> getSuit(String suit){
        ArrayList<Card> res = new ArrayList<>();
        for (int i = 0; i < deck.size(); i++){
            Card c = deck.get(i);
            if (c.code.contains(suit)) res.add(c);
        }

        return res;
    }

    public ArrayList<Card> getDeck() { return deck; }

    @Override
    public String getDeckCodes(){
        String res = "";

        for (int i = 0; i < deck.size(); i++){
            Card card = deck.get(i);
            if (card.selectedInDeck){
                res += card.code + ",";
            }
        }

        return res;
    }

    public String getRaceClass() { return playerRace.name + " " + playerClass.name; }

    public int getSpellCastingAbility(){
        return getAttribute(playerClass.spellCastingAbility);
    }
}
