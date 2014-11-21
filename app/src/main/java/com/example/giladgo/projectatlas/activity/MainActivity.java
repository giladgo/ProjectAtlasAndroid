package com.example.giladgo.projectatlas.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.giladgo.projectatlas.R;
import com.example.giladgo.projectatlas.http.Callback;
import com.example.giladgo.projectatlas.netrunnerdb.api.CardsRequest;
import com.example.giladgo.projectatlas.netrunnerdb.models.Card;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ArrayList<Card> cards;

    private class CardsAdapter extends ArrayAdapter<Card> {

        public CardsAdapter(Context context, int resource, List<Card> objects) {
            super(context, resource, objects);
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
            TextView textView = (TextView) rowView.findViewById(R.id.card_title);

            textView.setText(this.getItem(position).title);

            return rowView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardsRequest cardsRequest = new CardsRequest(this);

        cardsRequest.sendAsync(new Callback<ArrayList<Card>>() {
            @Override
            public void error(Exception ex) {
                Log.e("", "Error loading cards", ex);
            }

            @Override
            public void success(ArrayList<Card> cards) {
                MainActivity.this.cards = cards;
                populateListView();
            }
        });

    }

    private void populateListView() {
        ListView listView = (ListView)this.findViewById(R.id.cardListView);
        listView.setAdapter(new CardsAdapter(this, R.layout.card_list_item, this.cards));
    }
}
