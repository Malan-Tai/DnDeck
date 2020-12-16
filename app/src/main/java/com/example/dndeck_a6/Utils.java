package com.example.dndeck_a6;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class Utils {

    public enum SaveActivity {
        COMBAT_ACTIVITY, ENCOUNTER_ACTIVITY, LEVEL_ACTIVITY
    }

    public static Random random = new Random();

    public static int rollDie(int n){
        return 1 + random.nextInt(n);
    }

    public static int rollDice(String ndkpb) { return rollDice(ndkpb, false, false); }

    public static int rollDice(String ndkpb, boolean crit, boolean player){
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

        if (crit && !player) b = averageDice(ndkpb); // crit damage for a monster (rolled dice + avg dmg)

        int sum = b;
        for (int i = 0; i < n; i++){
            sum += rollDie(k);
            if (crit && player) sum += rollDie(k); // crit damage for the player (rolled twice the dice + modifiers)
        }

        Log.i("Malan", "rolled (crit:" + crit +") " + ndkpb + " = " + sum);
        return sum;
    }

    public static int averageDice(String ndkpb){
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

        return b + n * (k + 1) / 2;
    }

    public static int sum(int includedUpper){
        int sum = 0;
        for (int k = 1; k <= includedUpper; k++){
            sum += k;
        }
        return sum;
    }

    public static void testGenerator(){
        for (int difficulty = 0; difficulty <= 30; difficulty++){
            int budget = 6 + (int)difficulty;
            ArrayList<Integer> addedList = new ArrayList<Integer>();
            int minLeft = 1;

            while (budget >= minLeft){
                int added = addedList.size();
                int minValue = Utils.sum(3 - added);
                int freedom = budget - minValue;
                if (freedom <= 3 - added){
                    for (int j = 3 - added; j > 0; j--){
                        int bonus = 0;
                        if (freedom > 0) bonus = Utils.rollDie(freedom);
                        while (addedList.contains(j + bonus) && bonus > 0){
                            bonus--;
                        }
                        freedom -= bonus;
                        addedList.add(j + bonus);
                    }
                    break;
                }

                if (budget == minLeft){
                    addedList.add(minLeft);
                    break;
                }

                minValue = Utils.sum(2 - added);
                int maxRandom = Math.min(13, budget - minValue);
                int random = Utils.rollDie(maxRandom);
                while (addedList.contains(random)){
                    random = Utils.rollDie(maxRandom);
                }
                budget -= random;
                addedList.add(random);
                while (addedList.contains(minLeft)){
                    minLeft++;
                }
            }

            String added = difficulty + 6 + " : ";
            for (int j = 0; j < addedList.size(); j++){
                added += addedList.get(j) + ",";
            }
            Log.i("Malan", added);
        }
    }

}
