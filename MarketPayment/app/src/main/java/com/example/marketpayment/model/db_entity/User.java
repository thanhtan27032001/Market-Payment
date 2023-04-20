package com.example.marketpayment.model.db_entity;

public class User {
    private String userName;
    private String email;
    private String nickName;
    private String role;
    private long avtColor;

    public long getAvtColor() {
        return avtColor;
    }

    public void setAvtColor(long avtColor) {
        this.avtColor = avtColor;
    }

    public User() {
    }

    public User(String userName, String email, String nickName, String role, long avtColor) {
        this.userName = userName;
        this.email = email;
        this.nickName = nickName;
        this.role = role;
        this.avtColor = avtColor;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
