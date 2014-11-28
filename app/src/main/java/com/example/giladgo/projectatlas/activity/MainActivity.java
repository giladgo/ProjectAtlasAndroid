package com.example.giladgo.projectatlas.activity;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.giladgo.projectatlas.R;
import com.example.giladgo.projectatlas.netrunnerdb.json.CardParser;
import com.example.giladgo.projectatlas.netrunnerdb.json.JsonListParser;
import com.example.giladgo.projectatlas.netrunnerdb.models.Card;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.gson.JsonArray;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.parse.ConfigCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

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

            TextView influenceView = (TextView)rowView.findViewById(R.id.influence);
            if (influenceView != null) {
                influenceView.setText(new String(new char[card.influence]).replace("\0", "•\n"));
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
                    MainActivity.this.setActionBarTitle(constraint);
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

    private void setActionBarTitle(CharSequence title) {
        if (title == null || title.length() == 0) {
            setTitle("NetConsole");
        } else {
            setTitle(title);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActionBarTitle(null);

        mNetrunnerFont = Typeface.createFromAsset(getAssets(), "netrunner.ttf");

        mListView = (ListView)this.findViewById(R.id.cardListView);
        final ProgressBar mainProgressBar = (ProgressBar)this.findViewById(R.id.main_progress_bar);

        ParseConfig.getInBackground(new ConfigCallback() {
            @Override
            public void done(ParseConfig parseConfig, ParseException e) {
                Ion.with(MainActivity.this).load(parseConfig.getString("card_url"))
                        .progressBar(mainProgressBar)
                        .asJsonArray()
                        .setCallback(new FutureCallback<JsonArray>() {
                            @Override
                            public void onCompleted(Exception ex, JsonArray array) {
                                if (ex != null) {
                                    Log.e("", "Error loading cards", ex);
                                }
                                else {
                                    List<Card> cards = new JsonListParser<>(new CardParser()).parse(array);
                                    MainActivity.this.mCards = new ArrayList<>(Collections2.filter(cards, new Predicate<Card>() {
                                        @Override
                                        public boolean apply(Card card) {
                                            return card.isReal();
                                        }
                                    }));
                                    mainProgressBar.setVisibility(View.INVISIBLE);
                                    populateListView();
                                }
                            }
                        });

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
