package com.pepsi.battleofthebands.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pepsi.battleofthebands.entity.DownloadedSong;
import com.pepsi.battleofthebands.entity.Singer;
import com.pepsi.battleofthebands.utils.PLog;

import java.util.ArrayList;

import static com.pepsi.battleofthebands.db.QueueTableDataManager.DOWNLOADED_SONGS_TABLE;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.ID;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.SongID;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.audio;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.band_status;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.banner;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.description;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.downloadProgress;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.downloadingId;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.downloadingStatus;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.duration;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.episode_id;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.large_logo;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.name;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.season_id;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.singer_id;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.singer_name;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.singer_type;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.small_logo;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.thumbnail;
import static com.pepsi.battleofthebands.db.QueueTableDataManager.video_code;

/**
 * Created by Muhammad Kashan on 11/20/2015.
 */
public class DownloadedSongsTableDataManager {

    private static DownloadedSongsTableDataManager instance;

    private DownloadedSongsTableDataManager() {
    }

    public static DownloadedSongsTableDataManager getInstance() {
        if (instance == null)
            instance = new DownloadedSongsTableDataManager();
        return instance;
    }

    /**
     * Get all Downloading/Downloaded songs from the database
     *
     * @param asc true for ascending order and false for descending
     * @return list of songs
     */
    public ArrayList<DownloadedSong> getDownloadedSongs(boolean asc) {
        ArrayList<DownloadedSong> songList = null;
        try {
            String order = " ASC";
            if (!asc)
                order = " DESC";
            songList = new ArrayList<>();
            // Select All Query
            String selectQuery = "SELECT  * FROM " + DOWNLOADED_SONGS_TABLE + " ORDER BY " + ID + order;

            SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    DownloadedSong song = new DownloadedSong();
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

                    song.setDownloadingId((cursor.getInt(17)));
                    song.setDownloadProgress((cursor.getInt(18)));
                    song.setDownloadingStatus(cursor.getString(19));

                    if (song.getDownloadingStatus().equals("Downloaded") || song.getDownloadingStatus().equals("Downloading"))
                        songList.add(song);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return songList;
    }

    public DownloadedSong findSong(String songId) {
        // SELECT * FROM tablename ORDER BY column DESC LIMIT 1;
        String selectQuery = "SELECT  * FROM " + DOWNLOADED_SONGS_TABLE + " WHERE " + SongID + " LIKE '" + songId + "'";
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && !cursor.isClosed()) {
            DownloadedSong song = new DownloadedSong();
            try {
                if (cursor.moveToFirst()) {
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

                    song.setDownloadingId((cursor.getInt(17)));
                    song.setDownloadProgress((cursor.getInt(18)));
                    song.setDownloadingStatus(cursor.getString(19));

                    cursor.close();
                    return song;
                }
            } finally {
                cursor.close();
            }
            cursor.close();
        }
        return null;
    }

    /**
     * Get all Downloaded/Downloading songs as cursor
     *
     * @return cursor containing Downloaded/Downloading songs
     */
    public Cursor getAllDowloadedSongs() {
        // Select All Query
        String order = " ASC";
        order = " DESC";
        String selectQuery = "SELECT  * FROM " + DOWNLOADED_SONGS_TABLE + " ORDER BY " + ID + order;
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        Cursor cursor;
        cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public int getRowCounts() {
        // Used if user reinstall app and sync saved songs from server
        String selectQuery = "SELECT  * FROM " + DOWNLOADED_SONGS_TABLE;
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public ArrayList<DownloadedSong> getAllSongs(String status) {
        ArrayList<DownloadedSong> songList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DOWNLOADED_SONGS_TABLE;
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DownloadedSong song = new DownloadedSong();
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

                song.setDownloadingId((cursor.getInt(17)));
                song.setDownloadProgress((cursor.getInt(18)));
                song.setDownloadingStatus(cursor.getString(19));
                if (status.equalsIgnoreCase(cursor.getString(19))) {
                    songList.add(song);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return songList;
    }

    public ArrayList<DownloadedSong> getAllRemainingSongs() {
        ArrayList<DownloadedSong> songList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + DOWNLOADED_SONGS_TABLE + " WHERE " + downloadProgress + " != " + 100;
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DownloadedSong song = new DownloadedSong();
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

                song.setDownloadingId((cursor.getInt(17)));
                song.setDownloadProgress((cursor.getInt(18)));
                song.setDownloadingStatus(cursor.getString(19));
                songList.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return songList;
    }

    public int insertSong(DownloadedSong song) {
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
        values.put(QueueTableDataManager.banner, song.getSinger().getBanner());
        values.put(description, song.getSinger().getDescription());
        values.put(band_status, song.getSinger().getBand_status());
        values.put(season_id, song.getSinger().getSeason_id());

        // Downloads
        values.put(downloadingId, song.getDownloadingId());
        values.put(downloadProgress, song.getDownloadProgress());
        values.put(downloadingStatus, song.getDownloadingStatus());
        // Inserting Row
        int result;
        // result = (int) db.insert(TABLE_NAME, null, values);
        result = (int) db.insertWithOnConflict(DOWNLOADED_SONGS_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        PLog.showLog("DB", "row result : " + result);
        return result;
    }

    /**
     * Update Song details of those whose song id matches with the one in
     * parameters
     *
     * @param song song
     */
    public int updateSongDetails(DownloadedSong song) {
        int result = 0;
        try {
            SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
            ContentValues values = new ContentValues();

            // Downloads
            values.put(downloadingId, song.getDownloadingId());
            values.put(downloadProgress, song.getDownloadProgress());
            values.put(downloadingStatus, song.getDownloadingStatus());

            result = db.update(DOWNLOADED_SONGS_TABLE, values, SongID + "=?", new String[]{song.getSongID()});
        } catch (Exception e) {
            e.printStackTrace();
        }
        PLog.showLog("Song Progress Updation Status " + result);
        return result;
    }

    /**
     * Delete Downloaded Song details of those whose song id matches with the one in
     * parameters
     *
     * @param id id
     */
    public void deleteSongDetails(String id) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
//        int result =db.delete(DOWNLOADED_SONGS_TABLE, songID + "=" + id, null);
        int result = db.delete(DOWNLOADED_SONGS_TABLE, SongID + "=?", new String[]{String.valueOf(id)});

        PLog.showLog("Song Delete Status " + result);
    }

    public void updateDownloadProgress(DownloadedSong mSong) {
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(downloadProgress, mSong.getDownloadProgress());
        int result = db.update(DOWNLOADED_SONGS_TABLE, cv, SongID + "=?", new String[]{mSong.getSongID()});
    }
}
