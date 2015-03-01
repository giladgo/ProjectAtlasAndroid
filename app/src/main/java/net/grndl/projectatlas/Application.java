package net.grndl.projectatlas;

import com.parse.Parse;

import net.grndl.projectatlas.netrunnerdb.models.CardModule;

import dagger.ObjectGraph;

public class Application extends android.app.Application {


    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "EgQHHegfE3B4Bxa77C0RLY1IraE7mZkcqnYPCM62", "tB23lnzYum8ds8VkQdeuyeA2lI515Rgx5WBsRY8P");

        mObjectGraph = ObjectGraph.create(new CardModule());

    }

    public void inject(Object object) {
        mObjectGraph.inject(object);
    }


}
