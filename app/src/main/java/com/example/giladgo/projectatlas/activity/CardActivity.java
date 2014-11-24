package com.example.giladgo.projectatlas.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;

import com.example.giladgo.projectatlas.R;

import java.util.ArrayList;

public class CardActivity extends FragmentActivity {

    public static final String CARD_IMAGE_URLS_ARG = "card_imgs";
    public static final String CARD_POSITION_ARG = "card_pos";

    private ViewPager mPager;

    private PagerAdapter mPagerAdapter;

    private ArrayList<String> mCardImageUrls;

    public static int convertDip2Pixels(Context context, int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        mCardImageUrls = getIntent().getStringArrayListExtra(CARD_IMAGE_URLS_ARG);

        mPager = (ViewPager)findViewById(R.id.card_pager);
        mPager.setPageMargin(convertDip2Pixels(this, 30));
        mPagerAdapter = new CardPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        int cardPos = getIntent().getIntExtra(CARD_POSITION_ARG, 0);
        mPager.setCurrentItem(cardPos);
    }

    private class CardPagerAdapter extends FragmentStatePagerAdapter {

        public CardPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return CardFragment.newInstance(mCardImageUrls.get(position));
        }

        @Override
        public int getCount() {
            return mCardImageUrls.size();
        }
    }

}
