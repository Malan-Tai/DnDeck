package com.example.dndeck_a6;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.dndeck_a6.activities.LevelUpActivity;
import com.example.dndeck_a6.activities.MainActivity;
import com.example.dndeck_a6.game.Card;
import com.example.dndeck_a6.game.Spell;

import java.util.ArrayList;

public class CardImageAdapter extends ArrayAdapter<Card> {

    private boolean combat;
    private boolean isPlayer;
    private boolean levelUp;

    public Card levelUpSelectedCard = null;

    public CardImageAdapter(Context context, ArrayList<Card> cards) {
        this(context, cards, false, true, false);
    }

    public CardImageAdapter(Context context, ArrayList<Card> cards, boolean inCombat, boolean player) {
        this(context, cards, inCombat, player, false);
    }

    public CardImageAdapter(Context context, ArrayList<Card> cards, boolean inCombat, boolean player, boolean level) {
        super(context, 0, cards);
        combat = inCombat;
        isPlayer = player;
        levelUp = level;
    }

    public int getCount()
    {
        return super.getCount();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        Card card = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_line_layout, parent, false);
        }

        String code = card.code.toLowerCase();
        if (combat && !isPlayer) code = "back";
        String uri = "drawable/img_" + code;
        int imageResource = getContext().getResources().getIdentifier(uri, null, getContext().getPackageName());

        ImageView image = (ImageView)convertView.findViewById(R.id.listImage);
        image.setImageResource(imageResource);
        image.setClickable(isPlayer);
        image.setAdjustViewBounds(true);

        if (levelUp) {
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (card == levelUpSelectedCard){
                        levelUpSelectedCard = null;
                        image.clearColorFilter();
                    }
                    else if (levelUpSelectedCard == null){
                        image.setColorFilter(Color.argb(0.5f, 0, 0, 0));
                        levelUpSelectedCard = card;
                        LevelUpActivity.instance.clickSpell(card.suit);
                    }
                    else {
                        Toast.makeText(getContext(), "You can only choose one new card.", Toast.LENGTH_SHORT).show();
                        LevelUpActivity.instance.clickSpell(card.suit);
                    }
                }
            });
        }
        else if (!combat) {
            if (card.selectedInDeck) {
                image.clearColorFilter();
            } else {
                image.setColorFilter(Color.argb(0.5f, 0, 0, 0));
            }

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (card.selectedInDeck && MainActivity.player.getDeckLength() <= 12){
                        Toast.makeText(getContext(), "You can't have less than 12 cards selected", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    card.selectedInDeck = !card.selectedInDeck;
                    if (card.selectedInDeck) {
                        image.clearColorFilter();
                    } else {
                        image.setColorFilter(Color.argb(0.5f, 0, 0, 0));
                    }
                }
            });
        }
        else if (isPlayer){
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean didToggle = MainActivity.player.toggleCardToPlay(card);
                    if (!card.selectedInHand) {
                        image.clearColorFilter();
                    } else {
                        image.setColorFilter(Color.argb(0.5f, 0, 0, 0));
                    }

                    if (!didToggle) { Toast.makeText(getContext(), "You have already selected 3 spells to play", Toast.LENGTH_SHORT).show(); }
                }
            });
        }

        return convertView;
    }
}
