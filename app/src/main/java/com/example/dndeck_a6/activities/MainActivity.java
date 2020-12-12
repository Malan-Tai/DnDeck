package com.example.dndeck_a6.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dndeck_a6.Database;
import com.example.dndeck_a6.DeckParserTask;
import com.example.dndeck_a6.DndParserTask;
import com.example.dndeck_a6.JsonAdapter;
import com.example.dndeck_a6.R;
import com.example.dndeck_a6.Utils;
import com.example.dndeck_a6.game.GameCharacter;
import com.example.dndeck_a6.game.Player;
import com.example.dndeck_a6.game.Save;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public static Player player;
    public static Save currentSave;

    public static String playerDeckID = "new";
    public static String monsterDeckID = "new";

    private static final int monstersNumber = 322;
    private static JSONArray allMonsters;

    private static Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentSave = new Save();

        DndParserTask monstersTask = new DndParserTask(this, getApplicationContext());
        monstersTask.execute("/api/monsters/");

        DeckParserTask task1 = new DeckParserTask(this, getApplicationContext());
        task1.execute("new/");

        DeckParserTask task2 = new DeckParserTask(this, getApplicationContext());
        task2.execute("new/"); //do it again for the monster deck

        database = new Database(getApplicationContext());

        Button newGame = (Button)findViewById(R.id.buttonNewGame);
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewGameActivity.playerRace = null;
                NewGameActivity.playerClass = null;
                Intent intent = new Intent(getApplicationContext(), NewGameActivity.class);
                startActivity(intent);
            }
        });

        Button loadGame = (Button)findViewById(R.id.buttonLoadGame);
        loadGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoadGameActivity.class);
                startActivity(intent);
            }
        });

        for (int m = -5; m <= 10; m++){
            int i = (m + 5) * 2;
            GameCharacter.modifiers[i] = m;
            if (m < 10){
                GameCharacter.modifiers[i + 1] = m;
            }
        }
    }

    public static void setAllMonsters(JSONArray jsonArray){
        allMonsters = jsonArray;
    }

    public static JSONObject getRandomMonster(){
        int i = Utils.rollDie(monstersNumber) - 1;
        try {
            return allMonsters.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveGame() {
        String saveJson = currentSave.toJSON().toString();
        Log.i("Malan", "saving game : " + saveJson);
        database.insertData(currentSave.id, saveJson);
    }

    public static ArrayList<Save> getSavedGames() {
        ArrayList<Save> saves = new ArrayList<>();
        ArrayList<String>[] dataSet = database.readData();
        ArrayList<String> data = dataSet[0];
        ArrayList<String> ids = dataSet[1];
        for (int i = 0; i < data.size(); i++){
            Log.i("Malan", "got saved game " + ids.get(i));
            saves.add(new Save(Long.parseLong(ids.get(i)), data.get(i)));
        }
        return saves;
    }

    public static void deleteSave(Save save){
        database.deleteSave(save.id);
    }
}