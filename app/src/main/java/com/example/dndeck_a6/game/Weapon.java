package com.example.dndeck_a6.game;

import android.content.Context;
import android.widget.Toast;

import com.example.dndeck_a6.Utils;
import com.example.dndeck_a6.activities.CombatActivity;
import com.example.dndeck_a6.activities.MainActivity;

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
                desc += "Melee weapon (uses STR to hit)\n";
                abilityToHit = "STR";
            }
            else{
                desc += "Ranged weapon (uses DEX to hit)\n";
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

        boolean hit = false;
        int hitRoll = 0;
        hitRoll = Utils.rollDie(20) + GameCharacter.modifiers[MainActivity.player.getAttribute(abilityToHit)];
        hit = hitRoll >= target.armorClass;

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

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("name", name);
            json.put("suit", suit);

            if (abilityToHit.equals("STR")){
                json.put("weapon_range", "Melee");
            }
            else{
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
