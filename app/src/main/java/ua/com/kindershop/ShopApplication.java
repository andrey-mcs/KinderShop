package ua.com.kindershop;

import android.app.Application;

import ua.com.kindershop.application.ShopInstance;

/**
 * Created by andrey on 28.08.15.
 */
public class ShopApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ShopInstance.initInstance();
    }
}
