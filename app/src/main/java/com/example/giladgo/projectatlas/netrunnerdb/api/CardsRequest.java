package com.example.giladgo.projectatlas.netrunnerdb.api;

import android.content.Context;
import android.net.Uri;

import com.example.giladgo.projectatlas.netrunnerdb.json.CardParser;
import com.example.giladgo.projectatlas.netrunnerdb.json.JsonListParser;
import com.example.giladgo.projectatlas.netrunnerdb.models.Card;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by giladgo on 11/21/14.
 */
public class CardsRequest extends NetrunnerRequest<Void, ArrayList<Card>> {

    public CardsRequest(Context context) {
        super(context, null);
    }

    @Override
    protected ArrayList<Card> send(Void aVoid) throws IOException {
        Uri uri = this.getNetrunnerUriBuilder().path("api/cards").build();
        ArrayList<Card> cards = new JsonListParser<>(new CardParser()).parse(this.doRequest(uri));

        return new ArrayList<>(Collections2.filter(cards, new Predicate<Card>() {
            @Override
            public boolean apply(Card card) {
                return card.isReal();
            }
        }));
    }
}
