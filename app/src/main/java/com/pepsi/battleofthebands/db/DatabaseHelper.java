package com.pepsi.battleofthebands.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pepsi.battleofthebands.app.PepsiApplication;


/**
 * Created by Muhammad Kashan on 3/9/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pepsi.sqlite";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseHelper instance;
    private final static Integer lock = 0;


    private final String CREATE_SONGS_QUEUE = "create table if not exists " + QueueTableDataManager.SONGS_QUEUE_TABLE + "(" + QueueTableDataManager.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , " + QueueTableDataManager.SongID + " TEXT , " + QueueTableDataManager.episode_id + " TEXT , " + QueueTableDataManager.name + " TEXT , "
            + QueueTableDataManager.thumbnail + " TEXT , " + QueueTableDataManager.video_code + " TEXT , " + QueueTableDataManager.audio + " TEXT , " + QueueTableDataManager.duration + " TEXT , " + QueueTableDataManager.singer_id + " TEXT , " + QueueTableDataManager.singer_type + " TEXT , " + QueueTableDataManager.singer_name + " TEXT , "
            + QueueTableDataManager.large_logo + " TEXT , " + QueueTableDataManager.small_logo + " TEXT , " + QueueTableDataManager.banner + " TEXT , " + QueueTableDataManager.description + " TEXT , " + QueueTableDataManager.band_status + " TEXT , " + QueueTableDataManager.season_id + " TEXT ); ";

    private final String CREATE_SONGS_DOWNLOAD = "create table if not exists " + QueueTableDataManager.DOWNLOADED_SONGS_TABLE + "(" + QueueTableDataManager.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE , " + QueueTableDataManager.SongID + " TEXT , " + QueueTableDataManager.episode_id + " TEXT , " + QueueTableDataManager.name + " TEXT , "
            + QueueTableDataManager.thumbnail + " TEXT , " + QueueTableDataManager.video_code + " TEXT , " + QueueTableDataManager.audio + " TEXT , " + QueueTableDataManager.duration + " TEXT , " + QueueTableDataManager.singer_id + " TEXT , " + QueueTableDataManager.singer_type + " TEXT , " + QueueTableDataManager.singer_name + " TEXT , "
            + QueueTableDataManager.large_logo + " TEXT , " + QueueTableDataManager.small_logo + " TEXT , " + QueueTableDataManager.banner + " TEXT , " + QueueTableDataManager.description + " TEXT , " + QueueTableDataManager.band_status + " TEXT , " + QueueTableDataManager.season_id + " TEXT , " + QueueTableDataManager.downloadingId + " TEXT , " + QueueTableDataManager.downloadProgress + " TEXT , " + QueueTableDataManager.downloadingStatus + " TEXT ); ";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    private DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance() {
        synchronized (lock) {
            if (instance == null)
                instance = new DatabaseHelper(PepsiApplication.getAppContext());
            return instance;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SONGS_QUEUE);
        db.execSQL(CREATE_SONGS_DOWNLOAD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
