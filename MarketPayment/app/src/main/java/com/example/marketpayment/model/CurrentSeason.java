package com.example.marketpayment.model;

import com.example.marketpayment.model.db_entity.Season;

public class CurrentSeason {
    private static CurrentSeason instance;
    private Season season;

    public Season getSeason() {
        return season;
    }

    public boolean setSeason(Season season) {
        this.season = season;
        return true;
    }

    public static CurrentSeason getInstance(){
        if (instance == null)
            instance = new CurrentSeason();
        return instance;
    }
}
