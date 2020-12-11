package com.example.dndeck_a6.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dndeck_a6.DeckParserTask;
import com.example.dndeck_a6.R;
import com.example.dndeck_a6.game.GameCharacter;
import com.example.dndeck_a6.game.Player;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public static Player player;

    public static String playerDeckID = "new";
    public static String monsterDeckID = "new";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DeckParserTask task = new DeckParserTask(this, getApplicationContext());
        task.execute("new/");
        try {
            task.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        task = new DeckParserTask(this, getApplicationContext());
        task.execute("new/");                                               //do it again for the monster deck

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
}