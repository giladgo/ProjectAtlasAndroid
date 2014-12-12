package net.grndl.projectatlas.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import net.grndl.projectatlas.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CardFragment extends Fragment {
    private static final String ARG_CARD_URL = "card_url";

    private String mCardUrl;

    public static CardFragment newInstance(String cardUrl) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CARD_URL, cardUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public CardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCardUrl = getArguments().getString(ARG_CARD_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        view.findViewById(R.id.card_image);



        ImageView cardImageView = (ImageView)view.findViewById(R.id.card_image);
        final ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.card_progressbar);

        if (cardImageView != null) {
            if (mCardUrl != null && !mCardUrl.isEmpty()) {
                Uri cardImageUrl = Uri.parse(mCardUrl);
                Ion.with(this)
                        .load(cardImageUrl.toString())
                        .progressBar(progressBar)
                        .intoImageView(cardImageView)
                        .setCallback(new FutureCallback<ImageView>() {
                            @Override
                            public void onCompleted(Exception e, ImageView result) {
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });

            } else {
                cardImageView.setImageResource(R.drawable.sad_panda);
                progressBar.setVisibility(View.INVISIBLE);
            }

        }

        return view;
    }


}
