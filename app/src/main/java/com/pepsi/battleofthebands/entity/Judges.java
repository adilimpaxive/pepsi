package com.pepsi.battleofthebands.entity;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Judges {
    private String id;
    private String name;
    private String designation;
    private String description;
    private String image;
    private String banner;
    private String season_id;
    private String created_at;
    private String updated_at;



    private String number;




    private String title;
    private ArrayList<Song> songs;
    private ArrayList<Episode> episodes;

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

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getSeason_id() {
        return season_id;
    }

    public void setSeason_id(String season_id) {
        this.season_id = season_id;
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
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }
public ArrayList<Episode>getEpisode(){
        return episodes;
}

public void setEpisodes(ArrayList<Episode>episodes){
        this.episodes=episodes;
}

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
