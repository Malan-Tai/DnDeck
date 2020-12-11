package com.example.dndeck_a6.game;

import android.content.Context;
import android.util.Log;

import com.example.dndeck_a6.Utils;

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

    private String url;

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
            Log.i("Malan", "creating " + name);
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

            ArrayList<Spell> tempSpells = new ArrayList<>();
            JSONArray actions = json.getJSONArray("actions");
            for (int i = 0; i < actions.length(); i++){
                JSONObject action = actions.getJSONObject(i);
                JSONArray damageFields = action.getJSONArray("damage");
                if (damageFields.length() > 0){
                    tempSpells.add(new Spell(action, false));
                }
            }
            List<Spell> tempList = tempSpells;
            if (tempSpells.size() > 4){
                tempList = tempSpells.subList(0, 4);
            }
            spells = new Spell[tempList.size()];
            for (int i = 0; i < spells.length; i++){
                spells[i] = tempList.get(i);
                Log.i("Malan", "added spell " + spells[i].name);
            }
            setSpellsSuits();

            cardsToPlay = new ArrayList<>();

            desc += json.getString("size") + " " + json.getString("type") + " of difficulty " + json.getString("challenge_rating") + "\n";
            desc += "Average HP : " + json.getInt("hit_points") + " | Armor class : " + json.getString("armor_class") + "\n";
            desc += "STR : " + attributes[0] + " | " + "DEX : " + attributes[1] + " | " + "CON : " + attributes[3] + "\n";
            desc += "INT : " + attributes[3] + " | " + "WIS : " + attributes[4] + " | " + "CHA : " + attributes[5] + "\n";
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public Spell[] getSpells() { return spells; }

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
}
