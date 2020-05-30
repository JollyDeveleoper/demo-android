package com.demo.android;

import android.content.Context;

import com.demo.android.helpers.APIHelper;

public class Application extends android.app.Application {
    private static Context context;

    /**
     * Получаем синглтон для тостов и прочих действий
     * @return
     */
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
        // загружаем данные для api
        APIHelper.load();
    }
}
