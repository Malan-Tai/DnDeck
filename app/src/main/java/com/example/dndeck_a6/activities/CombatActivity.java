package com.example.dndeck_a6.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
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
import com.example.dndeck_a6.Utils;
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

        Bundle extras = getIntent().getExtras();
        DndParserTask monsterTask = new DndParserTask(this, getApplicationContext());
        monsterTask.execute(extras.getString("url"));
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

        doingTurn = false;

        MainActivity.currentSave.savedActivity = Utils.SaveActivity.COMBAT_ACTIVITY;
        MainActivity.currentSave.monster = monster;
        MainActivity.currentSave.playerCurrentHP = MainActivity.player.getHp();
        MainActivity.saveGame();

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

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                //MainActivity.saveGame();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
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
                draw(monster.chooseDeckToDrawFrom(), monsterAdapter);
                monsterCount++;
            }
            canDoTurn = true;
        }
        else if (monsterCount < 6){
            draw(monster.chooseDeckToDrawFrom(), monsterAdapter);
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