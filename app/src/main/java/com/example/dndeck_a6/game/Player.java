package com.example.dndeck_a6.game;

import com.example.dndeck_a6.Utils;
import com.example.dndeck_a6.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Player extends GameCharacter {
    private PlayerClass playerClass;
    private PlayerRace playerRace;

    private ArrayList<Card> deck;
    private ArrayList<Card> nextCardsToAdd;

    private int level = 1;
    private int[] levelThresholds = new int[] {
            0, 300, 900, 2700, 6500, 14000, 23000, 34000, 48000, 64000, 85000, 100000, 120000, 140000, 165000, 195000, 225000, 265000, 305000, 355000
    };

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
                if (j == 0) deck.add(new Card("A" + suit, MainActivity.playerDeckID));
                else deck.add(new Card((j + 1) + suit, MainActivity.playerDeckID));
            }
        }

        generateNextCards();
    }

    public Player(JSONObject json){
        super(json);

        try {
            playerClass = new PlayerClass(json.getJSONObject("class"));
            playerRace = new PlayerRace(json.getString("race"));
            level = json.getInt("level");
            hp = json.getInt("current_hp");

            JSONArray deckJson = json.getJSONArray("deck");
            deck = new ArrayList<>();
            for (int i = 0; i < deckJson.length(); i++){
                JSONObject cardJson = deckJson.getJSONObject(i);
                Card card = new Card(cardJson.getString("code"), MainActivity.playerDeckID);
                card.selectedInDeck = cardJson.getBoolean("selected");
                deck.add(card);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        generateNextCards();
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
            if (c.suit.equals(suit)) res.add(c);
        }

        return res;
    }

    private void generateNextCards() {
        int maxC = -1;
        int maxS = -1;
        int maxH = -1;
        int maxD = -1;

        for (int i = 0; i < deck.size(); i++){
            Card c = deck.get(i);
            switch (c.suit){
                case "C":
                    if (c.value > maxC) maxC = c.value;
                    break;
                case "S":
                    if (c.value > maxS) maxS = c.value;
                    break;
                case "H":
                    if (c.value > maxH) maxH = c.value;
                    break;
                case "D":
                    if (c.value > maxD) maxD = c.value;
                    break;
                default:
                    break;
            }
        }

        nextCardsToAdd = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int value = 0;
            String cardValue = "";
            String suit = "";
            switch (i){
                case 0:
                    suit = "C";
                    value = maxC;
                    break;
                case 1:
                    suit = "S";
                    value = maxS;
                    break;
                case 2:
                    suit = "H";
                    value = maxH;
                    break;
                case 3:
                    suit = "D";
                    value = maxD;
                    break;
                default:
                    break;
            }
            value++;

            switch (value){
                case 13:
                    cardValue = "K";
                    break;
                case 12:
                    cardValue = "Q";
                    break;
                case 11:
                    cardValue = "J";
                    break;
                case 1:
                    cardValue = "A";
                    break;
                default:
                    cardValue += value;
                    break;
            }

            if (value <= 13) nextCardsToAdd.add(new Card(cardValue + suit, MainActivity.playerDeckID));
        }
    }

    public ArrayList<Card> getNextCardsToAdd() { return nextCardsToAdd; }

    public void addCard(Card card){
        deck.add(card);
        nextCardsToAdd.remove(card);
        if (card.value == 13) return;

        int value = card.value + 1;
        String cardValue = "";
        switch (value){
            case 13:
                cardValue = "K";
                break;
            case 12:
                cardValue = "Q";
                break;
            case 11:
                cardValue = "J";
                break;
            case 1:
                cardValue = "A";
                break;
            default:
                cardValue += value;
                break;
        }
        nextCardsToAdd.add(new Card(cardValue + card.suit, MainActivity.playerDeckID));
    }

    public ArrayList<Card> getDeck() { return deck; }

    public int getLevel() { return level; }

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

    public int getDeckLength(){
        int res = 0;

        for (int i = 0; i < deck.size(); i++){
            Card card = deck.get(i);
            if (card.selectedInDeck){
                res++;
            }
        }

        return res;
    }

    public String getRaceClass() { return "Level " + level + " " + playerRace.name + " " + playerClass.name; }

    public int getSpellCastingAbility(){
        return getAttribute(playerClass.spellCastingAbility);
    }

    public boolean gainXP(int gain) {
        xp += gain;
        if (level < 20 && xp >= levelThresholds[level]){
            //levelUp();
            return true;
        }
        return false;
    }

    public int levelUp() {
        level++;

        int bonusHP = Math.max(1, Utils.rollDie(playerClass.hitDie) + modifiers[attributes[2]]);
        maxHP += bonusHP;
        hp = maxHP;

        return bonusHP;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();

        try {
            json.put("level", level);
            json.put("race", playerRace.name);
            json.put("class", playerClass.toJSON());
            json.put("current_hp", hp);

            JSONArray deckJson = new JSONArray();
            for (int i = 0; i < deck.size(); i++){
                JSONObject cardJson = new JSONObject();
                Card card = deck.get(i);
                cardJson.put("code", card.code);
                cardJson.put("selected", card.selectedInDeck);
                deckJson.put(cardJson);
            }
            json.put("deck", deckJson);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
