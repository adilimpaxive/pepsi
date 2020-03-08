package com.pepsi.battleofthebands.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Prefs {
    public static final String KEY_USER_LOGEDIN = "KEY_USER_LOGEDIN";
    public static final String KEY_USER_ID = "KEY_USER_ID";
    public static final String KEY_EMAIL = "KEY_EMAIL";
    public static final String KEY_FIRST_NAME = "KEY_FIRST_NAME";
    public static final String KEY_LAST_NAME = "KEY_LAST_NAME";
    public static final String KEY_PASSWORD = "KEY_PASSWORD";
    public static final String KEY_TOKEN = "KEY_TOKEN";

    public static final String KEY_THEME = "KEY_THEME";
    public static final String KEY_VOTING = "KEY_VOTING";

    public static final String KEY_IS_SONG_REPEAT_STATUS = "KEY_IS_SONG_REPEAT_STATUS";
    public static final String KEY_IS_LIST_ON_SHUFLE = "KEY_IS_LIST_ON_SHUFLE";

    public static final String KEY_QUEUE_POSITION_Y_VALUE = "KEY_QUEUE_POSITION_Y_VALUE";
    // Repeat related constants
    public static final String REPEAT_OFF = "REPEAT_OFF";
    public static final String REPEAT_ALL = "REPEAT_ALL";
    public static final String REPEAT_SONG = "REPEAT_SONG";
    public static final String RECOMMENDATIONS = "RECOMMENDATIONS";
    public static final String IS_OFFLINE_MODE = "IS_OFFLINE_MODE";
    public static final String IS_RECOMMENDATIONS = "IS_RECOMMENDATIONS";
    public static final String TRANSACTION_ID = "TRANSACTION_ID";

    public static final String IS_FIRST_TIME_DOWNLOADING = "IS_FIRST_TIME_DOWNLOADING";
    public static final String ASK_DOWNLOAD_AGAIN = "ASK_DOWNLOAD_AGAIN";
    //    public static final String IS_USER_SUBSCRIBED = "IS_USER_SUBSCRIBED";
    //    public static final String SUBSCRIPTION_EXPIRY_DATE = "SUBSCRIPTION_EXPIRY_DATE";
//    public static final String IS_EXPIRY_VALID = "IS_EXPIRY_VALID";
    public static final String KEY_SUBSCRIPTION_POPUP_SHOW = "KEY_SUBSCRIPTION_POPUP_SHOW";
    public static final String KEY_USER_SAVED_PREFERENCE = "KEY_USER_SAVED_PREFERENCE";
    public static final String KEY_GRACE_PERIOD = "KEY_GRACE_PERIOD";
    public static final String KEY_DEVICE_AUTHORISED = "KEY_DEVICE_AUTHORISED";
    public static final String KEY_SUBSCRIPTION_FREE = "KEY_SUBSCRIPTION_FREE";
    public static final String KEY_PAYING_COUNTRY = "KEY_PAYING_COUNTRY";
    public static final String KEY_CURRENT_COUNTRY = "KEY_CURRENT_COUNTRY";
    public static final String KEY_EXPIRY_DATE = "KEY_EXPIRY_DATE";
    public static final String KEY_EXPIRY_DATE_POPUP = "KEY_EXPIRY_DATE_POPUP";

    //    public static final String KEY_TESTING = "KEY_TESTING";
    public static final String KEY_PREMIUM_RECEIPT = "KEY_PREMIUM_RECEIPT";
    public static final String KEY_AUDIO_ADS = "KEY_AUDIO_ADS";
    public static final String KEY_REPLACE_QUEUE_TABLE = "KEY_REPLACE_QUEUE_TABLE";
    private static SharedPreferences applicationSharedPreference = null;

    public static SharedPreferences getSharedPreferences(Context context) {
        if (applicationSharedPreference == null) {
            applicationSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return applicationSharedPreference;
    }

    public static void saveString(Context context, String key, String value) {
        getSharedPreferences(context).edit().putString(key, value).commit();
    }

    public static String getString(Context context, String key, String defaultValue) {
        return getSharedPreferences(context).getString(key, defaultValue);
    }

    public static void saveInt(Context context, String key, int value) {
        getSharedPreferences(context).edit().putInt(key, value).commit();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        return getSharedPreferences(context).getInt(key, defaultValue);
    }

    public static void saveBoolean(Context context, String key, boolean value) {
        getSharedPreferences(context).edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return getSharedPreferences(context).getBoolean(key, defaultValue);
    }

    public static String getSettingsResponse(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("SettingsResponse", "");
    }

    public static void setSettingsResponse(String response, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("SettingsResponse", response);
        editor.commit();
    }
}
