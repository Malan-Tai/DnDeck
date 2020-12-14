package com.example.dndeck_a6.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dndeck_a6.AttributesBonusAdapter;
import com.example.dndeck_a6.CardImageAdapter;
import com.example.dndeck_a6.R;
import com.example.dndeck_a6.game.GameCharacter;
import com.example.dndeck_a6.game.Spell;

public class LevelUpActivity extends AppCompatActivity {

    public static LevelUpActivity instance;
    private int attributesBonus = 0;
    private int[] addedBonus = new int[] { 0, 0, 0, 0, 0, 0 };

    private ListView list;

    private int prevConModifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_up);
        instance = this;
        prevConModifier = GameCharacter.modifiers[MainActivity.player.getAttribute("CON")];

        int bonusHP = MainActivity.player.levelUp();

        int lvl = MainActivity.player.getLevel();
        if (lvl == 4 || lvl == 8 || lvl == 12 || lvl == 16 || lvl == 19) attributesBonus = 2;

        TextView textHP = (TextView)findViewById(R.id.textGainHP);
        textHP.setText("You gained " + bonusHP + " HP and were healed to full health.");

        CardImageAdapter cardAdapter = new CardImageAdapter(getApplicationContext(), MainActivity.player.getNextCardsToAdd(), false, true, true);
        GridView cardGrid = (GridView)findViewById(R.id.gridCardChoice);
        cardGrid.setAdapter(cardAdapter);

        if (attributesBonus > 0){
            updateAttributesText();

            list = findViewById(R.id.listAttributesBonus);
            setAdapter();
        }

        Button confirm = (Button)findViewById(R.id.buttonStart);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardAdapter.levelUpSelectedCard != null) MainActivity.player.addCard(cardAdapter.levelUpSelectedCard);
                Intent intent = new Intent(getApplicationContext(), EncounterChoiceActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void clickSpell(String suit){
        Spell spell = MainActivity.player.getSpell(suit);
        TextView spellName = (TextView) findViewById(R.id.textName);
        TextView spellDesc = (TextView) findViewById(R.id.textDesc);
        spellName.setText(spell.name);
        spellDesc.setText(spell.desc);
    }

    public void updateAttributesText() {
        TextView text = (TextView)findViewById(R.id.textAttributesBonus);
        text.setText(getString(R.string.attributeIncrease) + " (" + attributesBonus + " points left)");
    }

    public void increaseAttribute(int i) {
        attributesBonus--;
        addedBonus[i]++;
        MainActivity.player.getAttributes()[i]++;

        setAdapter();
    }

    public void decreaseAttribute(int i) {
        attributesBonus++;
        addedBonus[i]--;
        MainActivity.player.getAttributes()[i]--;

        setAdapter();
    }

    private void setAdapter() {
        int[] attributes = MainActivity.player.getAttributes();
        Integer[] array = new Integer[6];
        for (int i = 0; i < 6; i++){
            array[i] = attributes[i];
        }

        AttributesBonusAdapter adapter = new AttributesBonusAdapter(getApplicationContext(), array, addedBonus, attributesBonus);
        list.setAdapter(adapter);

        TextView text = (TextView)findViewById(R.id.textNote);
        int curConModifier = GameCharacter.modifiers[MainActivity.player.getAttribute("CON")];
        if (prevConModifier != curConModifier){
            text.setText("Note : you will gain " + MainActivity.player.getLevel() + " additional HP.");
        }
        else {
            text.setText("");
        }

        updateAttributesText();
    }
}







