package net.grndl.projectatlas.netrunnerdb.models;

import net.grndl.projectatlas.activity.CardListActivity;
import net.grndl.projectatlas.activity.LoaderActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by giladgo on 3/1/15.
 */

@Module(library = true, injects = {CardDB.class, LoaderActivity.class, CardListActivity.class})
public class CardModule {

    public CardModule() {

    }

    @Provides @Singleton CardDB getCardDB() {
        return new CardDB();
    }
}
