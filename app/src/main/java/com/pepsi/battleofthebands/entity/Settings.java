package com.pepsi.battleofthebands.entity;

import java.util.ArrayList;

public class Settings {
    private String id;
    private int theme;
    private int voting;
    private String tvc_link;
    private String created_at;
    private String updated_at;
    private String term_condition;
    private String main_page_text;
    private String main_page_link;
    private ArrayList<SocialMedia> social_media;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    public int getVoting() {
        return voting;
    }

    public void setVoting(int voting) {
        this.voting = voting;
    }

    public String getTvc_link() {
        return tvc_link;
    }

    public void setTvc_link(String tvc_link) {
        this.tvc_link = tvc_link;
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

    public String getTerm_condition() {
        return term_condition;
    }

    public void setTerm_condition(String term_condition) {
        this.term_condition = term_condition;
    }

    public String getMain_page_text() {
        return main_page_text;
    }

    public void setMain_page_text(String main_page_text) {
        this.main_page_text = main_page_text;
    }

    public String getMain_page_link() {
        return main_page_link;
    }

    public void setMain_page_link(String main_page_link) {
        this.main_page_link = main_page_link;
    }

    public ArrayList<SocialMedia> getSocialmedia() {
        return social_media;
    }

    public void setSocialmedia(ArrayList<SocialMedia> socialmedia) {
        this.social_media = socialmedia;
    }
}
