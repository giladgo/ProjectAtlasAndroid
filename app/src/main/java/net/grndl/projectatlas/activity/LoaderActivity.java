package net.grndl.projectatlas.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import net.grndl.projectatlas.R;
import net.grndl.projectatlas.netrunnerdb.models.Card;
import net.grndl.projectatlas.netrunnerdb.models.CardDB;

import org.jdeferred.DoneCallback;
import org.jdeferred.ProgressCallback;

import java.util.List;

public class LoaderActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

//        final ProgressBar mainProgressBar = (ProgressBar)this.findViewById(R.id.main_progress_bar);
//        mainProgressBar.setMax(100);

        CardDB.getInstance().load(this).progress(new ProgressCallback<Integer>() {
            @Override
            public void onProgress(Integer progress) {
//                mainProgressBar.setProgress(progress);
            }
        }).then(new DoneCallback<List<Card>>() {
            @Override
            public void onDone(List<Card> cards) {
                Intent intent = new Intent(LoaderActivity.this, CardListActivity.class);
                startActivity(intent);
            }
        });
    }

}
