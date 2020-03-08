package com.pepsi.battleofthebands.utils;

import android.util.Log;

public class PLog {
    private static final boolean IS_DEBUGABLE = true;
    public static String TAG = "Patari";

    public static void showLog(String message) {
        if (IS_DEBUGABLE) {
            try {
                Log.d(TAG, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void showLog(String tag, String message) {
        if (IS_DEBUGABLE) {
            try {
                Log.d(tag, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
