package com.example.dndeck_a6;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dndeck_a6.game.Spell;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SpellAdapter extends ArrayAdapter<Spell> {

    public SpellAdapter(Context context, Spell[] spells) {
        super(context, 0, spells);
    }

    public int getCount()
    {
        return super.getCount();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        Spell spell = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView text = (TextView) convertView.findViewById(android.R.id.text1);
        text.setText(spell.name);

        return convertView;
    }
}
