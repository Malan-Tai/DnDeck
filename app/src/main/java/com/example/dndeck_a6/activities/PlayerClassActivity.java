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
import com.example.dndeck_a6.game.PlayerClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class PlayerClassActivity extends AppCompatActivity {
    private boolean selectedClass = false;
    public static PlayerClass playerClass = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_class);

        Button chooseButton = (Button)findViewById(R.id.buttonChooseClass);
        chooseButton.setBackgroundColor(getColor(R.color.material_on_background_disabled));
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickChoose();
            }
        });

        DndParserTask classesTask = new DndParserTask(this, getApplicationContext());
        classesTask.execute("/api/classes/");

        ListView list = (ListView)findViewById(R.id.listClasses);
        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3){
                ClickClass(list, chooseButton, position);
            }
        });
    }

    private void ClickClass(ListView list, Button btn, int pos){
        JSONObject json = (JSONObject)list.getItemAtPosition(pos);

        DndParserTask classTask = new DndParserTask(this, getApplicationContext());
        try {
            classTask.execute(json.getString("url"));
            classTask.get(); //wait for the task to finish

            selectedClass = true;
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
        if (selectedClass){
            NewGameActivity.playerClass = playerClass;
            finish();
        }
    }
}