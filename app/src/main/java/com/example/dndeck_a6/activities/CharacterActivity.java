package com.example.dndeck_a6.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dndeck_a6.R;

public class CharacterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);

        TextView charName = (TextView)findViewById(R.id.textName);
        charName.setText(MainActivity.player.getName());

        TextView charRaceClass = (TextView)findViewById(R.id.textRaceClass);
        charRaceClass.setText(MainActivity.player.getRaceClass());

        TextView hp = (TextView)findViewById(R.id.textHP);
        hp.setText(getString(R.string.playerHP) + " " + MainActivity.player.getHpText());

        int[] attributes = MainActivity.player.getAttributes();
        for (int i = 0; i < 6; i++){
            TextView attrText;
            int textID;
            switch (i){
                case 0:
                    attrText = (TextView)findViewById(R.id.textStr);
                    textID = R.string.playerStr;
                    break;
                case 1:
                    attrText = (TextView)findViewById(R.id.textDex);
                    textID = R.string.playerDex;
                    break;
                case 2:
                    attrText = (TextView)findViewById(R.id.textCon);
                    textID = R.string.playerCon;
                    break;
                case 3:
                    attrText = (TextView)findViewById(R.id.textInt);
                    textID = R.string.playerInt;
                    break;
                case 4:
                    attrText = (TextView)findViewById(R.id.textWis);
                    textID = R.string.playerWis;
                    break;
                default:
                    attrText = (TextView)findViewById(R.id.textCha);
                    textID = R.string.playerCha;
                    break;
            }
            attrText.setText(getString(textID) + " " + attributes[i]);
        }

        Button decks = (Button)findViewById(R.id.buttonDeck);
        decks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DecksActivity.class);
                startActivity(intent);
            }
        });

        Button game = (Button)findViewById(R.id.buttonStart);
        game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getApplicationContext(), CombatActivity.class);
                startActivity(intent);*/
                finish();
            }
        });
    }
}