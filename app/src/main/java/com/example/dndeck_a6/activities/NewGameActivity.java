package com.example.dndeck_a6.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dndeck_a6.R;
import com.example.dndeck_a6.Utils;
import com.example.dndeck_a6.game.Player;
import com.example.dndeck_a6.game.PlayerClass;
import com.example.dndeck_a6.game.PlayerRace;

import org.json.JSONException;

import java.util.ArrayList;

public class NewGameActivity extends AppCompatActivity {

    private boolean rolledAttributes = false;
    private int[] attributes;

    public static PlayerRace playerRace = null;
    public static PlayerClass playerClass = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        EncounterChoiceActivity.clearMonsters();
        /*Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int[] extrasAttributes = extras.getIntArray("attributes");
            if (extrasAttributes != null) {
                rolledAttributes = true;
                attributes = extrasAttributes;
                updateSpinners();
            }
        }*/

        Button raceButton = (Button)findViewById(R.id.buttonRace);
        raceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlayerRaceActivity.class);
                startActivity(intent);
            }
        });

        Button classButton = (Button)findViewById(R.id.buttonClass);
        classButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlayerClassActivity.class);
                startActivity(intent);
            }
        });

        Button rollButton = (Button)findViewById(R.id.buttonRollAttributes);
        rollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollAttributes();
            }
        });

        Spinner spinnerCon = (Spinner)findViewById(R.id.spinnerCon);
        spinnerCon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateHP();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button startButton = (Button)findViewById(R.id.buttonStart);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkStartConditions()){
                    EditText name = (EditText)findViewById(R.id.editCharacterName);

                    int hp = 0;
                    if (playerClass != null) hp += 2 * playerClass.hitDie;
                    if (playerRace != null) hp += playerRace.bonusCon;

                    if (rolledAttributes) {
                        Spinner spinner = (Spinner) findViewById(R.id.spinnerCon);
                        int con = Integer.parseInt(spinner.getSelectedItem().toString());
                        hp += con;
                    }

                    MainActivity.player = new Player(name.getText().toString(), playerClass, playerRace, null, getOrderedAttributes(), hp);

                    try{
                        //check if class has spells
                        String spellsUrl = playerClass.json.getString("spells");
                        Intent intent = new Intent(getApplicationContext(), SpellChoiceActivity.class);
                        startActivity(intent);
                    }
                    catch (JSONException e){
                        //class does not have spells
                        Intent intent = new Intent(getApplicationContext(), EquipmentChoiceActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume(){
        Log.i("Malan", "resumed");
        super.onResume();

        //updateSpinners();
        if (playerRace != null){
            Log.i("Malan", "Resumed and chose race " + playerRace.name);
            Button raceButton = (Button)findViewById(R.id.buttonRace);
            raceButton.setText(getString(R.string.playerRace) + " " + playerRace.name);

            resetTexts();

            if (playerRace.bonusStr != 0){
                TextView text = (TextView)findViewById(R.id.textStr);
                text.setText(getString(R.string.playerStr) + " (+" + playerRace.bonusStr +")");
            }
            if (playerRace.bonusDex != 0){
                TextView text = (TextView)findViewById(R.id.textDex);
                text.setText(getString(R.string.playerDex) + " (+" + playerRace.bonusDex +")");
            }
            if (playerRace.bonusCon != 0){
                TextView text = (TextView)findViewById(R.id.textCon);
                text.setText(getString(R.string.playerCon) + " (+" + playerRace.bonusCon +")");
            }
            if (playerRace.bonusInt != 0){
                TextView text = (TextView)findViewById(R.id.textInt);
                text.setText(getString(R.string.playerInt) + " (+" + playerRace.bonusInt +")");
            }
            if (playerRace.bonusWis != 0){
                TextView text = (TextView)findViewById(R.id.textWis);
                text.setText(getString(R.string.playerWis) + " (+" + playerRace.bonusWis +")");
            }
            if (playerRace.bonusCha != 0){
                TextView text = (TextView)findViewById(R.id.textCha);
                text.setText(getString(R.string.playerCha) + " (+" + playerRace.bonusCha +")");
            }
        }

        if (playerClass != null) {
            Log.i("Malan", "Resumed and chose class " + playerClass.name);
            Button classButton = (Button) findViewById(R.id.buttonClass);
            classButton.setText(getString(R.string.playerClass) + " " + playerClass.name);
        }

        updateHP();
    }

    private void rollAttributes(){
        if (!rolledAttributes){
            rolledAttributes = true;
            Button rollButton = (Button)findViewById(R.id.buttonRollAttributes);
            rollButton.setBackgroundColor(getColor(R.color.material_on_background_disabled));

            attributes = new int[] { 0, 0, 0, 0, 0, 0 };
            //String attributesText = "-";
            for (int i = 0; i < 6; i++) {
                int min = 7;
                int sum = 0;
                for (int j = 0; j < 4; j++) {
                    int die = Utils.rollDie(6);
                    if (die < min) min = die;
                    sum += die;
                }
                attributes[i] = sum - min; //sum of the three best rolls
                //attributesText += " " + (sum - min) + " -";
            }

            //TextView attributesView = (TextView)findViewById(R.id.textAttributesResult);
            //attributesView.setText(attributesText);
            updateSpinners();
        }

    }

    private void updateSpinners() {
        if (rolledAttributes) {
            String attributesText = "-";
            for (int i = 0; i < 6; i++) {
                attributesText += " " + attributes[i] + " -";
            }
            TextView attributesView = (TextView)findViewById(R.id.textAttributesResult);
            attributesView.setText(attributesText);

            for (int i = 0; i < 6; i++) {
                Spinner spinner;
                switch (i){
                    case 0:
                        spinner = (Spinner)findViewById(R.id.spinnerStr);
                        break;
                    case 1:
                        spinner = (Spinner)findViewById(R.id.spinnerDex);
                        break;
                    case 2:
                        spinner = (Spinner)findViewById(R.id.spinnerCon);
                        break;
                    case 3:
                        spinner = (Spinner)findViewById(R.id.spinnerInt);
                        break;
                    case 4:
                        spinner = (Spinner)findViewById(R.id.spinnerWis);
                        break;
                    default:
                        spinner = (Spinner)findViewById(R.id.spinnerCha);
                        break;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.attributes_spinner_item);
                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                for (int j = 0; j < 6; j++) {
                    adapter.add(attributes[j] + "");
                }
                spinner.setAdapter(adapter);
                spinner.setSelection(i);
            }
        }
    }

    private void resetTexts(){
        for (int i = 0; i < 6; i++) {
            TextView text;
            switch (i){
                case 0:
                    text = (TextView)findViewById(R.id.textStr);
                    text.setText(R.string.playerStr);
                    break;
                case 1:
                    text = (TextView)findViewById(R.id.textDex);
                    text.setText(R.string.playerDex);
                    break;
                case 2:
                    text = (TextView)findViewById(R.id.textCon);
                    text.setText(R.string.playerCon);
                    break;
                case 3:
                    text = (TextView)findViewById(R.id.textInt);
                    text.setText(R.string.playerInt);
                    break;
                case 4:
                    text = (TextView)findViewById(R.id.textWis);
                    text.setText(R.string.playerWis);
                    break;
                default:
                    text = (TextView)findViewById(R.id.textCha);
                    text.setText(R.string.playerCha);
                    break;
            }

        }
    }

    public void updateHP(){
        String hpText = getString(R.string.playerHP);
        int hp = 0;
        if (playerClass != null) hp += 2 * playerClass.hitDie;
        if (playerRace != null) hp += playerRace.bonusCon;

        if (rolledAttributes) {
            Spinner spinner = (Spinner) findViewById(R.id.spinnerCon);
            int con = Integer.parseInt(spinner.getSelectedItem().toString());
            hp += con;
        }

        hpText += " " + hp;

        TextView text = (TextView)findViewById(R.id.textHP);
        text.setText(hpText);
    }

    public boolean checkStartConditions(){
        EditText editName = (EditText)findViewById(R.id.editCharacterName);
        String charName = editName.getText().toString();
        Log.i("Malan", "name : " + charName + " compared to " + getString(R.string.playerName));
        if (charName.equals(getString(R.string.playerName)) || charName.equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.invalidName), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (playerClass == null){
            Toast.makeText(getApplicationContext(), getString(R.string.noClass), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (playerRace == null){
            Toast.makeText(getApplicationContext(), getString(R.string.noRace), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!rolledAttributes){
            Toast.makeText(getApplicationContext(), getString(R.string.noAttributes), Toast.LENGTH_SHORT).show();
            return false;
        }

        ArrayList<String> usedAttributes = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Spinner spinner;
            switch (i) {
                case 0:
                    spinner = (Spinner) findViewById(R.id.spinnerStr);
                    break;
                case 1:
                    spinner = (Spinner) findViewById(R.id.spinnerDex);
                    break;
                case 2:
                    spinner = (Spinner) findViewById(R.id.spinnerCon);
                    break;
                case 3:
                    spinner = (Spinner) findViewById(R.id.spinnerInt);
                    break;
                case 4:
                    spinner = (Spinner) findViewById(R.id.spinnerWis);
                    break;
                default:
                    spinner = (Spinner) findViewById(R.id.spinnerCha);
                    break;
            }
            usedAttributes.add(spinner.getSelectedItem().toString());
        }

        for (int i = 0; i < 6; i++){
            String stringValue = attributes[i] + "";
            usedAttributes.remove(stringValue);
        }

        if (usedAttributes.size() > 0){
            Toast.makeText(getApplicationContext(), getString(R.string.matchingAttributes), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public int[] getOrderedAttributes(){
        int[] array = new int[6];
        for (int i = 0; i < 6; i++) {
            Spinner spinner;
            int bonus = 0;
            switch (i){
                case 0:
                    spinner = (Spinner)findViewById(R.id.spinnerStr);
                    bonus = playerRace.bonusStr;
                    break;
                case 1:
                    spinner = (Spinner)findViewById(R.id.spinnerDex);
                    bonus = playerRace.bonusDex;
                    break;
                case 2:
                    spinner = (Spinner)findViewById(R.id.spinnerCon);
                    bonus = playerRace.bonusCon;
                    break;
                case 3:
                    spinner = (Spinner)findViewById(R.id.spinnerInt);
                    bonus = playerRace.bonusInt;
                    break;
                case 4:
                    spinner = (Spinner)findViewById(R.id.spinnerWis);
                    bonus = playerRace.bonusWis;
                    break;
                default:
                    spinner = (Spinner)findViewById(R.id.spinnerCha);
                    bonus = playerRace.bonusCha;
                    break;
            }
            array[i] = Integer.parseInt(spinner.getSelectedItem().toString()) + bonus;
        }
        return array;
    }
}