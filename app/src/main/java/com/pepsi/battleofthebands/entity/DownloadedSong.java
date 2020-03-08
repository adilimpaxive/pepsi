package com.pepsi.battleofthebands.entity;

import java.io.Serializable;

/**
 * Created by Muhammad Kashan on 11/20/2015.
 */
public class DownloadedSong extends Song implements Serializable {
    private static final long serialVersionUID = -9089310009450287109L;
    private long downloadingId;
    private int downloadProgress = 0;
    private String downloadingStatus = "";

    public long getDownloadingId() {
        return downloadingId;
    }

    public void setDownloadingId(long downloadingId) {
        this.downloadingId = downloadingId;
    }

    public int getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(int downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public String getDownloadingStatus() {
        return downloadingStatus;
    }

    public void setDownloadingStatus(String downloadingStatus) {
        this.downloadingStatus = downloadingStatus;
    }

}
