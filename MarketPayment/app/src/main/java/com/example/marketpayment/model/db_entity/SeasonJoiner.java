package com.example.marketpayment.model.db_entity;

public class SeasonJoiner {
    private String userName;
    private long totalPaid;

    public SeasonJoiner() {
    }

    public SeasonJoiner(String userName, long totalPaid) {
        this.userName = userName;
        this.totalPaid = totalPaid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(long totalPaid) {
        this.totalPaid = totalPaid;
    }
}
