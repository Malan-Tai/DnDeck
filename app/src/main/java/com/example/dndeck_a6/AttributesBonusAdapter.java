package com.example.dndeck_a6;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.dndeck_a6.activities.LevelUpActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AttributesBonusAdapter extends ArrayAdapter<Integer> {

    private int[] addedBonus;
    private int availableBonus;

    public AttributesBonusAdapter(Context context, Integer[] attributes, int[] bonus, int available) {
        super(context, 0, attributes);
        addedBonus = bonus;
        availableBonus = available;
    }

    public int getCount()
    {
        return super.getCount();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Integer attribute = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_attributes_bonus, parent, false);
        }

        TextView text = (TextView)convertView.findViewById(R.id.textAttribute);
        text.setText(getAttributeName(position) + attribute);

        Button plus = (Button)convertView.findViewById(R.id.buttonPlus);
        if (attribute >= 20 || availableBonus <= 0) plus.setBackgroundColor(Color.parseColor("#60000000"));
        else {
            plus.setBackgroundColor(Color.parseColor("#6200EE"));
            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LevelUpActivity.instance.increaseAttribute(position);
                }
            });
        }

        Button minus = (Button)convertView.findViewById(R.id.buttonMinus);
        if (attribute <= 1 || addedBonus[position] <= 0) minus.setBackgroundColor(Color.parseColor("#60000000"));
        else {
            minus.setBackgroundColor(Color.parseColor("#6200EE"));
            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LevelUpActivity.instance.decreaseAttribute(position);
                }
            });
        }

        return convertView;
    }

    private String getAttributeName(int i) {
        switch (i){
            case 0:
                return "STR : ";
            case 1:
                return "DEX : ";
            case 2:
                return "CON : ";
            case 3:
                return "INT : ";
            case 4:
                return "WIS : ";
            default:
                return "CHA : ";
        }
    }
}
