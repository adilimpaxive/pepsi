package com.pepsi.battleofthebands.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.utils.PLog;
import com.pepsi.battleofthebands.utils.Prefs;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //make status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
//            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_splash);
        ImageView imageViewSplashBackground = findViewById(R.id.imageViewSplashBackground);
        ImageView imageViewSplash = findViewById(R.id.imageViewSplash);
        AnimationDrawable downloadingAnimation = (AnimationDrawable) imageViewSplash.getDrawable();
        downloadingAnimation.start();
        TextView textViewPoweredBy = findViewById(R.id.textViewPoweredBy);
        TextView textViewPepsi = findViewById(R.id.textViewPepsi);
//        Prefs.saveInt(this, Prefs.KEY_THEME, 1);
        if (Prefs.getInt(this, Prefs.KEY_THEME, 0) == 0) {
            imageViewSplashBackground.setImageResource(R.mipmap.splash_background);
            textViewPoweredBy.setTextColor(ContextCompat.getColor(this, R.color.white));
            textViewPepsi.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
        Handler mHandler = new Handler();
        mHandler.postDelayed(rb_updateUi, 1500);
        generateHashKeyforFaceBook();
    }

    private void generateHashKeyforFaceBook() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String s = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                PLog.showLog("KEY HASH: " + s);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private Runnable rb_updateUi = new Runnable() {

        @Override
        public void run() {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    };
}
