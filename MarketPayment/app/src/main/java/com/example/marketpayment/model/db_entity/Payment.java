package com.example.marketpayment.model.db_entity;

public class Payment {
    private String id;
    private String itemName;
    private long itemPrice;
    private String insertDate;
    private String userName;

    public Payment() {
    }

    public Payment(String id, String itemName, long itemPrice, String insertDate, String userName) {
        this.id = id;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.insertDate = insertDate;
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public long getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(long itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
