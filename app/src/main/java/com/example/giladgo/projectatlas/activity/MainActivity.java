package com.example.giladgo.projectatlas.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.giladgo.projectatlas.R;
import com.example.giladgo.projectatlas.http.Callback;
import com.example.giladgo.projectatlas.netrunnerdb.api.CardsRequest;
import com.example.giladgo.projectatlas.netrunnerdb.models.Card;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private ArrayList<Card> mCards;

    private Typeface mNetrunnerFont;
    private ListView mListView;
    private CardsAdapter mAdapter;

    private class CardsAdapter extends BaseAdapter implements Filterable {

        private List<Card> mShowingCards;

        public CardsAdapter(Context context, int resource, List<Card> objects) {
            mShowingCards = new ArrayList<>(objects);
        }

        private String getSubtitle(String faction, String subtype) {
            if (subtype != null && faction != null) {
                return faction + ": " + subtype;
            } else if (faction != null)  {
                return faction;
            }
            return "";
        }

        public List<Card> getShowingCards() {
            return mShowingCards;
        }


        @Override
        public int getCount() {
            return mShowingCards.size();
        }

        @Override
        public Card getItem(int position) {
            return mShowingCards.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
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

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(final CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    List<Card> filteredCards;
                    if (TextUtils.isEmpty(constraint)) {
                        filteredCards = new ArrayList<>(mCards);
                    }
                    else {
                        filteredCards = new ArrayList<>(Collections2.filter(mCards, new Predicate<Card>() {
                            @Override
                            public boolean apply(Card card) {
                                return card.title.toLowerCase().contains(constraint.toString().toLowerCase());
                            }
                        }));
                    }

                    filterResults.count = filteredCards.size();
                    filterResults.values = filteredCards;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    mShowingCards = (List<Card>)results.values;
                    MainActivity.this.setTitle(constraint);
                    notifyDataSetChanged();
                }
            };
        }
    }

    public MainActivity() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    mListView.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    mAdapter.getFilter().filter(newText);
                    return true;
                }
            });
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Project Atlas");

        mNetrunnerFont = Typeface.createFromAsset(getAssets(), "netrunner.ttf");

        mListView = (ListView)this.findViewById(R.id.cardListView);

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

        mAdapter = new CardsAdapter(this, R.layout.card_list_item, this.mCards);
        mListView.setAdapter(mAdapter);



        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final ArrayList<String> cardUrls = new ArrayList<>(Collections2.transform(mAdapter.getShowingCards(), new Function<Card, String>() {
                    @Override
                    public String apply(Card card) {
                        return card.imageUrl;
                    }
                }));

                Intent intent = new Intent(getApplicationContext(), CardActivity.class);

                intent.putStringArrayListExtra(CardActivity.CARD_IMAGE_URLS_ARG, cardUrls);
                intent.putExtra(CardActivity.CARD_POSITION_ARG, position);
                startActivity(intent);
            }
        });
    }
}
