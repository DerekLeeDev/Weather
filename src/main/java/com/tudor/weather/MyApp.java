package com.tudor.weather;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePalApplication;

/**
 * Created by LiDongduo on 2017/4/24.
 */

public class MyApp extends Application {
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        LitePalApplication.initialize(sContext);
    }

    public static Context getContext() {
        return sContext;
    }
}
