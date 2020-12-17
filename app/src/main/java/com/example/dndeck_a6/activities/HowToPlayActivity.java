package com.example.dndeck_a6.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dndeck_a6.R;

public class HowToPlayActivity extends AppCompatActivity {

    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        Button prev = (Button)findViewById(R.id.buttonPrev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index > 0){
                    index--;
                    updateString();
                }
            }
        });

        Button next = (Button)findViewById(R.id.buttonNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index < 5){
                    index++;
                    updateString();
                }
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

    private void updateString() {
        TextView text = (TextView)findViewById(R.id.textHowTo);
        String string;
        switch (index){
            case 0:
                string = getString(R.string.howToAbilities);
                break;
            case 1:
                string = getString(R.string.howToRaceClass);
                break;
            case 2:
                string = getString(R.string.howToCard);
                break;
            case 3:
                string = getString(R.string.howToTurn);
                break;
            case 4:
                string = getString(R.string.howToDeath);
                break;
            case 5:
                string = getString(R.string.howToLevel);
                break;
            default:
                string = "";
                break;
        }
        text.setText(string);
    }
}