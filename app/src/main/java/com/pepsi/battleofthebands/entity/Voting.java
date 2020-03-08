package com.pepsi.battleofthebands.entity;

import java.util.ArrayList;

public class Voting {
    private Vote vote;
    private ArrayList<VotingBands> bands;

    public Vote getVote() {
        return vote;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }

    public ArrayList<VotingBands> getBands() {
        return bands;
    }

    public void setBands(ArrayList<VotingBands> bands) {
        this.bands = bands;
    }
}
