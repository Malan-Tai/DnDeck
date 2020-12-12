package com.example.dndeck_a6.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.dndeck_a6.Utils;
import com.example.dndeck_a6.activities.CombatActivity;
import com.example.dndeck_a6.activities.EncounterChoiceActivity;
import com.example.dndeck_a6.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Save {
    public Player player;
    public int playerCurrentHP;

    public Utils.SaveActivity savedActivity;
    public GameCharacter monster = null;
    public GameCharacter[] monsters = null;

    public long id = -1;

    private JSONObject json;

    public Save() { }

    public Save(long id, String jsonString){
        Log.i("Malan", "creating save " + id);
        this.id = id;
        try {
            json = new JSONObject(jsonString);
            player = new Player(json.getJSONObject("player"));
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            JSONObject player = MainActivity.player.toJSON();
            player.put("current_hp", playerCurrentHP);
            json.put("player", player);
            json.put("player_deck_id", MainActivity.playerDeckID);
            json.put("monster_deck_id", MainActivity.monsterDeckID);

            switch (savedActivity){
                case ENCOUNTER_ACTIVITY:
                    JSONArray monstersJson = new JSONArray();
                    for (int i = 0; i < monsters.length; i++){
                        monstersJson.put(monsters[i].toJSON());
                    }
                    json.put("activity", "Encounters");
                    json.put("monsters", monstersJson);
                    break;
                case COMBAT_ACTIVITY:
                    json.put("activity", "Combat");
                    json.put("monster", monster.toJSON());
                    break;
                case LEVEL_ACTIVITY:
                    json.put("activity", "Level");
                    break;
                default:
                    break;
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return json;
    }

    public void LoadGame(Activity activity, Context context) {
        try {
            MainActivity.playerDeckID = json.getString("player_deck_id");
            MainActivity.monsterDeckID = json.getString("monster_deck_id");
            MainActivity.currentSave = this;
            String activityString = json.getString("activity");
            switch (activityString){
                case "Encounters":
                    EncounterChoiceActivity.clearMonsters();
                    JSONArray monsters = json.getJSONArray("monsters");
                    for (int i = 0; i < monsters.length(); i++){
                        EncounterChoiceActivity.addNewMonster(monsters.getJSONObject(i));
                    }

                    Intent intent = new Intent(context, EncounterChoiceActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                    break;
                case "Combat":
                    CombatActivity.monster = new GameCharacter(json.getJSONObject("monster"));

                    intent = new Intent(context, CombatActivity.class);
                    intent.putExtra("url", CombatActivity.monster.getUrl());
                    activity.startActivity(intent);
                    activity.finish();
                    break;
                /*case "Level":
                    break;*/
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
