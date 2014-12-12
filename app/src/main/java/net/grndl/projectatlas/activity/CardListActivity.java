package net.grndl.projectatlas.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import net.grndl.projectatlas.R;
import net.grndl.projectatlas.netrunnerdb.models.Card;
import net.grndl.projectatlas.netrunnerdb.models.CardDB;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import org.jdeferred.DoneCallback;
import org.jdeferred.ProgressCallback;

import java.util.ArrayList;
import java.util.List;

public class CardListActivity extends Activity {

    private ArrayList<Card> mCards;

    private Typeface mNetrunnerFont;
    private ListView mListView;
    private CardsAdapter mAdapter;
    private Card mRecommendationsForCard;

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
                                CardListActivity.this.getPackageName()
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
                    CardListActivity.this.setActionBarTitle(constraint);
                    notifyDataSetChanged();
                }
            };
        }
    }

    public CardListActivity() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.search);
        if (searchMenuItem != null) {
            if (mRecommendationsForCard != null) {
                searchMenuItem.setVisible(false);
            } else {
                final SearchView searchView = (SearchView) searchMenuItem.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        mListView.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(
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

        mNetrunnerFont = Typeface.createFromAsset(getAssets(), "netrunner.ttf");

        mListView = (ListView)this.findViewById(R.id.cardListView);


        if (getIntent().hasExtra("recommendationsFor")) {
            mRecommendationsForCard = getIntent().getParcelableExtra("recommendationsFor");
            setActionBarTitle("Recommendations For " + mRecommendationsForCard.title);
            mCards = Lists.newArrayList(CardDB.getInstance().recommendedCards(mRecommendationsForCard.code));
        } else {
            mRecommendationsForCard = null;
            setActionBarTitle(null);
            mCards = new ArrayList<>(Collections2.filter(CardDB.getInstance().allCards(), new Predicate<Card>() {
                @Override
                public boolean apply(Card card) {
                    return card.isReal();
                }
            }));
        }
        populateListView();
    }

    private void populateListView() {
        mAdapter = new CardsAdapter(this, R.layout.card_list_item, this.mCards);
        mListView.setAdapter(mAdapter);

        registerForContextMenu(mListView);
        invalidateOptionsMenu();

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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapterMenuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        Card selectedCard = mCards.get(adapterMenuInfo.position);
        switch(item.getItemId()) {
            case R.id.recommendation_menu_item:
                Intent intent = new Intent(this, CardListActivity.class);
                intent.putExtra("recommendationsFor", selectedCard);
                startActivity(intent);
                return true;
            case R.id.nrdb_menu_item:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, selectedCard.url);
                startActivity(browserIntent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.card_menu, menu);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("cards", mCards);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        mCards = savedInstanceState.getParcelableArrayList("cards");
    }
}
