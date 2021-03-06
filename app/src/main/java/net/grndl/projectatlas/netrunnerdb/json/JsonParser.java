package net.grndl.projectatlas.netrunnerdb.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Created by giladgo on 11/24/13.
 */
public abstract class JsonParser<T> {

    protected JsonObject getElementAsJsonObject(JsonElement element) {
        if (element instanceof JsonObject) {
            return (JsonObject)element;
        }
        throw new IllegalArgumentException("Expected element to be a JSON object");
    }

    protected String getJsonString(JsonObject jsonObject, String fieldName) {
        if (jsonObject.has(fieldName)) {
            JsonElement e = jsonObject.get(fieldName);
            if (e instanceof JsonPrimitive) {
                return e.getAsString();
            }
        }

        return null;
    }

    protected int getJsonInt(JsonObject object, String fieldName) {
        if (object.has(fieldName)) {
            JsonElement e = object.get(fieldName);
            if (e instanceof JsonPrimitive)
                return e.getAsInt();
        }

        return 0;
    }

    protected JsonParser() {
    }

    public abstract T parse(JsonElement element);
}
