package com.example.dndeck_a6.game;

import android.content.Context;
import android.util.Log;

import com.example.dndeck_a6.CardImageAdapter;
import com.example.dndeck_a6.Utils;
import com.example.dndeck_a6.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class GameCharacter {
    public static int[] modifiers = new int[31];

    protected String name;
    private String desc = "";

    protected Spell[] spells;
    protected int[] attributes;

    protected int maxHP;
    protected int hp;

    protected int armorClass;

    protected int xp = 0;

    private String url = "";

    private String size = "";
    private String type = "";
    private double difficulty = -1;
    private String avgHP = "";

    private String deckCodes = ""; //""AS,2S,KS,AD,2D,KD,AC,2C,KC,AH,2H,KH";

    protected ArrayList<Card> cardsToPlay;

    public GameCharacter(String n, Spell[] sp, int[] attr, int hits){
        name = n;
        spells = sp;
        attributes = attr;
        maxHP = hits;
        hp = hits;
        armorClass = 10 + modifiers[attr[1]]; // armor class for a player
        cardsToPlay = new ArrayList<>();
    }

    public GameCharacter(JSONObject json){
        try{
            name = json.getString("name");
            int n = name.indexOf("(");
            if (n != -1){
                name = name.substring(0, n);
            }
            Log.i("Malan", "creating " + name + " ; hp dice : " + json.getString("hit_dice"));
            armorClass = json.getInt("armor_class");

            attributes = new int[]{
                    json.getInt("strength"),
                    json.getInt("dexterity"),
                    json.getInt("constitution"),
                    json.getInt("intelligence"),
                    json.getInt("wisdom"),
                    json.getInt("charisma")
            };

            maxHP = Utils.rollDice(json.getString("hit_dice")) + modifiers[attributes[2]];
            hp = maxHP;

            url = json.getString("url");

            difficulty = Double.parseDouble(json.getString("challenge_rating"));
            xp = json.getInt("xp");

            try {
                ArrayList<Spell> tempSpells = new ArrayList<>();
                JSONArray actions = json.getJSONArray("actions");
                for (int i = 0; i < actions.length(); i++) {
                    JSONObject action = actions.getJSONObject(i);
                    try {
                        String range = action.getString("weapon_range");
                        tempSpells.add(new Weapon(action));
                    } catch (JSONException e) {
                        JSONArray damageFields = action.getJSONArray("damage");
                        if (damageFields.length() > 0) {
                            tempSpells.add(new Spell(action, false));
                        }
                    }
                }
                int len = Math.min(4, tempSpells.size());

                spells = new Spell[4];
                for (int i = 0; i < len; i++) {
                    spells[i] = tempSpells.get(i);
                    Log.i("Malan", "added spell " + spells[i].name);
                }
                for (int i = len; i < 4; i++){
                    spells[i] = new DoNothingSpell();
                    Log.i("Malan", "added pass spell");
                }
                setSpellsSuits();

                try {
                    deckCodes = json.getString("deck_codes");
                }
                catch (JSONException e) {
                    generateDeck();
                }
            }
            catch (JSONException e) { }

            cardsToPlay = new ArrayList<>();

            size = json.getString("size");
            type = json.getString("type");
            avgHP = json.getString("hit_points");
            desc += size + " " + type + " of difficulty " + difficulty + "\n";
            desc += "Average HP : " + avgHP + " | Armor class : " + armorClass + "\n";
            desc += "STR : " + attributes[0] + " | " + "DEX : " + attributes[1] + " | " + "CON : " + attributes[3] + "\n";
            desc += "INT : " + attributes[3] + " | " + "WIS : " + attributes[4] + " | " + "CHA : " + attributes[5] + "\n";
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void generateDeck() {
        /*Arrays.sort(spells, 0, 4, new Comparator<Spell>() {
            @Override
            public int compare(Spell o1, Spell o2) {
                return o1.getAverageDamage() - o2.getAverageDamage();
            }
        });*/
        deckCodes = "";

        for (int i = 0; i < 4; i++){
            String suit = spells[i].suit;
            if (spells[i].name.equals("")){ // pass spell
                deckCodes += "3" + suit + ",2" + suit + ",A" + suit + ",";
                continue;
            }

            int budget = 6 + (int)difficulty;
            ArrayList<Integer> addedList = new ArrayList<Integer>();
            int minLeft = 1;

            while (budget >= minLeft){
                int added = addedList.size();
                int minValue = Utils.sum(3 - added);
                int freedom = budget - minValue;
                if (freedom <= 3 - added){
                    for (int j = 3 - added; j > 0; j--){
                        int bonus = 0;
                        if (freedom > 0) bonus = Utils.rollDie(freedom);
                        while (addedList.contains(j + bonus) && bonus > 0){
                            bonus--;
                        }
                        freedom -= bonus;
                        addedList.add(j + bonus);
                    }
                    break;
                }

                if (budget == minLeft){
                    addedList.add(minLeft);
                    break;
                }

                minValue = Utils.sum(2 - added);
                int maxRandom = Math.min(13, budget - minValue);
                int random = Utils.rollDie(maxRandom);
                while (addedList.contains(random)){
                    random = Utils.rollDie(maxRandom);
                    Log.i("Malan", "in while");
                }
                budget -= random;
                addedList.add(random);
                while (addedList.contains(minLeft)){
                    minLeft++;
                }
            }

            for (int j = 0; j < addedList.size(); j++){
                String cardValue = "";
                int value = addedList.get(j);
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
                deckCodes += cardValue + suit + ",";
            }

            //Log.i("Malan", spells[i].name + " (" + suit + ") : " + spells[i].getAverageDamage());
        }
        Log.i("Malan", "generated deck : " + deckCodes);
    }

    public ArrayList<Spell> getSpells() {
        if (spells == null) return null;

        ArrayList<Spell> res = new ArrayList<>();
        for (int i = 0; i < spells.length; i++){
            Spell spell = spells[i];
            if (!spell.name.equals("")) res.add(spell);
        }
        return res;
    }

    /*public ArrayList<String> getSpellsSuits() {
        ArrayList<String> res = new ArrayList<>();
        if (spells == null) return res;

        for (int i = 0; i < spells.length; i++){
            res.add(spells[i].suit);
        }
        return res;
    }*/

    public Spell getSpell(String suit){
        for (int i = 0; i < spells.length; i++){
            if (spells[i].suit.equals(suit)){
                return spells[i];
            }
        }
        return null;
    }

    public void setSpellsSuits(){
        if (spells != null){
            ArrayList<String> suits = new ArrayList<>(Arrays.asList("C", "S", "H", "D"));
            for (int i = 0; i < spells.length; i++){
                if (spells[i].suit.equals("")) {
                    String suit = suits.get(Utils.rollDie(suits.size()) - 1);
                    suits.remove(suit);
                    spells[i].suit = suit;
                }
                else {
                    suits.remove(spells[i].suit);
                }
            }
        }
    }

    public int[] getAttributes() { return attributes; }

    public int getAttribute(String attr){
        int i = 0;
        switch (attr){
            case "FINE":
                if (attributes[0] >= attributes[1]) i = 0;
                else i = 1;
                break;
            case "STR":
                i = 0;
                break;
            case "DEX":
                i = 1;
                break;
            case "CON":
                i = 2;
                break;
            case "INT":
                i = 3;
                break;
            case "WIS":
                i = 4;
                break;
            case "CHA":
                i = 5;
                break;
            default:
                break;
        }

        return attributes[i];
    }

    public String getName() { return name; }

    public String getHpText() { return hp + " / " + maxHP; }

    public int getHp() { return hp; }

    public String getDescription() { return desc; }

    public String getUrl() { return url; }

    public String getDeckCodes() { return deckCodes; }

    public boolean toggleCardToPlay(Card card){
        Log.i("Malan", name + " toggled " + card.code);
        if (card.selectedInHand){
            cardsToPlay.remove(card);
            card.selectedInHand = false;
            return true;
        }
        if (cardsToPlay.size() < 3) {
            cardsToPlay.add(card);
            card.selectedInHand = true;
            return true;
        }
        return false;
    }

    public Card popNextCard() {
        if (cardsToPlay.size() <= 0) { return null; }

        Card card = cardsToPlay.get(0);
        cardsToPlay.remove(0);

        return card;
    }

    public ArrayList<Card> getCardsToPlay() { return new ArrayList<>(cardsToPlay); }

    public boolean abilityCheck(String dcType, int difficulty){
        int res = Utils.rollDie(20) + modifiers[getAttribute(dcType)];
        return res >= difficulty;
    }

    public void takeDamage(int dmg){
        hp -= dmg;
        Log.i("Malan", name + " took " + dmg + " damage : " + getHpText());
    }

    public int getXP() { return xp; }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        Log.i("Malan", "converting to json : " + name);

        try{
            json.put("name", name);
            json.put("armor_class", armorClass);

            json.put("strength", attributes[0]);
            json.put("dexterity", attributes[1]);
            json.put("constitution", attributes[2]);
            json.put("intelligence", attributes[3]);
            json.put("wisdom", attributes[4]);
            json.put("charisma", attributes[5]);

            json.put("hit_dice", maxHP - modifiers[attributes[2]]); //uses the fact that rollDice returns N if the string is only "N"

            json.put("url", url);

            json.put("xp", xp);

            json.put("size", size);
            json.put("type", type);
            json.put("challenge_rating", difficulty);
            json.put("hit_points", avgHP);

            JSONArray actions = new JSONArray();
            for (int i = 0; i < spells.length; i++) {
                JSONObject spellJson = spells[i].toJSON();
                if (spellJson != null) actions.put(spellJson);
            }
            json.put("actions", actions);

            json.put("deck_codes", deckCodes);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return json;
    }

    public String chooseDeckToDrawFrom() {
        if (difficulty <= 0) return MainActivity.playerDeckID;

        double inPlayerDeck = 50.0 * spells.length * (double)MainActivity.player.getLevel() / (4 * difficulty);
        int die = Utils.rollDie(100);
        if (die <= inPlayerDeck) return MainActivity.playerDeckID;

        return MainActivity.monsterDeckID;
    }

    public void chooseActions(CardImageAdapter adapter) {
        //ArrayList<String> usedSuits = getSpellsSuits();
        while (cardsToPlay.size() < 3){
            int max = -1;
            Card maxCard = null;
            for (int i = 0; i < adapter.getCount(); i++){
                Card card = adapter.getItem(i);
                int dmg = getSpell(card.suit).getAverageDamage();
                if (card.value * dmg > max && !card.selectedInHand){ // && usedSuits.contains(card.suit)){
                    max = card.value * dmg;
                    maxCard = card;
                }
            }
            if (maxCard == null) return; //there is no more possible card to play
            toggleCardToPlay(maxCard);
        }
    }
}
