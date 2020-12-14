package com.example.dndeck_a6.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dndeck_a6.CardImageAdapter;
import com.example.dndeck_a6.R;
import com.example.dndeck_a6.game.Spell;

import java.util.ArrayList;

public class DecksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decks);

        ArrayList<Spell> spells = MainActivity.player.getSpells();

        Button btnSpell1 = (Button)findViewById(R.id.buttonSpell1);
        btnSpell1.setText(spells.get(0).name);
        btnSpell1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSpell(spells.get(0));
            }
        });

        Button btnSpell2 = (Button)findViewById(R.id.buttonSpell2);
        btnSpell2.setText(spells.get(1).name);
        btnSpell2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSpell(spells.get(1));
            }
        });

        Button btnSpell3 = (Button)findViewById(R.id.buttonSpell3);
        btnSpell3.setText(spells.get(2).name);
        btnSpell3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSpell(spells.get(2));
            }
        });

        Button btnSpell4 = (Button)findViewById(R.id.buttonSpell4);
        btnSpell4.setText(spells.get(3).name);
        btnSpell4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSpell(spells.get(3));
            }
        });

        Button back = (Button)findViewById(R.id.buttonStart);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void clickSpell(Spell spell){
        TextView name = (TextView)findViewById(R.id.textName);
        name.setText(spell.name);

        TextView desc = (TextView)findViewById(R.id.textDesc);
        desc.setText(spell.desc);

        GridView grid = (GridView)findViewById(R.id.gridDeck);
        grid.setAdapter(new CardImageAdapter(getApplicationContext(), MainActivity.player.getSuit(spell.suit)));
    }
}