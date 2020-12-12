package com.example.dndeck_a6.game;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.dndeck_a6.Utils;
import com.example.dndeck_a6.activities.CombatActivity;
import com.example.dndeck_a6.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Spell {
    public String name = "";
    public String desc = "";
    public String suit = "";

    protected int attackBonus = 0;

    protected String[] damageRolls;

    protected String difficultyClassAbility = "";
    protected int difficultyClass = 15;
    protected String difficultySuccess = "";

    protected String[] damageTypes = null;

    protected boolean player;

    public Spell(JSONObject json){
        this(json, true);
    }

    public Spell(JSONObject json, boolean player) {
        this.player = player;
        if (player) {
            try {
                name = json.getString("name");

                desc = "";
                damageRolls = new String[1];
                JSONObject dmg = json.getJSONObject("damage");
                try {
                    JSONObject dmgLvl = dmg.getJSONObject("damage_at_character_level");
                    desc += "Damage : " + dmgLvl.getString("1");
                    damageRolls[0] = dmgLvl.getString("1");
                } catch (JSONException e) {
                    try {
                        JSONObject dmgLvl = dmg.getJSONObject("damage_at_slot_level");
                        desc += "Damage : " + dmgLvl.getString("1");
                        damageRolls[0] = dmgLvl.getString("1");
                    } catch (JSONException e2) {
                        return;
                    }
                }
                try {
                    String dmgType = dmg.getJSONObject("damage_type").getString("name");
                    damageTypes = new String[] { dmgType };
                    desc += " " + dmgType;
                } catch (JSONException e) {
                }
                desc += "\n";

                try {
                    JSONObject dc = json.getJSONObject("dc");
                    String dcType = dc.getJSONObject("dc_type").getString("name");
                    String dcSuccess = dc.getString("dc_success");
                    desc += "If enemy succeeds a " + dcType + " test, it suffers ";
                    if (dcSuccess.equals("half")) desc += "half the damage. \n";
                    else desc += "no damage. \n";

                    difficultyClassAbility = dcType;
                    difficultySuccess = dcSuccess;
                } catch (JSONException e) {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                name = json.getString("name");

                desc = "";
                try {
                    attackBonus = json.getInt("attack_bonus");
                    desc += "Attack bonus : +" + attackBonus + "\n";

                } catch (JSONException e) {

                }

                desc += "Damage : ";
                JSONArray dmgFields = json.getJSONArray("damage");
                damageRolls = new String[dmgFields.length()];
                damageTypes = new String[dmgFields.length()];
                for (int i = 0; i < dmgFields.length(); i++){
                    damageRolls[i] = dmgFields.getJSONObject(i).getString("damage_dice");
                    desc += damageRolls[i] + " ";
                    try {
                        String dmgType = dmgFields.getJSONObject(i).getJSONObject("damage_type").getString("name");
                        damageTypes[i] = dmgType;
                        desc += dmgType + " ";
                    } catch (JSONException e) { damageTypes[i] = ""; }
                }

                try {
                    JSONObject dc = json.getJSONObject("dc");
                    difficultyClassAbility = dc.getJSONObject("dc_type").getString("name");
                    difficultySuccess = dc.getString("success_type");
                    difficultyClass = dc.getInt("dc_value");

                    desc += "\nIf player succeeds a " + difficultyClassAbility + " test of difficulty " + difficultyClass + ", it suffers ";
                    if (difficultySuccess.equals("half")) desc += "half the damage. \n";
                    else desc += "no damage. \n";

                } catch (JSONException e) {

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected Spell() {}

    public void cast(Context context, int value){
        GameCharacter target;
        if (player){
            target = CombatActivity.monster;
        }
        else{
            target = MainActivity.player;
        }
        if (target == null) return;

        boolean hit = false;
        if (difficultyClassAbility.equals("")){ // if the spell requires a hit roll and not an evade roll

            int hitRoll = 0;
            if (player){
                hitRoll = Utils.rollDie(20) + GameCharacter.modifiers[MainActivity.player.getSpellCastingAbility()];
            }
            else{
                hitRoll = Utils.rollDie(20) + attackBonus;
            }
            hit = hitRoll >= target.armorClass;
        }
        else {
            hit = target.abilityCheck(difficultyClassAbility, difficultyClass);
        }

        if (!hit) {
            Toast.makeText(context, name + " missed " + target.name + "!", Toast.LENGTH_SHORT).show();
            return;
        }

        int damage = 0;
        for (int i = 0; i < damageRolls.length; i++){
            damage += Utils.rollDice(damageRolls[i]);
        }

        String bonusRoll = "";
        if (value == 13) bonusRoll = "5d13";
        else if (value == 12) bonusRoll = "4d12";
        else if (value == 11) bonusRoll = "3d11";
        else if (value > 5) bonusRoll = "2d" + value;
        else bonusRoll = "1d" + value;
        damage += Utils.rollDice(bonusRoll);

        Toast.makeText(context, name + " hit " + target.name + " for " + damage + " damage!", Toast.LENGTH_SHORT).show();
        target.takeDamage(damage);
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        Log.i("Malan", "converting to json : " + name);

        /*if (player) {
            try {
                json.put("name", name);

                JSONObject dmg = new JSONObject();
                JSONObject dmgLvl = new JSONObject();
                dmgLvl.put("1", damageRolls[0]);
                dmg.put("damage_at_character_level", dmgLvl);

                if (damageTypes != null){
                    JSONObject dmgType = new JSONObject();
                    dmgType.put("name", damageTypes[0]);
                    dmg.put("damage_type", dmgType);
                }
                json.put("damage", dmg);

                if (!difficultyClassAbility.equals("")){
                    JSONObject dc = new JSONObject();
                    JSONObject dcType = new JSONObject();
                    dcType.put("name", difficultyClassAbility);
                    dc.put("dc_type", dcType);
                    dc.put("dc_success", difficultySuccess);
                    json.put("dc", dc);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {*/
        try {
            json.put("name", name);

            if (attackBonus != 0) json.put("attack_bonus", attackBonus);

            JSONArray dmgFields = new JSONArray(); //json.getJSONArray("damage");
            for (int i = 0; i < damageRolls.length; i++) {
                JSONObject dmgField = new JSONObject();
                dmgField.put("damage_dice", damageRolls[i]);

                if (damageTypes != null && !damageTypes[i].equals("")){
                    JSONObject dmgType = new JSONObject();
                    dmgType.put("name", damageTypes[i]);
                    dmgField.put("damage_type", dmgType);
                }

                dmgFields.put(dmgField);
            }
            json.put("damage", dmgFields);

            if (!difficultyClassAbility.equals("")){
                JSONObject dc = new JSONObject();
                JSONObject dcType = new JSONObject();
                dcType.put("name", difficultyClassAbility);
                dc.put("dc_type", dcType);
                dc.put("success_type", difficultySuccess);
                dc.put("dc_value", difficultyClass);
                json.put("dc", dc);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //}

        return json;
    }
}
