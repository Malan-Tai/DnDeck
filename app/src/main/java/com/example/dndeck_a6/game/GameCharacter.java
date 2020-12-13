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

    private String deckCodes = "AS,2S,KS,AD,2D,KD,AC,2C,KC,AH,2H,KH";

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
                List<Spell> tempList = tempSpells;
                if (tempSpells.size() > 4) {
                    tempList = tempSpells.subList(0, 4);
                }
                spells = new Spell[tempList.size()];
                for (int i = 0; i < spells.length; i++) {
                    spells[i] = tempList.get(i);
                    Log.i("Malan", "added spell " + spells[i].name);
                }
                setSpellsSuits();
            }
            catch (JSONException e) { }

            cardsToPlay = new ArrayList<>();

            size = json.getString("size");
            type = json.getString("type");
            difficulty = Double.parseDouble(json.getString("challenge_rating"));
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

    public Spell[] getSpells() { return spells; }

    public ArrayList<String> getSpellsSuits() {
        ArrayList<String> res = new ArrayList<>();
        if (spells == null) return res;

        for (int i = 0; i < spells.length; i++){
            res.add(spells[i].suit);
        }
        return res;
    }

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
            for (int i = 0; i < spells.length; i++){
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
                spells[i].suit = suit;
            }
        }
    }

    public int[] getAttributes() { return attributes; }

    public int getAttribute(String attr){
        int i = 0;
        switch (attr){
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
                actions.put(spells[i].toJSON());
            }
            json.put("actions", actions);
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
        ArrayList<String> usedSuits = getSpellsSuits();
        while (cardsToPlay.size() < 3){
            int max = -1;
            Card maxCard = null;
            for (int i = 0; i < adapter.getCount(); i++){
                Card card = adapter.getItem(i);
                if (card.value > max && !card.selectedInHand && usedSuits.contains(card.suit)){
                    max = card.value;
                    maxCard = card;
                }
            }
            if (maxCard == null) return; //there is no more possible card to play
            toggleCardToPlay(maxCard);
        }
    }
}
