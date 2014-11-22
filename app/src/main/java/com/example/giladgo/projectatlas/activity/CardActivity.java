package com.example.giladgo.projectatlas.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.giladgo.projectatlas.R;
import com.koushikdutta.ion.Ion;

public class CardActivity extends Activity {

    public static final String CARD_URL_KEY = "cardurl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_card);

        final Uri cardImageUrl = Uri.parse("http://netrunnerdb.com" + getIntent().getStringExtra(CARD_URL_KEY));

        ImageView cardImageView = (ImageView)this.findViewById(R.id.card_image);
        if (cardImageView != null) {
            Ion.with(cardImageView).load(cardImageUrl.toString());
        }
    }

}
