package com.pepsi.battleofthebands.app;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pepsi.battleofthebands.utils.Utils;

import java.io.File;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class PepsiApplication extends MultiDexApplication {

    public static String SONGS_CACHE_DIR_PATH;
    public static String IMAGES_CACHE_DIR_PATH;
    static Context context;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    public static Context getAppContext() {
        return context;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        try {
            Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String token = FirebaseInstanceId.getInstance().getToken();
            if (token != null)
                Log.d("TOKEN", token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            FacebookSdk.sdkInitialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SONGS_CACHE_DIR_PATH = getFilesDir() + File.separator + "Cache" + File.separator + "Music" + File.separator;
        IMAGES_CACHE_DIR_PATH = getFilesDir() + File.separator + "Cache" + File.separator + "Images" + File.separator;
        File dir = new File(SONGS_CACHE_DIR_PATH);
        dir.mkdirs();

        AQUtility.setCacheDir(new File(IMAGES_CACHE_DIR_PATH));
        AjaxCallback.setNetworkLimit(2);

        BitmapAjaxCallback.setIconCacheLimit(200);
        BitmapAjaxCallback.setCacheLimit(80);
        BitmapAjaxCallback.setPixelLimit(400 * 400);
        BitmapAjaxCallback.setMaxPixelLimit(2000000);

        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);
        tracker = analytics.newTracker(Utils.GOOGLE_ANALYTICS_TRACKER_ID);
        // Replace with actual tracker/property Id
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
    }

    @Override
    public void onLowMemory() {
        BitmapAjaxCallback.clearCache();
    }

    private OkHttpClient okHttpClient;

    private final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    public okhttp3.OkHttpClient getOkHttpClient() {

        if (okHttpClient == null) {
            /*
             * in case of custom Timeout and logging
             * use below lines
             */
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

            OkHttpClient.Builder timeOutBuilder = new OkHttpClient.Builder();

            int READ_TIME = 30;
            timeOutBuilder.readTimeout(READ_TIME, TIME_UNIT);
            int WRITE_TIME = 60;
            timeOutBuilder.writeTimeout(WRITE_TIME, TIME_UNIT);
            int CONNECT_TIME = 30;
            timeOutBuilder.connectTimeout(CONNECT_TIME, TIME_UNIT);
            timeOutBuilder.cookieJar(new JavaNetCookieJar(cookieManager));

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            timeOutBuilder.interceptors().add(logging);

            okHttpClient = timeOutBuilder.build();

            /* ................................................... */

            /*
             * if you do not want want logging and timeout use
             * then initialize okHttpclient with new OKHttpClient
             * default timeout is 10000 milliseconds
             * no default logging
             */

            // okHttpClient = new OkHttpClient();

            /* ................................................... */
        }
        return okHttpClient;
    }
}
