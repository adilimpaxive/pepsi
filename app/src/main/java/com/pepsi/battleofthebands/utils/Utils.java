package com.pepsi.battleofthebands.utils;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.pepsi.battleofthebands.db.DatabaseHelper;
import com.pepsi.battleofthebands.db.QueueTableDataManager;
import com.pepsi.battleofthebands.entity.Gallery;
import com.pepsi.battleofthebands.entity.Song;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class Utils {
    // abdull mannan
    public static final String GOOGLE_ANALYTICS_TRACKER_ID = "UA-123785658-1";

    public static final String KEY_SERVER_NO_RESPONSE = "NO_RESPONSE";

    /*
     * Intent Extras
     */
    public static final String EXTRA_NOTIFICATION_KEY = "EXTRA_NOTIFICATION_KEY";

    public static final String EXTRA_NOTIFICATION_CONTROL_PAUSE_SONG = "EXTRA_NOTIFICATION_CONTROL_PAUSE_SONG";
    public static final String EXTRA_NOTIFICATION_CONTROL_PLAY_SONG = "EXTRA_NOTIFICATION_CONTROL_PLAY_SONG";
    public static final String EXTRA_NOTIFICATION_CONTROL_REWIND_SONG = "EXTRA_NOTIFICATION_CONTROL_REWIND_SONG";
    public static final String EXTRA_NOTIFICATION_CONTROL_FORWARD_SONG = "EXTRA_NOTIFICATION_CONTROL_FORWARD_SONG";
    public static final String EXTRA_NOTIFICATION_CONTROL_CLOSE_PLAYER_AND_SONG = "EXTRA_NOTIFICATION_CONTROL_STOP_SONG";

    /**
     * BroadCast action constants
     */
//    public static final String BR_ACTION_REFRESH_RECOMMENDATION_BUTTON_STATE = "BR_ACTION_REFRESH_RECOMMENDATION_BUTTON_STATE";
    public static final String BR_ACTION_SHUFFLE_ON = "BR_ACTION_SHUFFLE_ON";
    public static final String BR_ACTION_SHUFFLE_OFF = "BR_ACTION_SHUFFLE_OFF";

    public static final String BR_RESET_NOTIFICATION_ICONS = "BR_RESET_NOTIFICATION_ICONS";

    public static final String BR_ACTION_PLAY_SONG = "PLAY_SONG_PATARI";

    public static final String BR_ACTION_START_PLAYING_SELECTED_SONG = "BR_ACTION_START_PLAYING_SELECTED_SONG_PATARI";

    public static final String BR_ACTION_UPDATE_SONG_PLAYERS_SONG_CHANGED = "BR_ACTION_UPDATE_SONG_PLAYERS_SONG_CHANGED";

    public static final String BR_ACTION_UPDATE_SONG_PLAYERS_SONG_STOPPED = "BR_ACTION_UPDATE_SONG_PLAYERS_SONG_STOPPED";

    public static final String BR_ACTION_UPDATE_SONG_PLAYERS_SONG_RESUMED = "BR_ACTION_UPDATE_SONG_PLAYERS_SONG_RESUMED";

    public static final String BR_ACTION_SHOW_MINI_PLAYER = "BR_ACTION_SHOW_MINI_PLAYER_PATARI";

    public static final String BR_ACTION_RESET_PLAYER_LAYOUT = "BR_ACTION_RESET_MINI_PLAYER_LAYOUT_PATARI";

    public static final String BR_ACTION_UPDATE_NAVDRAWER_SONGS_LIST = "BR_ACTION_UPDATE_NAVDRAWER_SONGS_LIST";

    public static final String BR_ACTION_PLAY_ALL_ALBUM = "BR_ACTION_PLAY_ALL_ALBUM_PATARI";

    public static final String BR_ACTION_START_PLAYING_ALL_ALBUM_SONG = "BR_ACTION_START_PLAYING_ALL_ALBUM_SONG_PATARI";

    public static final String BR_ACTION_START_PLAYING_ALREADY_ADDED = "BR_ACTION_START_PLAYING_ALREADY_ADDED_PATARI";

    public static final String BR_ACTION_DB_DATA_SET_CHANGED = "BR_ACTION_DB_DATA_SET_CHANGED_PATARI";

    public static final String BR_ACTION_NEW_SONG_PLAYED = "BR_ACTION_NEW_SONG_PLAYED_PATARI";

    // For Recommendations and Home Screen Playlist/Album Broadcast.
    public static final String BR_ACTION_UPDATE_SONGS_LIST_UI_AND_SET_SPECIFIC_SONG_AS_SELECTED = "BR_ACTION_UPDATE_SONGS_LIST_UI_AND_SET_SPECIFIC_SONG_AS_SELECTED";
    // For Home Screen Playlist/Album Broadcast
    public static final String BR_ACTION_START_PLAYING_QUEUE_AT_SPECIFIC_POSITION = "BR_ACTION_START_PLAYING_QUEUE_AT_SPECIFIC_POSITION";

    // For Home Screen Playlist/Album Broadcast
    public static final String INDEX_OF_SELECTED_SONG_TO_PLAY = "INDEX_OF_SELECTED_SONG_TO_PLAY";


    public static int getDpiFromPixel(Context context, int pixel) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel, context.getResources().getDisplayMetrics());
    }


    public static boolean isOnline(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = null;
            if (cm != null) {
                activeNetwork = cm.getActiveNetworkInfo();
            }
            return activeNetwork != null && (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE);
        }
        return false;
    }

    public static String getNetworkOperatorName(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager != null) {
            return manager.getNetworkOperatorName();
        }
        return "";
    }

    public static String getDeviceId(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return manager.getDeviceId();
            } else
                return manager.getDeviceId();
        }
        return "12345";
    }

    public static String getMD5FromString(String string) {
        StringBuilder sb = new StringBuilder();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes());
            byte[] digest = md.digest();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String password = sb.toString();
        PLog.showLog("md5 hash : " + password);
        return password;
    }

    public static void saveUserInfoInPreference(Context context, String userID, String firstName, String lastName, String email, String password) {
        Prefs.saveString(context, Prefs.KEY_USER_ID, userID);
        Prefs.saveString(context, Prefs.KEY_FIRST_NAME, firstName);
        Prefs.saveString(context, Prefs.KEY_LAST_NAME, lastName);
        Prefs.saveString(context, Prefs.KEY_EMAIL, email);
        Prefs.saveString(context, Prefs.KEY_PASSWORD, password);
    }

    public static void removeUserInfoFromPreferenceAndLogout(Context context) {
        Prefs.saveBoolean(context, Prefs.KEY_USER_LOGEDIN, false);
//        Prefs.saveString(context, Prefs.KEY_USER_ID, "");
//        Prefs.saveString(context, Prefs.KEY_FIRST_NAME, "");
//        Prefs.saveString(context, Prefs.KEY_LAST_NAME, "");
//        Prefs.saveString(context, Prefs.KEY_EMAIL, "");
        removeAllSavedSongs(context);
        // open login
        AccessToken.setCurrentAccessToken(null);
        Profile.setCurrentProfile(null);
        LoginManager.getInstance().logOut();
    }

    public static void removeAllSavedSongs(Context context) {
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        db.delete(QueueTableDataManager.SONGS_QUEUE_TABLE, null, null);
        File dir = new File(context.getExternalFilesDir("kashan") + "");
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                new File(dir, aChildren).delete();
            }
        }
    }

    public static int getWindowHeight(Context context) {
        if (context == null) {
            return 0;
        }
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        return display.heightPixels;
    }

    public static int getWindowWidth(Context context) {
        if (context == null) {
            return 0;
        }
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        return display.widthPixels;
    }


    public static void hideKeyBoard(EditText editTextField, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) (context).getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editTextField.getWindowToken(), 0);
    }

    public static void showKeyBoard(EditText editTextField, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) (context).getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editTextField, 0);
    }

    public static void downloadAndShowCircularImageInImageView(Context context, final ImageView imageView, final int imageWidth, int defaultImage, final String imageUrl, final boolean isBoarder) {
        if (context != null && imageView != null && imageUrl != null) {
            Bitmap defaultBitmap = null;
            try {
                defaultBitmap = BitmapFactory.decodeResource(context.getResources(), defaultImage);
            } catch (Exception e) {
                e.printStackTrace();
            }

            final Bitmap defaultCircularBitmap = Utils.getCircularBitmap(defaultBitmap, imageWidth, isBoarder);
            Target target = new Target() {

                @Override
                public void onPrepareLoad(Drawable arg0) {
                    imageView.setImageBitmap(defaultCircularBitmap);

                }

                @Override
                public void onBitmapLoaded(Bitmap songImagebitmap, LoadedFrom arg1) {
                    Bitmap bmp = Utils.getCircularBitmap(songImagebitmap, imageWidth, isBoarder);
                    imageView.setImageBitmap(bmp);
                }

                @Override
                public void onBitmapFailed(Drawable arg0) {
                    imageView.setImageBitmap(defaultCircularBitmap);
                }
            };
            Picasso.with(context).load(imageUrl).into(target);
            imageView.setTag(target);
        }
    }

    /**
     * Get scaled and circular image
     *
     * @param bitmap bitmap
     * @param scale  scale
     * @return bitmap
     * @throws OutOfMemoryError exception
     */
    private static Bitmap getCircularBitmap(Bitmap bitmap, int scale, boolean isBoarder) throws OutOfMemoryError {
        if (bitmap != null) {
            try {
                bitmap = getSquareImage(bitmap);
                Bitmap output = Bitmap.createBitmap(scale, scale, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(output);

                final int color = 0xff424242;
                final Paint paint = new Paint();
                final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                final Rect rectD = new Rect(0, 0, scale, scale);

                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                paint.setColor(color);
                canvas.drawCircle(scale / 2, scale / 2, scale / 2, paint);

                paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
                canvas.drawBitmap(bitmap, rect, rectD, paint);
                if (isBoarder) {
                    paint.setStyle(Style.STROKE);
                    paint.setStrokeWidth(4);
                    paint.setARGB(-46, 26, 188, 156);
                    canvas.drawCircle(scale / 2, scale / 2, scale / 2, paint);
                }
                return output;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static Bitmap getSquareImage(Bitmap srcBmp) {
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()) {
            dstBmp = Bitmap.createBitmap(srcBmp, srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2, 0, srcBmp.getHeight(), srcBmp.getHeight());
        } else {
            dstBmp = Bitmap.createBitmap(srcBmp, 0, srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2, srcBmp.getWidth(), srcBmp.getWidth());
        }
        return dstBmp;
    }

    /**
     * Draw circular image with a black boundary
     *
     * @param bitmap bitmap
     * @return Circled Bitmap
     */
    public static Bitmap getBlackBoundaryCircleBitmap(Bitmap bitmap) {

        bitmap = getSquareImage(bitmap);
        int scale = bitmap.getWidth();
        Bitmap output = Bitmap.createBitmap(scale, scale, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff000000;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final Rect rectD = new Rect(0, 0, scale, scale);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(scale / 2, scale / 2, scale / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rectD, paint);

        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setARGB(255, 0, 0, 0);
        canvas.drawCircle(scale / 2, scale / 2, scale / 2, paint);

        return output;
    }

    /**
     * This method deletes the wole Queue and Adds a new List to it and Plays
     * the 1st song in the Queue
     *
     * @param context   context
     * @param songsList songs
     */
    public static void playSongListAndAddToQueue(final Context context, final ArrayList<Song> songsList, final int position) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                QueueTableDataManager dataDoa = QueueTableDataManager.getInstance();
                dataDoa.deleteSongsQueueTableData(QueueTableDataManager.SONGS_QUEUE_TABLE);
                for (int i = 0; i < songsList.size(); i++) {
                    int result = dataDoa.insertSongInToQueue(songsList.get(i), QueueTableDataManager.SONGS_QUEUE_TABLE);
                    if (result != -1) {
                    }
                }
                context.sendBroadcast(new Intent(Utils.BR_ACTION_UPDATE_SONGS_LIST_UI_AND_SET_SPECIFIC_SONG_AS_SELECTED).putExtra(Utils.INDEX_OF_SELECTED_SONG_TO_PLAY, position));
            }
        });
        t.start();
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int color, int cornerDips, int borderDips, Context context) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int borderSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) borderDips,
                context.getResources().getDisplayMetrics());
        final int cornerSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) cornerDips,
                context.getResources().getDisplayMetrics());
        final Paint paint = new Paint();
        final Rect rect;
        rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        // prepare canvas for transfer
        paint.setAntiAlias(true);
        paint.setColor(0xFFFFFFFF);
        paint.setStyle(Style.FILL);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

        // draw bitmap
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        // draw border
        paint.setColor(color);
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth((float) borderSizePx);
        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

        return output;
    }

    private final static int HOUR = 60 * 60 * 1000;
    private final static int MINUTE = 60 * 1000;
    private final static int SECOND = 1000;

    public static String getTotalSeekTime(int duration) {

        int durationHour = duration / HOUR;
        int durationMint = (duration % HOUR) / MINUTE;
        int durationSec = (duration % MINUTE) / SECOND;

        if (durationHour > 0) {
            return String.format("%02d:%02d:%02d", durationHour, durationMint, durationSec);
        } else {
            return String.format("%02d:%02d", durationMint, durationSec);
        }
    }

    public static String getCurrentSeekTime(int duration) {

        int currentHour = duration / HOUR;
        int currentMint = (duration % HOUR) / MINUTE;
        int currentSec = (duration % MINUTE) / SECOND;

        if (currentHour > 0) {
            return String.format("%02d:%02d:%02d", currentHour, currentMint, currentSec);
        } else if (currentMint < 0 || currentSec < 0) {
            return "00:00";
        } else {
            return String.format("%02d:%02d", currentMint, currentSec);
        }
    }

    public static String getVideoName(String urlTVS) {
        String videoName = "WvDSsFo469Q";
        if (urlTVS.contains("=")) {
            videoName = urlTVS.split("=")[1];
        }
        return videoName;
    }

    public static ArrayList<Song> selectedPerformance;

    public static void setSelectedBand(ArrayList<Song> songs) {
        selectedPerformance = songs;
    }

    public static ArrayList<Gallery> galleries;

    public static void setSelectedGallery(ArrayList<Gallery> gallery) {
        galleries = gallery;
    }

    public static String getFirstLetterCapital(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
