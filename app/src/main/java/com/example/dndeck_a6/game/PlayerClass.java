package com.example.dndeck_a6.game;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class PlayerClass {

    public String name = "";
    public int hitDie = 0;
    public String spellCastingAbility = "";

    public JSONObject json;

    public PlayerClass(){}
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
}
