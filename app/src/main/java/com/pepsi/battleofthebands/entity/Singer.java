package com.pepsi.battleofthebands.entity;

import java.io.Serializable;

public class Singer implements Serializable {
    private String id;
    private String name;
    private String large_logo;
    private String small_logo;
    private String banner;
    private String description;
    private String band_status;
    private String season_id;

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

    public String getLarge_logo() {
        return large_logo;
    }

    public void setLarge_logo(String large_logo) {
        this.large_logo = large_logo;
    }

    public String getSmall_logo() {
        return small_logo;
    }

    public void setSmall_logo(String small_logo) {
        this.small_logo = small_logo;
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

    public String getBand_status() {
        return band_status;
    }

    public void setBand_status(String band_status) {
        this.band_status = band_status;
    }

    public String getSeason_id() {
        return season_id;
    }

    public void setSeason_id(String season_id) {
        this.season_id = season_id;
    }
}
