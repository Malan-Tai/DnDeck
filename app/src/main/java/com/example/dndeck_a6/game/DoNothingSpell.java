package com.example.dndeck_a6.game;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

public class DoNothingSpell extends Spell {

    public DoNothingSpell() {

    }


    @Override
    public void cast(Context context, int value){
        Log.i("Malan", "passed");
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }
}
