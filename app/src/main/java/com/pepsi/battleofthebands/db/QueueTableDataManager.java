package com.pepsi.battleofthebands.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pepsi.battleofthebands.entity.Singer;
import com.pepsi.battleofthebands.entity.Song;
import com.pepsi.battleofthebands.utils.PLog;

import java.util.ArrayList;

public class QueueTableDataManager {
    // Table Name
    public static final String DOWNLOADED_SONGS_TABLE = "DOWNLOADED_SONGS";
    public static final String SONGS_QUEUE_TABLE = "QUEUE_SONG";

    public static final String ID = "Id";

    // Song
    public static final String SongID = "SongID";
    public static final String episode_id = "episode_id";
    public static final String name = "name";
    public static final String thumbnail = "thumbnail";
    public static final String video_code = "video_code";
    public static final String audio = "audio";
    public static final String duration = "duration";
    public static final String singer_id = "singer_id";
    public static final String singer_type = "singer_type";

    // Singer
    public static final String singer_name = "singer_name";
    public static final String large_logo = "large_logo";
    public static final String small_logo = "small_logo";
    public static final String banner = "banner";
    public static final String description = "description";
    public static final String band_status = "band_status";
    public static final String season_id = "season_id";

    // Download
    public static final String downloadingId = "downloadingId";
    public static final String downloadProgress = "downloadProgress";
    public static final String downloadingStatus = "downloadingStatus";

    private static QueueTableDataManager instance;

    public static QueueTableDataManager getInstance() {
        if (instance == null)
            instance = new QueueTableDataManager();
        return instance;
    }

    /**
     * Get Songs from the Queue in Particular Order
     *
     * @param tableName table name
     * @return list of songs
     */
    public ArrayList<Song> getSongsListFromQueue(String tableName) {
        String order = " ASC";
        ArrayList<Song> songList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + tableName + " ORDER BY " + ID + order;

        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    Song song = new Song();
                    song._id = (cursor.getInt(0));

                    // Song
                    song.id = (cursor.getString(1));
                    song.episode_id = (cursor.getString(2));
                    song.name = (cursor.getString(3));
                    song.thumbnail = (cursor.getString(4));
                    song.video_code = (cursor.getString(5));
                    song.audio = (cursor.getString(6));
                    song.duration = (cursor.getString(7));
                    song.singer_id = (cursor.getString(8));
                    song.singer_type = (cursor.getString(9));

                    // Singer
                    Singer singer = new Singer();
                    singer.setName(cursor.getString(10));
                    singer.setLarge_logo(cursor.getString(11));
                    singer.setSmall_logo(cursor.getString(12));
                    singer.setBanner(cursor.getString(13));
                    singer.setDescription(cursor.getString(14));
                    singer.setBand_status(cursor.getString(15));
                    singer.setSeason_id(cursor.getString(16));
                    song.setSinger(singer);
                    songList.add(song);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return songList;
    }

    public int insertSongInToQueue(Song song, String tableName) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues values = new ContentValues();

        // Song
        values.put(SongID, song.getSongID());
        values.put(episode_id, song.episode_id);
        values.put(name, song.name);
        values.put(thumbnail, song.thumbnail);
        values.put(video_code, song.video_code);
        values.put(audio, song.audio);
        values.put(duration, song.duration);
        values.put(singer_id, song.singer_id);
        values.put(singer_type, song.singer_type);

        // Singer
        values.put(singer_name, song.getSinger().getName());
        values.put(large_logo, song.getSinger().getLarge_logo());
        values.put(small_logo, song.getSinger().getSmall_logo());
        values.put(banner, song.getSinger().getBanner());
        values.put(description, song.getSinger().getDescription());
        values.put(band_status, song.getSinger().getBand_status());
        values.put(season_id, song.getSinger().getSeason_id());

        // Inserting Row
        int result = -1;
//        result = (int) db.insert(SONGS_QUEUE_TABLE, null, values);
        result = (int) db.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        PLog.showLog("DB", "row result : " + result);
        return result;
    }

    public Song getFirstSongFromQueue(String tableName) {
        // SELECT * FROM tablename ORDER BY column DESC LIMIT 1;
        String selectQuery = "SELECT  * FROM " + tableName + " ORDER BY " + ID + " ASC LIMIT 1";
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Song song = new Song();
        if (cursor.moveToFirst()) {
            do {
                song._id = (cursor.getInt(0));

                // Song
                song.id = (cursor.getString(1));
                song.episode_id = (cursor.getString(2));
                song.name = (cursor.getString(3));
                song.thumbnail = (cursor.getString(4));
                song.video_code = (cursor.getString(5));
                song.audio = (cursor.getString(6));
                song.duration = (cursor.getString(7));
                song.singer_id = (cursor.getString(8));
                song.singer_type = (cursor.getString(9));

                // Singer
                Singer singer = new Singer();
                singer.setName(cursor.getString(10));
                singer.setLarge_logo(cursor.getString(11));
                singer.setSmall_logo(cursor.getString(12));
                singer.setBanner(cursor.getString(13));
                singer.setDescription(cursor.getString(14));
                singer.setBand_status(cursor.getString(15));
                singer.setSeason_id(cursor.getString(16));
                song.setSinger(singer);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return song;
    }

    // Deleting single item
    public int deleteSongFromQueue(Song song, String tableName) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        int result = db.delete(tableName, ID + " = ?", new String[]{String.valueOf(song.id)});
        PLog.showLog("delete song result row : " + result);
        return result;
    }

    public void deleteSongsQueueTableData(String tableName) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        int result = db.delete(tableName, null, null);
        PLog.showLog("delete songs list result row : " + result);

    }

    /**
     * @return number of records in Queue table
     */
    public int getNumOfRecordInQueueTable() {
        String selectQuery = "SELECT  * FROM " + SONGS_QUEUE_TABLE;
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount();
    }
}
