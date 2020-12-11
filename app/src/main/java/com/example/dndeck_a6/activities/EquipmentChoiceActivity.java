package com.example.dndeck_a6.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dndeck_a6.DndParserTask;
import com.example.dndeck_a6.JsonAdapter;
import com.example.dndeck_a6.R;
import com.example.dndeck_a6.game.Weapon;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class EquipmentChoiceActivity extends AppCompatActivity {

    private boolean selectedWeapon = false;
    private boolean clickToAdd = true;
    public static JSONObject currentWeapon = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_choice);

        TextView title = (TextView)findViewById(R.id.textWeapons);
        title.setText(getString(R.string.playerWeapons) + " " + getString(R.string.loading));

        ListView equippedList = (ListView)findViewById(R.id.listEquippedWeapons);
        JsonAdapter array = new JsonAdapter(equippedList.getContext(), new ArrayList<>());
        equippedList.setAdapter(array);

        Button chooseButton = (Button)findViewById(R.id.buttonToggleWeapon);
        chooseButton.setBackgroundColor(getColor(R.color.material_on_background_disabled));
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedWeapon) ClickToggle(array);
            }
        });

        equippedList.setClickable(true);
        equippedList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3){
                clickToAdd = false;
                updateToggleButton();

                ClickWeapon(equippedList, chooseButton, position);
            }
        });

        try {
            String urlWeapons = NewGameActivity.playerClass.json.getString("starting_equipment");
            DndParserTask classesTask = new DndParserTask(this, getApplicationContext());
            classesTask.execute(urlWeapons);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        ListView list = (ListView)findViewById(R.id.listWeapons);
        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3){
                clickToAdd = !array.contains((JSONObject)list.getItemAtPosition(position));
                updateToggleButton();

                ClickWeapon(list, chooseButton, position);
            }
        });

        Button startButton = (Button)findViewById(R.id.buttonStart);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Weapon[] weapons = getWeapons(array);
                int needed = 4;
                if (MainActivity.player.getSpells() != null){
                    needed -= MainActivity.player.getSpells().length;
                }
                boolean countIsOk = weapons.length == needed;
                if (!countIsOk) Toast.makeText(getApplicationContext(), "Please select exactly " + needed + " weapons !", Toast.LENGTH_SHORT).show();
                else {
                    MainActivity.player.setWeapons(weapons);
                    MainActivity.player.setSpellsSuits();

                    Intent intent = new Intent(getApplicationContext(), CharacterActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void ClickWeapon(ListView list, Button btn, int pos){
        JSONObject json = (JSONObject)list.getItemAtPosition(pos);

        DndParserTask classTask = new DndParserTask(this, getApplicationContext());
        try {
            classTask.execute(json.getString("url"));
            classTask.get(); //wait for the task to finish

            selectedWeapon = true;
            btn.setBackgroundColor(getColor(R.color.design_default_color_primary));
        }
        catch (JSONException e){
            Log.i("Malan", "JSONException");
            e.printStackTrace();
        }
        catch (ExecutionException e){
            Log.i("Malan", "ExecutionException");
            e.printStackTrace();
        }
        catch (InterruptedException e){
            Log.i("Malan", "InterruptedException");
            e.printStackTrace();
        }

    }

    private void ClickToggle(JsonAdapter adapter){
        if (clickToAdd){
            adapter.add(currentWeapon);
        }
        else {
            adapter.remove(currentWeapon);
        }

        clickToAdd = !clickToAdd;
        updateToggleButton();
    }

    private void updateToggleButton(){
        Button chooseButton = (Button)findViewById(R.id.buttonToggleWeapon);
        if (clickToAdd) chooseButton.setText(getString(R.string.equipWeapon));
        else chooseButton.setText(getString(R.string.unequipWeapon));
    }

    private Weapon[] getWeapons(JsonAdapter array) {
        Weapon[] weapons = new Weapon[array.getCount()];
        for (int i = 0; i < array.getCount(); i++){
            weapons[i] = new Weapon(array.getItem(i));
        }
        return weapons;
    }
}