package com.example.dndeck_a6;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dndeck_a6.activities.CombatActivity;
import com.example.dndeck_a6.activities.EncounterChoiceActivity;
import com.example.dndeck_a6.activities.EquipmentChoiceActivity;
import com.example.dndeck_a6.activities.MainActivity;
import com.example.dndeck_a6.activities.PlayerClassActivity;
import com.example.dndeck_a6.activities.PlayerRaceActivity;
import com.example.dndeck_a6.activities.SpellChoiceActivity;
import com.example.dndeck_a6.game.GameCharacter;
import com.example.dndeck_a6.game.PlayerClass;
import com.example.dndeck_a6.game.PlayerRace;

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
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class DndParserTask extends AsyncTask<String, Integer, Void> {

    private Context context;
    private WeakReference<AppCompatActivity> activityReference;

    private String baseURL = "https://www.dnd5eapi.co";

    public DndParserTask(AppCompatActivity activity, Context context) {
        activityReference = new WeakReference<>(activity);
        this.context = context;
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
                Log.i("Malan", "retrieved " + s);

                JSONObject json = new JSONObject(s);

                switch (urlDetails[2]){
                    case "classes":
                        if (urlDetails.length == 3) { // /api/classes/ aka list of all classes
                            parseClasses(json);
                        }
                        else if (urlDetails.length == 4) { // /api/classes/xxx aka a given class
                            parseClass(json);
                        }
                        else if (urlDetails.length == 5 && urlDetails[4].equals("spells")) { // /api/classes/xxx/spells aka list of all spells of a given class
                            parseSpells(json);
                        }
                        break;

                    case "races":
                        if (urlDetails.length == 3) { // /api/races/ aka list of all races
                            parseRaces(json);
                        }
                        else if (urlDetails.length == 4) { // /api/races/xxx aka a given race
                            parseRace(json);
                        }
                        break;

                    case "spells":
                        if (urlDetails.length == 4){ // /api/spells/xxx aka a given spell
                            parseSpell(json);
                        }
                        break;

                    case "starting-equipment":
                        if (urlDetails.length == 4){ // /api/starting-equipment/xxx aka the starting equipment of a given class
                            parseEquipment(json);
                        }
                        break;

                    case "equipment":
                        if (urlDetails.length == 4){ // /api/equipment/xxx aka a given equipment
                            parseWeapon(json);
                        }
                        break;

                    case "monsters":
                        if (urlDetails.length == 3){ // /api/monsters/ aka all monsters
                            MainActivity.setAllMonsters(json.getJSONArray("results"));
                        }
                        else if (urlDetails.length == 4){ // /api/monsters/xxx aka a given monster
                            CombatActivity.monster = new GameCharacter(json);
                            EncounterChoiceActivity.addNewMonster(json);
                            try{
                                getMonsterImage(json.getString("index"));
                            }
                            catch (JSONException e){
                                Log.i("Malan", "JSONexception");
                                e.printStackTrace();
                            }
                        }
                        break;

                    default:
                        break;
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

    private void parseClasses(JSONObject json){
        Log.i("Malan", "parsing classes");
        activityReference.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray classes = json.getJSONArray("results");
                    ListView list = (ListView)activityReference.get().findViewById(R.id.listClasses);
                    JsonAdapter array = new JsonAdapter(list.getContext(), new ArrayList<>());

                    for (int i = 0; i < classes.length(); i++) {
                        array.add(classes.getJSONObject(i));
                    }

                    list.setAdapter(array);
                }
                catch (JSONException e){
                    Log.i("Malan", "JSONexception");
                    e.printStackTrace();
                }
            }
        });
    }

    private void parseClass(JSONObject json){
        Log.i("Malan", "parsing class");
        activityReference.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    PlayerClassActivity.playerClass = new PlayerClass(json);

                    TextView className = (TextView)activityReference.get().findViewById(R.id.textClassName);
                    className.setText(json.getString("name"));

                    TextView classDesc = (TextView)activityReference.get().findViewById(R.id.textClassDesc);

                    String desc = "Hit Die : " + PlayerClassActivity.playerClass.hitDie + "\n";
                    try {
                        String spellcasting = json.getJSONObject("spellcasting").getJSONObject("spellcasting_ability").getString("name");
                        desc += "You cast spells using your " + spellcasting + " score.\n";
                    }
                    catch (JSONException e) {}

                    classDesc.setText(desc);
                }
                catch (JSONException e){
                    Log.i("Malan", "JSONexception");
                    e.printStackTrace();
                }
            }
        });
    }

    private void parseRaces(JSONObject json){
        Log.i("Malan", "parsing races");
        activityReference.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray classes = json.getJSONArray("results");
                    ListView list = (ListView)activityReference.get().findViewById(R.id.listRaces);
                    JsonAdapter array = new JsonAdapter(list.getContext(), new ArrayList<>());

                    for (int i = 0; i < classes.length(); i++) {
                        array.add(classes.getJSONObject(i));
                    }

                    list.setAdapter(array);
                }
                catch (JSONException e){
                    Log.i("Malan", "JSONexception");
                    e.printStackTrace();
                }
            }
        });
    }

    private void parseRace(JSONObject json){
        Log.i("Malan", "parsing race");
        activityReference.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    PlayerRaceActivity.playerRace = new PlayerRace(json);

                    TextView raceName = (TextView)activityReference.get().findViewById(R.id.textRaceName);
                    raceName.setText(json.getString("name"));

                    TextView raceDesc = (TextView)activityReference.get().findViewById(R.id.textRaceDesc);
                    JSONArray bonuses = json.getJSONArray("ability_bonuses");
                    String bonusText = "";
                    for (int i = 0; i < bonuses.length(); i++){
                        JSONObject jsonBonus = bonuses.getJSONObject(i);
                        String attr = jsonBonus.getJSONObject("ability_score").getString("name");
                        int bonus = jsonBonus.getInt("bonus");
                        bonusText += attr + ": +" + bonus + "\n";
                    }
                    raceDesc.setText(bonusText);
                }
                catch (JSONException e){
                    Log.i("Malan", "JSONexception");
                    e.printStackTrace();
                }
            }
        });
    }

    private void parseSpells(JSONObject json){
        Log.i("Malan", "parsing spells");
        ListView list = (ListView)activityReference.get().findViewById(R.id.listSpells);
        JsonAdapter array = new JsonAdapter(list.getContext(), new ArrayList<>());
        try {
            JSONArray spells = json.getJSONArray("results");

            for (int i = 0; i < spells.length(); i++) {
                JSONObject spell = spells.getJSONObject(i);
                if (checkSpellEligibility(spell, 1)){
                    array.add(spell);
                }
            }
        }
        catch (JSONException e){
            Log.i("Malan", "JSONexception");
            e.printStackTrace();
        }

        activityReference.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                list.setAdapter(array);
                TextView title = (TextView)activityReference.get().findViewById(R.id.textSpells);
                title.setText(activityReference.get().getString(R.string.playerSpells));
            }
        });
    }

    private void parseSpell(JSONObject json) {
        Log.i("Malan", "parsing spell");
        activityReference.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    SpellChoiceActivity.currentSpell = json;

                    TextView spellName = (TextView) activityReference.get().findViewById(R.id.textName);
                    spellName.setText(json.getString("name"));

                    TextView spellDesc = (TextView) activityReference.get().findViewById(R.id.textDesc);
                    String desc = "";

                    JSONObject dmg = json.getJSONObject("damage");
                    try{
                        JSONObject dmgLvl = dmg.getJSONObject("damage_at_character_level");
                        desc += "Damage : " + dmgLvl.getString("1");
                    } catch (JSONException e){
                        try {
                            JSONObject dmgLvl = dmg.getJSONObject("damage_at_slot_level");
                            desc += "Damage : " + dmgLvl.getString("1");
                        }
                        catch (JSONException e2) { return; }
                    }
                    try {
                        String dmgType = dmg.getJSONObject("damage_type").getString("name");
                        desc += " " + dmgType;
                    } catch (JSONException e) { }
                    desc += "\n";

                    try{
                        JSONObject dc = json.getJSONObject("dc");
                        String dcType = dc.getJSONObject("dc_type").getString("name");
                        String dcSuccess = dc.getString("dc_success");
                        desc += "If enemy succeeds a " + dcType + " test, it suffers ";
                        if (dcSuccess.equals("half")) desc += "half the damage. \n";
                        else desc += "no damage. \n";
                    }
                    catch (JSONException e){

                    }

                    spellDesc.setText(desc);

                } catch (JSONException e) {
                    Log.i("Malan", "JSONexception");
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean checkSpellEligibility(JSONObject json, int maxSpellLvl){ //checks if spell is of eligible level and does damage
        URL url = null;
        try {
            String urlEnd = json.getString("url");
            String urlString = baseURL + urlEnd;

            System.setProperty("http.agent", "Chrome");
            url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String s = readStream(in);
                JSONObject spell = new JSONObject(s);

                int lvl = spell.getInt("level");
                if (lvl > maxSpellLvl) return false;
                try {
                    JSONObject dmg = spell.getJSONObject("damage");
                } catch (JSONException e){
                    return false;
                }

                return true;

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
        } catch (JSONException e) {
            Log.i("Malan", "JSONexception");
            e.printStackTrace();
        }

        return false;
    }

    private void parseEquipment(JSONObject json){
        Log.i("Malan", "parsing equipment");
        ListView list = (ListView)activityReference.get().findViewById(R.id.listWeapons);
        JsonAdapter array = new JsonAdapter(list.getContext(), new ArrayList<>());
        try {
            JSONArray givenEq = json.getJSONArray("starting_equipment");
            for (int i = 0; i < givenEq.length(); i++){
                JSONObject equipment = givenEq.getJSONObject(i).getJSONObject("equipment");

                if (checkEquipmentIsWeapon(equipment)){
                    array.add(equipment);
                }
            }

            JSONArray choiceEq = json.getJSONArray("starting_equipment_options");
            for (int i = 0; i < choiceEq.length(); i++){
                JSONArray choices = choiceEq.getJSONObject(i).getJSONArray("from");
                for (int j = 0; j < choices.length(); j++) {
                    try {
                        JSONObject choice = choices.getJSONObject(j);
                        try {
                            JSONObject equipment = choice.getJSONObject("equipment");
                            if (checkEquipmentIsWeapon(equipment) && !array.contains(equipment)) {
                                array.add(equipment);
                            }
                        } catch (JSONException e) { // the choice item is not a given item but within a list of possible items
                            try {
                                JSONObject optionObj = choice.getJSONObject("equipment_option");
                                JSONObject eqCategory = optionObj.getJSONObject("from").getJSONObject("equipment_category");
                                String categoryName = eqCategory.getString("index");
                                if (categoryName.contains("weapon")) {
                                    fillAdapterFromEquipmentCategory(eqCategory, array);
                                }
                            }
                            catch (JSONException e1) { // the choice is not a list of possible items neither, but a numbered option
                                try {
                                    for (int k = 0; k < 4; k++){
                                        JSONObject numberedOption = choice.getJSONObject(String.valueOf(k));
                                        try {
                                            JSONObject equipment = numberedOption.getJSONObject("equipment");
                                            if (checkEquipmentIsWeapon(equipment) && !array.contains(equipment)) {
                                                array.add(equipment);
                                            }
                                        } catch (JSONException e2) { // the choice item is not a given item but within a list of possible items
                                            JSONObject optionObj = numberedOption.getJSONObject("equipment_option");
                                            JSONObject eqCategory = optionObj.getJSONObject("from").getJSONObject("equipment_category");
                                            String categoryName = eqCategory.getString("index");
                                            if (categoryName.contains("weapon")) {
                                                fillAdapterFromEquipmentCategory(eqCategory, array);
                                            }
                                        }
                                    }
                                } catch (JSONException e2) { }
                            }
                        }
                    }
                    catch (JSONException e){ //the choice is not a single item but a list of possible items
                        JSONArray choice = choices.getJSONArray(j);
                        for (int k = 0; k < choice.length(); k++){
                            JSONObject innerChoice = choice.getJSONObject(k);
                            try {
                                JSONObject equipment = innerChoice.getJSONObject("equipment");
                                if (checkEquipmentIsWeapon(equipment) && !array.contains(equipment)) {
                                    array.add(equipment);
                                }
                            } catch (JSONException e1) { // the choice item is not a given item but within a list of possible items
                                JSONObject optionObj = innerChoice.getJSONObject("equipment_option");
                                JSONObject eqCategory = optionObj.getJSONObject("from").getJSONObject("equipment_category");
                                String categoryName = eqCategory.getString("index");
                                if (categoryName.contains("weapon")) {
                                    fillAdapterFromEquipmentCategory(eqCategory, array);
                                }
                            }
                        }
                    }
                }
            }

        }
        catch (JSONException e){
            Log.i("Malan", "JSONexception");
            e.printStackTrace();
        }

        activityReference.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                list.setAdapter(array);
                TextView title = (TextView)activityReference.get().findViewById(R.id.textWeapons);
                title.setText(activityReference.get().getString(R.string.playerWeapons));
            }
        });
    }

    private void parseWeapon(JSONObject json) {
        Log.i("Malan", "parsing weapon");
        activityReference.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    EquipmentChoiceActivity.currentWeapon = json;

                    TextView weaponName = (TextView) activityReference.get().findViewById(R.id.textName);
                    weaponName.setText(json.getString("name"));

                    TextView weaponDesc = (TextView) activityReference.get().findViewById(R.id.textDesc);
                    String desc = "";

                    if (json.getString("weapon_range").equals("Melee")){
                        boolean fine = false;
                        try {
                            JSONArray properties = json.getJSONArray("properties");
                            for (int i = 0; i < properties.length(); i++){
                                if (properties.getJSONObject(i).getString("index").equals("finesse")){
                                    fine = true;
                                    break;
                                }
                            }
                        }
                        catch (JSONException e) { }

                        if (!fine) {
                            desc += "Melee weapon (uses STR to hit and damage)\n";
                        } else {
                            desc += "Fine melee weapon (uses either STR or DEX to hit and damage)\n";
                        }
                    }
                    else{
                        desc += "Ranged weapon (uses DEX to hit)\n";
                    }

                    JSONObject dmg = json.getJSONObject("damage");
                    desc += "Damage : " + dmg.getString("damage_dice");
                    try {
                        String dmgType = dmg.getJSONObject("damage_type").getString("name");
                        desc += " " + dmgType;
                    } catch (JSONException e) {
                    }
                    desc += "\n";

                    weaponDesc.setText(desc);

                } catch (JSONException e) {
                    Log.i("Malan", "JSONexception");
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean checkEquipmentIsWeapon(JSONObject json){
        URL url = null;
        try {
            String urlEnd = json.getString("url");
            String urlString = baseURL + urlEnd;

            System.setProperty("http.agent", "Chrome");
            url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String s = readStream(in);
                JSONObject equipment = new JSONObject(s);

                if (equipment.getJSONObject("equipment_category").getString("index").equals("weapon")){
                    return true;
                }

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
        } catch (JSONException e) {
            Log.i("Malan", "JSONexception");
            e.printStackTrace();
        }

        return false;
    }

    private void fillAdapterFromEquipmentCategory(JSONObject json, JsonAdapter adapter){
        URL url = null;
        try {
            String urlEnd = json.getString("url");
            String urlString = baseURL + urlEnd;

            System.setProperty("http.agent", "Chrome");
            url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String s = readStream(in);
                JSONObject category = new JSONObject(s);
                JSONArray equipmentList = category.getJSONArray("equipment");
                for (int i = 0; i < equipmentList.length(); i++){
                    JSONObject eq = equipmentList.getJSONObject(i);
                    if (!adapter.contains(eq)){
                        adapter.add(eq);
                    }
                }

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
        } catch (JSONException e) {
            Log.i("Malan", "JSONexception");
            e.printStackTrace();
        }
    }

    private void getMonsterImage(String monsterIndex){
        ImageView image = (ImageView)activityReference.get().findViewById(R.id.imageMonster);

        monsterIndex = monsterIndex.replaceAll("adult-", "");
        monsterIndex = monsterIndex.replaceAll("young-", "");
        monsterIndex = monsterIndex.replaceAll("ancient-", "");
        monsterIndex = monsterIndex.replaceAll("-wyrmling", "");
        String aidedd = "https://www.aidedd.org/dnd/images/";
        String urlString = aidedd + monsterIndex + ".jpg";

        URL url = null;
        Bitmap bitmap = null;
        try {
            System.setProperty("http.agent", "Chrome");
            url = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            bitmap = BitmapFactory.decodeStream(in);
        }
        catch (MalformedURLException e) {
            Log.i("Malan", "MalformedURLexception");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.i("Malan", "IOexception");
            e.printStackTrace();

            urlString = aidedd + "demon-" + monsterIndex + ".jpg";
            try {
                System.setProperty("http.agent", "Chrome");
                url = new URL(urlString);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                bitmap = BitmapFactory.decodeStream(in);
            }
            catch (MalformedURLException e2) {
                Log.i("Malan", "MalformedURLexception");
                e2.printStackTrace();
            }
            catch (IOException e2) {
                Log.i("Malan", "IOexception");
                e2.printStackTrace();
            }
        }

        if (bitmap != null && image != null){
            final Bitmap bitmapRes = bitmap;
            activityReference.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    image.setAdjustViewBounds(true);
                    image.setImageBitmap(bitmapRes);
                }
            });
        }
    }

}
