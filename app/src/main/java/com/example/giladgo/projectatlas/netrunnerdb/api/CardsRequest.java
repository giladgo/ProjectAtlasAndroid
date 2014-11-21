package com.example.giladgo.projectatlas.netrunnerdb.api;

import android.content.Context;
import android.net.Uri;

import com.example.giladgo.projectatlas.netrunnerdb.json.CardParser;
import com.example.giladgo.projectatlas.netrunnerdb.json.JsonListParser;
import com.example.giladgo.projectatlas.netrunnerdb.models.Card;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by giladgo on 11/21/14.
 */
public class CardsRequest extends NetrunnerRequest<Void, ArrayList<Card>> {

    public CardsRequest(Context context, Void aVoid) {
        super(context, aVoid);
    }

    @Override
    protected ArrayList<Card> send(Void aVoid) throws IOException {
        Uri uri = this.getNetrunnerUriBuilder().path("api/cards").build();
        return new JsonListParser<>(new CardParser()).parse(this.doRequest(uri));
    }
}
