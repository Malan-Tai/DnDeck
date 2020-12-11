package com.example.dndeck_a6.game;

import android.content.Context;

import com.example.dndeck_a6.CardImageAdapter;

public class SpellCast {
    public GameCharacter caster;
    public Spell spell;
    public Card card;
    private CardImageAdapter adapter;

    public SpellCast(GameCharacter caster, Spell spell, Card card, CardImageAdapter adapter){
        this.caster = caster;
        this.spell = spell;
        this.card = card;
        this.adapter = adapter;
    }

    public void cast(Context context){
        spell.cast(context, card.value);
        adapter.remove(card);
    }
}
