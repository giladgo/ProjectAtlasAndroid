package net.grndl.projectatlas.netrunnerdb.models;

import android.content.Context;

import net.grndl.projectatlas.netrunnerdb.json.CardParser;
import net.grndl.projectatlas.netrunnerdb.json.JsonListParser;
import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.parse.ConfigCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;

import org.jdeferred.Deferred;
import org.jdeferred.DonePipe;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by giladgo on 11/28/14.
 */
public class CardDB {

    private static CardDB mInstance = null;

    private Map<String, Card> mCards;

    private CardDB() {

    }

    public static CardDB getInstance() {
        if (mInstance == null) mInstance = new CardDB();
        return mInstance;
    }


    private Promise<List<Card>, Exception, Integer> loadCardsInternal(final Context context, ParseConfig config) {

        final Deferred<List<Card>, Exception, Integer> deferred = new DeferredObject<>();
        Promise<List<Card>, Exception, Integer> promise = deferred.promise();

        Ion.with(context).load(config.getString("card_url")).progress(new ProgressCallback() {
            @Override
            public void onProgress(long downloaded, long total) {
                deferred.notify((int) (100.0 * (float) downloaded / (float) total));
            }
        })
        .asJsonArray()
        .setCallback(new FutureCallback<JsonArray>() {
            @Override
            public void onCompleted(Exception ex, JsonArray array) {
                if (ex != null) {
                    deferred.reject(ex);
                } else {
                    List<Card> cards = new JsonListParser<>(new CardParser()).parse(array);
                    deferred.resolve(cards);
                }
            }
        });

        return promise;

    }

    private Promise<ParseConfig, ParseException, Void> loadParseConfig() {

        final Deferred<ParseConfig, ParseException, Void> deferred = new DeferredObject<>();

        ParseConfig.getInBackground(new ConfigCallback() {
            @Override
            public void done(ParseConfig parseConfig, ParseException e) {
                if (e != null) {
                    deferred.reject(e);
                } else {
                    deferred.resolve(parseConfig);
                }
            }
        });
        return deferred.promise();
    }

    private void parseRecommmendations(JsonArray jsonArray) {
        for (JsonElement element : jsonArray) {

            if (element instanceof JsonObject) {
                JsonObject obj = (JsonObject)element;

                Card card = mCards.get(obj.get("id").getAsString());
                Iterator<String> recommendedCodes = Iterators.transform(obj.get("recommendations").getAsJsonArray().iterator(),
                        new Function<JsonElement, String>() {
                            @Override
                            public String apply(JsonElement input) {
                                return input.getAsString();
                            }
                        });
                if (card != null) {
                    card.recommendations = Lists.newArrayList(Iterators.transform(recommendedCodes, new Function<String, Card>() {
                        @Override
                        public Card apply(String code) {
                            return mCards.get(code);
                        }
                    }));
                }
            }
        }
    }

    public Promise<List<Card>, Exception, Integer> load(final Context context) {

        return loadParseConfig().then(new DonePipe<ParseConfig, List<Card>, Exception, Integer>() {
            @Override
            public Promise<List<Card>, Exception, Integer> pipeDone(ParseConfig parseConfig) {
                return loadCardsInternal(context, parseConfig);
            }
        }).then(new DonePipe<List<Card>, JsonArray, Exception, Void>() {
            @Override
            public Promise<JsonArray, Exception, Void> pipeDone(List<Card> cards) {
            mCards = new LinkedHashMap<>(cards.size());

            for (Card card : cards) {
                mCards.put(card.code, card);
            }

            return loadRecommendationsInternal(context);
            }
        }).then(new DonePipe<JsonArray, List<Card>, Exception, Integer>() {
            @Override
            public Promise<List<Card>, Exception, Integer> pipeDone(JsonArray jsonArray) {
                parseRecommmendations(jsonArray);
                return new DeferredObject<List<Card>, Exception, Integer>().resolve(new ArrayList<>(mCards.values()));
            }
        });
    }

    private Promise<JsonArray, Exception, Void> loadRecommendationsInternal(final Context context) {
        final Deferred<JsonArray, Exception, Void> deferred = new DeferredObject<>();

        Ion.with(context).load("http://indexing.grndl.net/javascripts/cards.json").asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if (e != null)
                            deferred.reject(e);
                        else
                            deferred.resolve(result);
                    }
                });

        return deferred.promise();
    }

    public List<Card> allCards() {
        return new ArrayList<>(mCards.values());
    }

    public List<Card> recommendedCards(String code) {
        List<Card> recommendedCards = mCards.get(code).recommendations;
        if (recommendedCards != null) {
            return Lists.newCopyOnWriteArrayList(recommendedCards);
        } else {
            return Lists.newArrayList();
        }

    }

    public Card getCard(String code) {
        return mCards.get(code);
    }

}
