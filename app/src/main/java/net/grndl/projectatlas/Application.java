package net.grndl.projectatlas;

import com.parse.Parse;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "EgQHHegfE3B4Bxa77C0RLY1IraE7mZkcqnYPCM62", "tB23lnzYum8ds8VkQdeuyeA2lI515Rgx5WBsRY8P");
    }
}
