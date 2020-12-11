package com.example.dndeck_a6;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dndeck_a6.activities.CombatActivity;
import com.example.dndeck_a6.activities.MainActivity;
import com.example.dndeck_a6.game.Card;
import com.example.dndeck_a6.game.GameCharacter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DeckParserTask extends AsyncTask<String, Integer, Void> {

    private Context context;
    private WeakReference<AppCompatActivity> activityReference;
    private CardImageAdapter adapter;

    private String baseURL = "https://deckofcardsapi.com/api/deck/";

    public DeckParserTask(AppCompatActivity activity, Context context){
        this(activity, context, null);
    }

    public DeckParserTask(AppCompatActivity activity, Context context, CardImageAdapter adapter) {
        activityReference = new WeakReference<>(activity);
        this.context = context;
        this.adapter = adapter;
    }

    protected void onPreExecute() {
    }

    protected Void doInBackground(String... params) {
        String urlEnd = params[0];
        String urlString = baseURL + urlEnd;

        String[] urlDetails = urlEnd.split("/");

        URL url = null;
        try {
            System.setProperty("http.agent", "Chrome");
            url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String s = readStream(in);
                Log.i("Malan", "sent " + urlString);
                Log.i("Malan", "retrieved " + s);

                JSONObject json = new JSONObject(s);

                if (urlDetails[0].equals("new")) {
                    if (urlDetails.length == 1) { // new/ aka creating a new deck
                        if (MainActivity.playerDeckID.equals("new")) {
                            MainActivity.playerDeckID = json.getString("deck_id");
                        } else {
                            MainActivity.monsterDeckID = json.getString("deck_id");
                        }
                    }
                    else if (urlDetails.length > 1 && urlDetails[1].equals("shuffle")){
                        if (MainActivity.playerDeckID.equals("new")) {
                            MainActivity.playerDeckID = json.getString("deck_id");

                            String remaining = json.getString("remaining");
                            updateRemainingText(R.id.textPlayerDeckCount, remaining);
                        }
                        else {
                            MainActivity.monsterDeckID = json.getString("deck_id");

                            String remaining = json.getString("remaining");
                            updateRemainingText(R.id.textEnemyDeckCount, remaining);
                        }
                    }
                }
                else if (urlDetails[0].equals(MainActivity.playerDeckID)){

                    if (urlDetails.length > 1 && urlDetails[1].equals("draw") && adapter != null){

                        JSONArray cards = json.getJSONArray("cards");
                        draw(MainActivity.playerDeckID, cards);
                    }
                    else if (urlDetails.length > 3 && urlDetails[1].equals("pile") && urlDetails[2].equals("discard") && urlDetails[3].equals("list")){
                        // if asked for a list of the discard pile, it means it needs to be shuffled back into the deck

                        shuffleBack(MainActivity.playerDeckID, json.getJSONObject("piles").getJSONObject("discard").getJSONArray("cards"));
                    }

                    String remaining = json.getString("remaining");
                    if (remaining.equals("0")){
                        DeckParserTask listDiscardTask = new DeckParserTask(activityReference.get(), context);
                        listDiscardTask.execute(MainActivity.playerDeckID + "/pile/discard/list/");
                    }
                    updateRemainingText(R.id.textPlayerDeckCount, remaining);
                }
                else if (urlDetails[0].equals(MainActivity.monsterDeckID)){

                    if (urlDetails.length > 1 && urlDetails[1].equals("draw") && adapter != null){

                        JSONArray cards = json.getJSONArray("cards");
                        draw(MainActivity.monsterDeckID, cards);
                    }
                    else if (urlDetails.length > 3 && urlDetails[1].equals("pile") && urlDetails[2].equals("discard") && urlDetails[3].equals("list")){
                        // if asked for a list of the discard pile, it means it needs to be shuffled back into the deck

                        shuffleBack(MainActivity.monsterDeckID, json.getJSONObject("piles").getJSONObject("discard").getJSONArray("cards"));
                    }

                    String remaining = json.getString("remaining");
                    if (remaining.equals("0")){
                        DeckParserTask listDiscardTask = new DeckParserTask(activityReference.get(), context);
                        listDiscardTask.execute(MainActivity.monsterDeckID + "/pile/discard/list/");
                    }
                    updateRemainingText(R.id.textEnemyDeckCount, remaining);
                }

                return null;

            } catch (JSONException e) {
                Log.i("Malan", "JSONexception");
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        } catch (MalformedURLException e) {
            Log.i("Malan", "MalformedURLexception");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("Malan", "IOexception");
            e.printStackTrace();
        }

        Log.i("Malan", "returned null");
        return null;
    }

    protected void onProgressUpdate(Integer... values) {

    }

    protected void onPostExecute(Void v) {

    }


    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            Log.i("Malan", "IOException");
            return "";
        }
    }

    private void updateRemainingText(int id, String remaining){
        TextView text = (TextView)activityReference.get().findViewById(id);
        activityReference.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(remaining);
            }
        });
    }

    private void draw(String id, JSONArray cards){
        activityReference.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    for (int i = 0; i < cards.length(); i++) {
                        String code = cards.getJSONObject(i).getString("code");
                        adapter.add(new Card(code, id));
                    }
                }
                catch (JSONException e){
                    Log.i("Malan", "JSONexception");
                    e.printStackTrace();
                }
            }
        });
    }

    private void shuffleBack(String id, JSONArray cards){
        try{
            String shuffleBack = id + "/shuffle/?cards=";

            for (int i = 0; i < cards.length(); i++){
                shuffleBack += cards.getJSONObject(i).getString("code") + ",";
            }

            DeckParserTask shuffleTask = new DeckParserTask(activityReference.get(), context);
            shuffleTask.execute(shuffleBack);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }
}
