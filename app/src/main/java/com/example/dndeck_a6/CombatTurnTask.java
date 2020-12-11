package com.example.dndeck_a6;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dndeck_a6.activities.CombatActivity;
import com.example.dndeck_a6.activities.MainActivity;
import com.example.dndeck_a6.game.Card;
import com.example.dndeck_a6.game.GameCharacter;
import com.example.dndeck_a6.game.Spell;
import com.example.dndeck_a6.game.SpellCast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class CombatTurnTask extends AsyncTask<Void, SpellCast, Void> {
    private Context context;
    private WeakReference<AppCompatActivity> activityReference;

    private CardImageAdapter playerAdapter;
    private CardImageAdapter monsterAdapter;

    private ArrayList<Card> discarded = new ArrayList<>();

    public CombatTurnTask(AppCompatActivity activity, Context context, CardImageAdapter player, CardImageAdapter monster){
        activityReference = new WeakReference<>(activity);
        this.context = context;

        playerAdapter = player;
        monsterAdapter = monster;
    }

    protected void onPreExecute() {
        CombatActivity.doingTurn = true;
    }

    protected Void doInBackground(Void... voids) {
        boolean actionsLeft = true;

        while (actionsLeft){
            Card playerCard = MainActivity.player.popNextCard();
            if (playerCard != null){
                discarded.add(playerCard);
                Spell spell = MainActivity.player.getSpell(playerCard.suit);
                publishProgress(new SpellCast(MainActivity.player, spell, playerCard, playerAdapter));
            }

            Card monsterCard = CombatActivity.monster.popNextCard();
            if (monsterCard != null) {
                discarded.add(monsterCard);
                Spell spell = CombatActivity.monster.getSpell(playerCard.suit);
                publishProgress(new SpellCast(CombatActivity.monster, spell, playerCard, monsterAdapter));
            }

            actionsLeft = monsterCard != null || playerCard != null;
        }

        return null;
    }

    protected void onProgressUpdate(SpellCast... spellCasts) {
        SpellCast spellCast = spellCasts[0];
        Log.i("Malan", spellCast.caster + " casts " + spellCast.spell.name + " with " + spellCast.card.code);
        spellCast.cast(context);

    }

    protected void onPostExecute(Void v) {
        TextView playerDraw = (TextView)activityReference.get().findViewById(R.id.textPlayerDeckCount);
        String playerRemaining = playerDraw.getText().toString();
        Log.i("Malan", "player remaining : " + playerRemaining);
        if (playerRemaining.equals("0")){
            DeckParserTask listDiscardTask = new DeckParserTask(activityReference.get(), context);
            listDiscardTask.execute(MainActivity.playerDeckID + "/pile/discard/list/");
        }

        TextView monsterDraw = (TextView)activityReference.get().findViewById(R.id.textEnemyDeckCount);
        String monsterRemaining = playerDraw.getText().toString();
        if (monsterRemaining.equals("0")){
            DeckParserTask listDiscardTask = new DeckParserTask(activityReference.get(), context);
            listDiscardTask.execute(MainActivity.monsterDeckID + "/pile/discard/list/");
        }

        GridView playerGrid = (GridView)activityReference.get().findViewById(R.id.gridPlayerHand);
        playerGrid.setAdapter(playerAdapter);

        GridView monsterGrid = (GridView)activityReference.get().findViewById(R.id.gridEnemyHand);
        monsterGrid.setAdapter(monsterAdapter);

        String playerDiscard = MainActivity.playerDeckID + "/pile/discard/add/?cards=";
        String monsterDiscard = MainActivity.monsterDeckID + "/pile/discard/add/?cards=";
        for (int i = 0; i < discarded.size(); i++){
            Card card = discarded.get(i);
            if (card.deckID.equals(MainActivity.playerDeckID)){
                playerDiscard += card.code + ",";
            }
            else {
                monsterDiscard += card.code + ",";
            }
        }

        DeckParserTask playerDiscardTask = new DeckParserTask(activityReference.get(), context);
        playerDiscardTask.execute(playerDiscard);

        DeckParserTask monsterDiscardTask = new DeckParserTask(activityReference.get(), context);
        monsterDiscardTask.execute(monsterDiscard);

        TextView monsterHP = (TextView)activityReference.get().findViewById(R.id.textEnemyHP);
        monsterHP.setText(CombatActivity.monster.getHpText());

        TextView playerHP = (TextView)activityReference.get().findViewById(R.id.textPlayerHP);
        playerHP.setText(MainActivity.player.getHpText());

        CombatActivity.doingTurn = false;
    }
}
