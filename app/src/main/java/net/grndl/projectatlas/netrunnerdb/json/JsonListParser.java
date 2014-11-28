package net.grndl.projectatlas.netrunnerdb.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by giladgo on 11/24/13.
 */
public class JsonListParser<T> extends JsonParser<ArrayList<T>>  {

    private JsonParser<T> mItemParser;

    public JsonListParser(JsonParser<T> itemParser) {
        mItemParser = itemParser;
    }

    @Override
    public ArrayList<T> parse(JsonElement element) {
        if (element instanceof JsonArray) {
            JsonArray arr = (JsonArray)element;
            ArrayList<T> list = new ArrayList<T>();
            for (JsonElement arrayElement : arr) {
                if (arrayElement instanceof JsonObject) {
                    list.add(mItemParser.parse(arrayElement));
                }
            }
            return list;
        }
        else {
            throw new IllegalArgumentException("Invalid element, should be an array");
        }
    }
}
