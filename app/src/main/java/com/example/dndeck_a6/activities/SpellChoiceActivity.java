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
import com.example.dndeck_a6.game.Spell;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SpellChoiceActivity extends AppCompatActivity {
    private boolean selectedSpell = false;
    private boolean clickToAdd = true;
    public static JSONObject currentSpell = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell_choice);

        TextView title = (TextView)findViewById(R.id.textSpells);
        title.setText(getString(R.string.playerSpells) + " " + getString(R.string.loading));

        ListView equippedList = (ListView)findViewById(R.id.listEquippedSpells);
        JsonAdapter array = new JsonAdapter(equippedList.getContext(), new ArrayList<>());
        equippedList.setAdapter(array);

        Button chooseButton = (Button)findViewById(R.id.buttonToggleSpell);
        chooseButton.setBackgroundColor(getColor(R.color.material_on_background_disabled));
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSpell) ClickToggle(array);
            }
        });

        equippedList.setClickable(true);
        equippedList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3){
                clickToAdd = false;
                updateToggleButton();

                ClickSpell(equippedList, chooseButton, position);
            }
        });

        try {
            String urlSpells = NewGameActivity.playerClass.json.getString("spells");
            DndParserTask classesTask = new DndParserTask(this, getApplicationContext());
            classesTask.execute(urlSpells);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        ListView list = (ListView)findViewById(R.id.listSpells);
        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3){
                clickToAdd = !array.contains((JSONObject)list.getItemAtPosition(position));
                updateToggleButton();

                ClickSpell(list, chooseButton, position);
            }
        });

        Button startButton = (Button)findViewById(R.id.buttonStart);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spell[] spells = getSpells(array);
                int count = list.getCount();
                int needed = Math.min(4, count);
                boolean countIsOk = spells.length == needed;
                if (!countIsOk) Toast.makeText(getApplicationContext(), "Please select exactly " + needed + " spells !", Toast.LENGTH_SHORT).show();
                else {
                    MainActivity.player.setSpells(spells);
                    if (spells.length == 4){
                        MainActivity.player.setSpellsSuits();

                        Intent intent = new Intent(getApplicationContext(), CharacterActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(getApplicationContext(), EquipmentChoiceActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void ClickSpell(ListView list, Button btn, int pos){
        JSONObject json = (JSONObject)list.getItemAtPosition(pos);

        DndParserTask classTask = new DndParserTask(this, getApplicationContext());
        try {
            classTask.execute(json.getString("url"));
            classTask.get(); //wait for the task to finish

            selectedSpell = true;
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
            adapter.add(currentSpell);
        }
        else {
            adapter.remove(currentSpell);
        }

        clickToAdd = !clickToAdd;
        updateToggleButton();
    }

    private void updateToggleButton(){
        Button chooseButton = (Button)findViewById(R.id.buttonToggleSpell);
        if (clickToAdd) chooseButton.setText(getString(R.string.equipSpell));
        else chooseButton.setText(getString(R.string.unequipSpell));
    }

    private Spell[] getSpells(JsonAdapter array) {
        Spell[] spells = new Spell[array.getCount()];
        for (int i = 0; i < array.getCount(); i++){
            spells[i] = new Spell(array.getItem(i));
        }
        return spells;
    }
}