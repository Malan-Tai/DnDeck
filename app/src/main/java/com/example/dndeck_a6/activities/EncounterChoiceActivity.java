package com.example.dndeck_a6.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dndeck_a6.DndParserTask;
import com.example.dndeck_a6.JsonAdapter;
import com.example.dndeck_a6.R;
import com.example.dndeck_a6.SpellAdapter;
import com.example.dndeck_a6.game.GameCharacter;
import com.example.dndeck_a6.game.Spell;

import org.json.JSONException;
import org.json.JSONObject;

public class EncounterChoiceActivity extends AppCompatActivity {

    private static int createMonstersIndex = 0;
    private static GameCharacter[] monsters = new GameCharacter[3];

    private String currentURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encounter_choice);

        for (int i = 0; i < monsters.length; i++){
            JSONObject json = MainActivity.getRandomMonster();
            try {
                DndParserTask task = new DndParserTask(this, getApplicationContext());
                task.execute(json.getString("url"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        while (createMonstersIndex < monsters.length){

        }

        Button monsterBtn1 = (Button)findViewById(R.id.buttonMonster1);
        monsterBtn1.setText(monsters[0].getName());
        monsterBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMonster(monsters[0]);
            }
        });

        Button monsterBtn2 = (Button)findViewById(R.id.buttonMonster2);
        monsterBtn2.setText(monsters[1].getName());
        monsterBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMonster(monsters[1]);
            }
        });

        Button monsterBtn3 = (Button)findViewById(R.id.buttonMonster3);
        monsterBtn3.setText(monsters[2].getName());
        monsterBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickMonster(monsters[2]);
            }
        });

        Button character = (Button)findViewById(R.id.buttonCharacterSheet);
        character.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CharacterActivity.class);
                startActivity(intent);
            }
        });

        Button choose = (Button)findViewById(R.id.buttonChooseEncounter);
        choose.setBackgroundColor(getColor(R.color.material_on_background_disabled));
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentURL.equals("")){
                    Intent intent = new Intent(getApplicationContext(), CombatActivity.class);
                    intent.putExtra("url", currentURL);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public static void addNewMonster(JSONObject json){
        if (createMonstersIndex < 3) {
            monsters[createMonstersIndex] = new GameCharacter(json);
            createMonstersIndex++;
        }
    }

    public static void clearMonsters() {
        monsters = new GameCharacter[3];
        createMonstersIndex = 0;
    }

    private void clickMonster(GameCharacter monster){
        currentURL = monster.getUrl();

        Button choose = (Button)findViewById(R.id.buttonChooseEncounter);
        choose.setBackgroundColor(getColor(R.color.design_default_color_primary));

        TextView spellName = (TextView)findViewById(R.id.textSpellName);
        spellName.setText("");

        TextView spellDesc = (TextView)findViewById(R.id.textSpellDesc);
        spellDesc.setText("");

        TextView monsterSpellsTxt = (TextView)findViewById(R.id.textMonsterSpells);
        monsterSpellsTxt.setText(R.string.monsterSpells);

        TextView monsterName = (TextView)findViewById(R.id.textMonsterName);
        monsterName.setText(monster.getName());

        TextView monsterDesc = (TextView)findViewById(R.id.textMonsterDesc);
        monsterDesc.setText(monster.getDescription());

        SpellAdapter adapter = new SpellAdapter(getApplicationContext(), monster.getSpells());
        ListView list = (ListView)findViewById(R.id.listMonsterSpells);
        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Spell spell = (Spell)list.getItemAtPosition(position);

                TextView spellName = (TextView)findViewById(R.id.textSpellName);
                spellName.setText(spell.name + " (" + spell.suit + ")");

                TextView spellDesc = (TextView)findViewById(R.id.textSpellDesc);
                spellDesc.setText(spell.desc);
            }
        });
        list.setAdapter(adapter);
    }
}