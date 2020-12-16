package com.example.dndeck_a6.game;

import android.content.Context;
import android.widget.Toast;

import com.example.dndeck_a6.Utils;
import com.example.dndeck_a6.activities.CombatActivity;
import com.example.dndeck_a6.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Weapon extends Spell {

    private String abilityToHit = "";

    public Weapon(JSONObject json){
        player = true;
        try {
            name = json.getString("name");

            try {
                suit = json.getString("suit");
            } catch (JSONException e){ }

            desc = "";
            if (json.getString("weapon_range").equals("Melee")){
                boolean fine = false;
                try {
                    JSONArray properties = json.getJSONArray("properties");
                    for (int i = 0; i < properties.length(); i++){
                        if (properties.getJSONObject(i).getString("index").equals("finesse")){
                            fine = true;
                            break;
                        }
                    }
                }
                catch (JSONException e) { }

                if (!fine) {
                    desc += "Melee weapon (uses STR to hit and damage)\n";
                    abilityToHit = "STR";
                } else {
                    desc += "Fine melee weapon (uses either STR or DEX to hit and damage)\n";
                    abilityToHit = "FINE";
                }
            }
            else {
                desc += "Ranged weapon (uses DEX to hit and damage)\n";
                abilityToHit = "DEX";
            }

            JSONObject dmg = json.getJSONObject("damage");
            desc += "Damage : " + dmg.getString("damage_dice");
            damageRolls = new String[1];
            damageRolls[0] = dmg.getString("damage_dice");
            try {
                String dmgType = dmg.getJSONObject("damage_type").getString("name");
                damageTypes = new String[] { dmgType };
                desc += " " + dmgType;
            } catch (JSONException e) {
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void cast(Context context, int value) {
        GameCharacter target = CombatActivity.monster;
        if (target == null) return;

        int modifier = GameCharacter.modifiers[MainActivity.player.getAttribute(abilityToHit)];
        int hitRoll = Utils.rollDie(20) + modifier;
        boolean crit = hitRoll - modifier == 20;
        boolean hit = (hitRoll >= target.armorClass || crit) && !(hitRoll - modifier == 1); // taking into account critical failures and successes

        String critString = "";
        if (crit || hitRoll - modifier == 1) critString = " critically";

        if (!hit) {
            Toast.makeText(context, name + critString + " missed " + target.name + "!", Toast.LENGTH_SHORT).show();
            return;
        }

        int damage = modifier;
        for (int i = 0; i < damageRolls.length; i++){
            damage += Utils.rollDice(damageRolls[i], crit, true);
        }

        String bonusRoll = "";
        if (value == 13) bonusRoll = "5d13";
        else if (value == 12) bonusRoll = "4d12";
        else if (value == 11) bonusRoll = "3d11";
        else if (value > 5) bonusRoll = "2d" + value;
        else bonusRoll = "1d" + value;
        damage += Utils.rollDice(bonusRoll);

        Toast.makeText(context, name + critString + " hit " + target.name + " for " + damage + " damage!", Toast.LENGTH_SHORT).show();
        target.takeDamage(damage);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("name", name);
            json.put("suit", suit);

            if (abilityToHit.equals("FINE")){
                json.put("weapon_range", "Melee");
                JSONObject fine = new JSONObject();
                fine.put("index", "finesse");
                JSONArray properties = new JSONArray();
                properties.put(fine);
                json.put("properties", properties);
            }
            else if (abilityToHit.equals("STR")){
                json.put("weapon_range", "Melee");
            }
            else {
                json.put("weapon_range", "Ranged");
            }

            JSONObject dmg = new JSONObject();
            dmg.put("damage_dice", damageRolls[0]);

            if (damageTypes != null){
                JSONObject dmgType = new JSONObject();
                dmgType.put("name", damageTypes[0]);
                dmg.put("damage_type", dmgType);
            }
            json.put("damage", dmg);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return json;
    }
}
