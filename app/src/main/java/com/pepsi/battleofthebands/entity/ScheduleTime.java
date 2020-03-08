package com.pepsi.battleofthebands.entity;

import java.util.ArrayList;

public class ScheduleTime {
    private String time;
    private ArrayList<Schedule> channels;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ArrayList<Schedule> getChannels() {
        return channels;
    }

    public void setChannels(ArrayList<Schedule> channels) {
        this.channels = channels;
    }
}
