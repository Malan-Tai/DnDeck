package com.example.dndeck_a6.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dndeck_a6.DeckParserTask;
import com.example.dndeck_a6.DndParserTask;
import com.example.dndeck_a6.JsonAdapter;
import com.example.dndeck_a6.R;
import com.example.dndeck_a6.Utils;
import com.example.dndeck_a6.game.GameCharacter;
import com.example.dndeck_a6.game.Player;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public static Player player;

    public static String playerDeckID = "new";
    public static String monsterDeckID = "new";

    private static final int monstersNumber = 322;
    private static JSONArray allMonsters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DndParserTask monstersTask = new DndParserTask(this, getApplicationContext());
        monstersTask.execute("/api/monsters/");

        DeckParserTask task1 = new DeckParserTask(this, getApplicationContext());
        task1.execute("new/");
        /*try {
            task.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        DeckParserTask task2 = new DeckParserTask(this, getApplicationContext());
        task2.execute("new/");                                               //do it again for the monster deck

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
}