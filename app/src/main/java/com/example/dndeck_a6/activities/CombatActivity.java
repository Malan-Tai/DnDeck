package com.example.dndeck_a6.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dndeck_a6.CardImageAdapter;
import com.example.dndeck_a6.CombatTurnTask;
import com.example.dndeck_a6.DeckParserTask;
import com.example.dndeck_a6.DndParserTask;
import com.example.dndeck_a6.R;
import com.example.dndeck_a6.game.Card;
import com.example.dndeck_a6.game.GameCharacter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CombatActivity extends AppCompatActivity {
    public static GameCharacter monster = null;
    public static boolean doingTurn = false;
    private boolean canDoTurn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combat);

        String url = "/api/monsters/adult-silver-dragon";
        DndParserTask monsterTask = new DndParserTask(this, getApplicationContext());
        monsterTask.execute(url);
        try{
            monsterTask.get();
        } catch (ExecutionException e){
            Log.i("Malan", "ExecutionException");
            e.printStackTrace();
            finish();
        } catch (InterruptedException e){
            Log.i("Malan", "InterruptedException");
            e.printStackTrace();
            finish();
        }

        DeckParserTask deckTask1 = new DeckParserTask(this, getApplicationContext());
        deckTask1.execute(MainActivity.playerDeckID + "/shuffle/?cards=" + MainActivity.player.getDeckCodes());
        DeckParserTask deckTask2 = new DeckParserTask(this, getApplicationContext());
        deckTask2.execute(MainActivity.monsterDeckID + "/shuffle/?cards=" + monster.getDeckCodes());

        TextView monsterName = (TextView)findViewById(R.id.textMonsterName);
        monsterName.setText(monster.getName());

        updateHP();

        GridView playerGrid = (GridView)findViewById(R.id.gridPlayerHand);
        CardImageAdapter playerAdapter = new CardImageAdapter(getApplicationContext(), new ArrayList<>(), true, true);
        playerGrid.setAdapter(playerAdapter);

        GridView monsterGrid = (GridView)findViewById(R.id.gridEnemyHand);
        CardImageAdapter monsterAdapter = new CardImageAdapter(getApplicationContext(), new ArrayList<>(), true, false);
        monsterGrid.setAdapter(monsterAdapter);

        ImageView imagePlayerDeck = (ImageView)findViewById(R.id.imagePlayerDeck);
        imagePlayerDeck.setClickable(true);
        imagePlayerDeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!doingTurn) {
                    playerDraw(MainActivity.playerDeckID, playerAdapter, monsterAdapter);
                }
            }
        });

        ImageView imageMonsterDeck = (ImageView)findViewById(R.id.imageEnemyDeck);
        imageMonsterDeck.setClickable(true);
        imageMonsterDeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!doingTurn) {
                    playerDraw(MainActivity.monsterDeckID, playerAdapter, monsterAdapter);
                }
            }
        });

        CombatActivity instance = this;
        Button endTurn = (Button)findViewById(R.id.buttonEndTurn);
        endTurn.setBackgroundColor(getColor(R.color.material_on_background_disabled));
        endTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canDoTurn){
                    canDoTurn = false;
                    endTurn.setBackgroundColor(getColor(R.color.material_on_background_disabled));
                    CombatTurnTask turnTask = new CombatTurnTask(instance, getApplicationContext(), playerAdapter, monsterAdapter);
                    turnTask.execute();
                }
            }
        });
    }

    private void playerDraw(String id, CardImageAdapter playerAdapter, CardImageAdapter monsterAdapter){
        int playerCount = playerAdapter.getCount();
        int monsterCount = monsterAdapter.getCount();

        if (playerCount < 6){
            draw(id, playerAdapter);
            playerCount++;
        }
        else {
            Toast.makeText(getApplicationContext(), "Your hand is full !", Toast.LENGTH_SHORT).show();
        }

        if (playerCount >= 6){
            Button endTurn = (Button)findViewById(R.id.buttonEndTurn);
            endTurn.setBackgroundColor(getColor(R.color.design_default_color_primary));

            while (monsterCount < 6){
                draw(MainActivity.monsterDeckID, monsterAdapter);
                monsterCount++;
            }
            canDoTurn = true;
        }
        else if (monsterCount < 6){
            draw(MainActivity.monsterDeckID, monsterAdapter);
            monsterCount++;
        }
    }

    private void draw(String id, CardImageAdapter adapter){
        DeckParserTask task = new DeckParserTask(this, getApplicationContext(), adapter);
        task.execute(id + "/draw/?count=1");
    }

    private void updateHP(){
        TextView monsterHP = (TextView)findViewById(R.id.textEnemyHP);
        monsterHP.setText(monster.getHpText());

        TextView playerHP = (TextView)findViewById(R.id.textPlayerHP);
        playerHP.setText(MainActivity.player.getHpText());
    }
}