package net.grndl.projectatlas.netrunnerdb.json;

import android.net.Uri;

import net.grndl.projectatlas.netrunnerdb.models.Card;
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

        card.subtype     = getJsonString(jsonObject, "subtype");
        card.faction     = getJsonString(jsonObject, "faction");
        card.factionCode = getJsonString(jsonObject, "faction_code");
        card.setCode     = getJsonString(jsonObject, "set_code");
        card.sideCode    = getJsonString(jsonObject, "side_code");
        card.influence   = getJsonInt(jsonObject, "factioncost");

        String imageSrc = getJsonString(jsonObject, "imagesrc");
        if (imageSrc != null && imageSrc.length() > 0)
            card.imageUrl    = "http://netrunnerdb.com" + imageSrc;
        else
            card.imageUrl    = "";

        card.url = Uri.parse(getJsonString(jsonObject, "url"));

        return card;
    }
}
