package com.example.dndeck_a6.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dndeck_a6.DndParserTask;
import com.example.dndeck_a6.R;
import com.example.dndeck_a6.game.PlayerRace;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class PlayerRaceActivity extends AppCompatActivity {
    private boolean selectedRace = false;
    public static PlayerRace playerRace = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_race);

        Button chooseButton = (Button)findViewById(R.id.buttonChooseRace);
        chooseButton.setBackgroundColor(getColor(R.color.material_on_background_disabled));
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickChoose();
            }
        });

        DndParserTask classesTask = new DndParserTask(this, getApplicationContext());
        classesTask.execute("/api/races/");

        ListView list = (ListView)findViewById(R.id.listRaces);
        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3){
                ClickRace(list, chooseButton, position);
            }
        });
    }

    private void ClickRace(ListView list, Button btn, int pos){
        JSONObject json = (JSONObject)list.getItemAtPosition(pos);

        DndParserTask raceTask = new DndParserTask(this, getApplicationContext());
        try {
            raceTask.execute(json.getString("url"));
            raceTask.get(); //wait for the task to finish

            selectedRace = true;
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

    private void ClickChoose(){
        if (selectedRace){
            /*PlayerRace race = new PlayerRace();

            TextView raceName = (TextView)findViewById(R.id.textRaceName);
            String name = raceName.getText().toString();
            race.name = name;
            Log.i("Malan", "race clicked " + name + " in class " + race.name + " gotten as " + raceName.getText());

            TextView raceDesc = (TextView)findViewById(R.id.textRaceDesc);
            String desc = raceDesc.getText().toString();
            String[] lines = desc.split("\n");
            for (int i = 0; i < lines.length; i++){
                String[] info = lines[i].split(": +");
                int bonus = Integer.parseInt(info[1]);
                switch (info[0]){
                    case "STR":
                        race.bonusStr = bonus;
                        break;
                    case "DEX":
                        race.bonusDex = bonus;
                        break;
                    case "CON":
                        race.bonusCon = bonus;
                        break;
                    case "INT":
                        race.bonusInt = bonus;
                        break;
                    case "WIS":
                        race.bonusWis = bonus;
                        break;
                    case "CHA":
                        race.bonusCha = bonus;
                        break;
                    default:
                        break;
                }
            }*/

            NewGameActivity.playerRace = playerRace;
            finish();
        }
    }
}