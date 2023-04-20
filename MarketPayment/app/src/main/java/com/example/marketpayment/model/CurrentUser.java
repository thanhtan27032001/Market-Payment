package com.example.marketpayment.model;

import com.example.marketpayment.model.db_entity.User;

public class CurrentUser {
    private User user;
    private static CurrentUser instance;
    public static CurrentUser getInstance(){
        if (instance == null)
            instance = new CurrentUser();
        return instance;
    }
    public void setUser(User user){
        this.user = user;
    }
    public User getUser(){
        return user;
    }
}
