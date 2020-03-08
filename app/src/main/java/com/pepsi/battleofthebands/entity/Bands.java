package com.pepsi.battleofthebands.entity;

import java.util.ArrayList;

public class Bands {
    private String id;
    private String name;
    private String small_logo;
    private String large_logo;
    private String banner;
    private String description;
    private String created_at;
    private String updated_at;
    private ArrayList<Song> songs;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSmall_logo() {
        return small_logo;
    }

    public void setSmall_logo(String small_logo) {
        this.small_logo = small_logo;
    }

    public String getLarge_logo() {
        return large_logo;
    }

    public void setLarge_logo(String large_logo) {
        this.large_logo = large_logo;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
