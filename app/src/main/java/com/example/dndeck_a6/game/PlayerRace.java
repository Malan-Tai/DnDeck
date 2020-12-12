package com.example.dndeck_a6.game;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlayerRace {

    public String name = "";
    public int bonusStr = 0;
    public int bonusDex = 0;
    public int bonusCon = 0;
    public int bonusInt = 0;
    public int bonusWis = 0;
    public int bonusCha = 0;

    public JSONObject json;

    public PlayerRace(String n) { name = n; }

    public PlayerRace(JSONObject json){
        try{
            name = json.getString("name");
            JSONArray bonuses = json.getJSONArray("ability_bonuses");
            for (int i = 0; i < bonuses.length(); i++){
                JSONObject jsonBonus = bonuses.getJSONObject(i);
                String attr = jsonBonus.getJSONObject("ability_score").getString("name");
                int bonus = jsonBonus.getInt("bonus");

                switch (attr){
                    case "STR":
                        bonusStr = bonus;
                        break;
                    case "DEX":
                        bonusDex = bonus;
                        break;
                    case "CON":
                        bonusCon = bonus;
                        break;
                    case "INT":
                        bonusInt = bonus;
                        break;
                    case "WIS":
                        bonusWis = bonus;
                        break;
                    case "CHA":
                        bonusCha = bonus;
                        break;
                    default:
                        break;
                }
            }
            this.json = json;
        }
        catch (JSONException e){
            Log.i("Malan", "JSONException");
            e.printStackTrace();
        }
    }
}
