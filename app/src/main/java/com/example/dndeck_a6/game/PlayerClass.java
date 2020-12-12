package com.example.dndeck_a6.game;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class PlayerClass {

    public String name = "";
    public int hitDie = 0;
    public String spellCastingAbility = "";

    public JSONObject json;

    public PlayerClass(JSONObject json){
        try{
            name = json.getString("name");
            hitDie = json.getInt("hit_die");

            try {
                spellCastingAbility = json.getJSONObject("spellcasting").getJSONObject("spellcasting_ability").getString("name");
            }
            catch (JSONException e) {}

            this.json = json;
        }
        catch (JSONException e){
            Log.i("Malan", "JSONException");
            e.printStackTrace();
        }
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try{
            json.put("name", name);
            json.put("hit_die", hitDie);

            if (!spellCastingAbility.equals("")){
                JSONObject spellcasting = new JSONObject();
                JSONObject ability = new JSONObject();
                ability.put("name", spellCastingAbility);
                spellcasting.put("spellcasting_abiility", ability);
                json.put("spellcasting", spellcasting);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return json;
    }
}
