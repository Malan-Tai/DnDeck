package com.example.dndeck_a6;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonAdapter extends ArrayAdapter<JSONObject> {

    public JsonAdapter(Context context, ArrayList<JSONObject> jsonObjects) {
        super(context, 0, jsonObjects);
    }

    public int getCount()
    {
        return super.getCount();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        JSONObject json = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView text = (TextView) convertView.findViewById(android.R.id.text1);
        try {
            text.setText(json.getString("name"));
        }
        catch (JSONException e) {
            text.setText("");
            Log.i("Malan", "JSONException");
            e.printStackTrace();
        }

        return convertView;
    }

    public void remove(JSONObject obj){
        for (int i = 0; i < getCount(); i++){
            try {
                if (getItem(i).getString("name").equals(obj.getString("name"))) {
                    super.remove(getItem(i));
                    i--;
                }
            }
            catch (JSONException e) { }
        }
    }

    public boolean contains(JSONObject obj){
        for (int i = 0; i < getCount(); i++){
            try {
                if (getItem(i).getString("name").equals(obj.getString("name"))) {
                    return true;
                }
            }
            catch (JSONException e) { }
        }

        return false;
    }
}
