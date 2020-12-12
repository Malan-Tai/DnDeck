package com.example.dndeck_a6;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dndeck_a6.game.Save;

import java.util.ArrayList;

public class SavedGameAdapter extends ArrayAdapter<Save> {

    public SavedGameAdapter(Context context, ArrayList<Save> saves) {
        super(context, 0, saves);
    }

    public int getCount() {
        return super.getCount();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        Save save = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_saved_game_layout, parent, false);
        }

        TextView name = (TextView)convertView.findViewById(R.id.textName);
        name.setText(save.player.getName() + " (" + save.player.getHpText() + ")");

        TextView desc = (TextView)convertView.findViewById(R.id.textDesc);
        desc.setText(save.player.getRaceClass());

        return convertView;
    }
}
