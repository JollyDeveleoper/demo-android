package com.demo.android.helpers;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.demo.android.activity.LoginActivity;

public class APIHelper {
    public static final String BASE_DOMAIN= "https://demo.maffinca.design/api";
    public static String TOKEN = "";

    public static void expiredToken(Context context) {
        PrefsHelper.setValue().putString(PreferencesKeys.ACCESS_TOKEN, "").apply();
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    public static void load() {
        if (TextUtils.isEmpty(TOKEN)) {
            TOKEN = PrefsHelper.getAccessToken();
        }
    }
}
