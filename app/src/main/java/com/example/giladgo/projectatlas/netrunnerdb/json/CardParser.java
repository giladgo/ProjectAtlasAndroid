package com.example.giladgo.projectatlas.netrunnerdb.json;

import com.example.giladgo.projectatlas.netrunnerdb.models.Card;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by giladgo on 11/21/14.
 */
public class CardParser extends JsonParser<Card> {
    @Override
    public Card parse(JsonElement element) {
        Card card = new Card();
        JsonObject jsonObject = getElementAsJsonObject(element);

        card.code    = jsonObject.get("code").getAsString();
        card.title   = jsonObject.get("title").getAsString();
        card.type    = jsonObject.get("type").getAsString();

        JsonElement subtypeElement = jsonObject.get("subtype");
        if (subtypeElement != null) {
            card.subtype = subtypeElement.getAsString();
        }

        JsonElement factionElement = jsonObject.get("faction");
        if (factionElement != null) {
            card.faction = factionElement.getAsString();
        }

        return card;
    }
}
