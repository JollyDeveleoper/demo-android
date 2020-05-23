package com.demo.android;

import android.content.Context;

public class Application extends android.app.Application {
    private static Context context;

    public static Context getInstance() {
        if (context == null) {
            throw new NullPointerException("Context cannot be null");
        }
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
