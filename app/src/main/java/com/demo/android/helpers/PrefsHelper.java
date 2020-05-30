package com.demo.android.helpers;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.demo.android.Application;

public class PrefsHelper {
    private static SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(Application.getInstance());

    /**
     * Получаем access_token
     * @return String
     */
    public static String getAccessToken() {
        return mPrefs.getString(PreferencesKeys.ACCESS_TOKEN, "");
    }

    /**
     * Получаем роль
     * @return String
     */
    public static String getRole() {
        return mPrefs.getString(PreferencesKeys.ROLE, RoleHelper.ROLE_MODERATOR);
    }

    public static SharedPreferences.Editor setValue() {
        return mPrefs.edit();
    }
}
