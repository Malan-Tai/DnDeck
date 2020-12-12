package com.example.dndeck_a6.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dndeck_a6.R;
import com.example.dndeck_a6.SavedGameAdapter;
import com.example.dndeck_a6.game.Save;

import java.util.ArrayList;

public class LoadGameActivity extends AppCompatActivity {

    private ArrayList<Save> savedGames;
    private Save selectedSave = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);
        Activity instance = this;

        savedGames = MainActivity.getSavedGames();

        TextView name = (TextView)findViewById(R.id.textName);
        TextView desc = (TextView)findViewById(R.id.textDesc);

        Button sheetButton = (Button)findViewById(R.id.buttonCharacterSheet);
        sheetButton.setBackgroundColor(getColor(R.color.material_on_background_disabled));
        sheetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSave != null){
                    Intent intent = new Intent(getApplicationContext(), CharacterActivity.class);
                    startActivity(intent);
                }
            }
        });

        Button startButton = (Button)findViewById(R.id.buttonStart);
        startButton.setBackgroundColor(getColor(R.color.material_on_background_disabled));
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSave != null){
                    selectedSave.LoadGame(instance, getApplicationContext());
                }
            }
        });

        SavedGameAdapter adapter = new SavedGameAdapter(getApplicationContext(), savedGames);
        ListView list = (ListView)findViewById(R.id.listSavedGames);
        list.setAdapter(adapter);
        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSave = (Save)list.getItemAtPosition(position);
                MainActivity.player = selectedSave.player;
                name.setText(selectedSave.player.getName());
                desc.setText(selectedSave.player.getRaceClass());

                sheetButton.setBackgroundColor(getColor(R.color.design_default_color_primary));
                startButton.setBackgroundColor(getColor(R.color.design_default_color_primary));
            }
        });

        ImageButton delete = (ImageButton)findViewById(R.id.imageButtonTrash);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSave != null) {
                    adapter.remove(selectedSave);
                    MainActivity.deleteSave(selectedSave);
                    list.setAdapter(adapter);

                    selectedSave = null;
                    name.setText("");
                    desc.setText("");
                    sheetButton.setBackgroundColor(getColor(R.color.material_on_background_disabled));
                    startButton.setBackgroundColor(getColor(R.color.material_on_background_disabled));
                }
            }
        });
    }
}