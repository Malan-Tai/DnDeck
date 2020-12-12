package com.example.dndeck_a6.game;

import com.example.dndeck_a6.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Player extends GameCharacter {
    private PlayerClass playerClass;
    private PlayerRace playerRace;

    private ArrayList<Card> deck;

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
                if (j == 0) deck.add(new Card("A" + suit));
                else deck.add(new Card((j + 1) + suit));
            }
        }
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

    public String getRaceClass() { return "Level " + level + " " + playerRace.name + " " + playerClass.name; }

    public int getSpellCastingAbility(){
        return getAttribute(playerClass.spellCastingAbility);
    }

    public void gainXP(int gain) {
        xp += gain;
        if (level < 20 && xp >= levelThresholds[level]){
            levelUp();
        }
    }

    public void levelUp() {
        level++;

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
