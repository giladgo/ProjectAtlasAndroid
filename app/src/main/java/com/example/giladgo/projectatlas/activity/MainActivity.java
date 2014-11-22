package com.example.giladgo.projectatlas.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.giladgo.projectatlas.R;
import com.example.giladgo.projectatlas.http.Callback;
import com.example.giladgo.projectatlas.netrunnerdb.api.CardsRequest;
import com.example.giladgo.projectatlas.netrunnerdb.models.Card;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private ArrayList<Card> mCards;
    private Typeface mNetrunnerFont;

    private class CardsAdapter extends ArrayAdapter<Card> {

        public CardsAdapter(Context context, int resource, List<Card> objects) {
            super(context, resource, objects);
        }

        private String getSubtitle(String faction, String subtype) {
            if (subtype != null && faction != null) {
                return faction + ": " + subtype;
            } else if (faction != null)  {
                return faction;
            }
            return "";
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView;
            if (convertView != null) {
                rowView = convertView;
            } else {
                rowView = inflater.inflate(R.layout.card_list_item, parent, false);
            }

            Card card = this.getItem(position);

            TextView titleView = (TextView) rowView.findViewById(R.id.card_title);
            if (titleView != null) {
                titleView.setText(card.title);
            }

            TextView factionSubtypeView = (TextView) rowView.findViewById(R.id.card_faction_subtype);
            if (factionSubtypeView != null) {
                factionSubtypeView.setText(getSubtitle(card.faction, card.subtype));
            }

            TextView factionLogoView = (TextView) rowView.findViewById(R.id.faction_logo);
            if (factionLogoView != null) {
                factionLogoView.setTypeface(mNetrunnerFont);
                int colorId = getResources().getIdentifier(
                                card.factionCode.replace('-','_'),
                                "color",
                                MainActivity.this.getPackageName()
                        );
                factionLogoView.setTextColor(getResources().getColor(colorId));

                String symbol = card.getSymbol();
                if (symbol != null) {
                    factionLogoView.setText(symbol);
                } else {
                    factionLogoView.setText("");
                }

            }

            return rowView;
        }
    }

    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNetrunnerFont = Typeface.createFromAsset(getAssets(), "netrunner.ttf");

        CardsRequest cardsRequest = new CardsRequest(this);
        cardsRequest.sendAsync(new Callback<ArrayList<Card>>() {
            @Override
            public void error(Exception ex) {
                Log.e("", "Error loading cards", ex);
            }

            @Override
            public void success(ArrayList<Card> cards) {
                MainActivity.this.mCards = cards;
                populateListView();
            }
        });

    }

    private void populateListView() {
        ListView listView = (ListView)this.findViewById(R.id.cardListView);
        listView.setAdapter(new CardsAdapter(this, R.layout.card_list_item, this.mCards));

        final ArrayList<String> cardUrls = new ArrayList<>(Collections2.transform(mCards, new Function<Card, String>() {
            @Override
            public String apply(Card card) {
                return card.imageUrl;
            }
        }));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), CardActivity.class);

                intent.putStringArrayListExtra(CardActivity.CARD_IMAGE_URLS_ARG, cardUrls);
                intent.putExtra(CardActivity.CARD_POSITION_ARG, position);
                startActivity(intent);
            }
        });
    }
}
