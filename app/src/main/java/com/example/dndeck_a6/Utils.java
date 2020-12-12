package com.example.dndeck_a6;

import android.util.Log;

import java.util.Random;

public class Utils {

    public enum SaveActivity {
        COMBAT_ACTIVITY, ENCOUNTER_ACTIVITY, LEVEL_ACTIVITY
    }

    public static Random random = new Random();

    public static int rollDie(int n){
        return 1 + random.nextInt(n);
    }

    public static int rollDice(String ndkpb){
        String[] values = ndkpb.split("d");
        if (values.length == 1) return Integer.parseInt(values[0]); //if the string is only "1" or "16" for example

        int n = Integer.parseInt(values[0]);
        int k = 0;
        int b = 0;
        if (values[1].contains("+")){
            int i = values[1].indexOf("+");
            k = Integer.parseInt(values[1].substring(0, i));
            b = Integer.parseInt(values[1].substring(i + 1));
        }
        else{
            k = Integer.parseInt(values[1]);
        }

        int sum = b;
        for (int i = 0; i < n; i++){
            sum += rollDie(k);
        }

        Log.i("Malan", "rolled " + ndkpb + " = " + sum);
        return sum;
    }

}
