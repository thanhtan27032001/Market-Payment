package com.example.marketpayment.model.db_entity;

import com.example.marketpayment.model.CurrentUser;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Season implements Serializable {
    private String id;
    private long totalPaid;
    private String dateStart;
    private String dateEnd;
    private String creator;
    private String finisher;

    public Season() {
    }

    public Season(String id) {
        this.id = id;
        this.totalPaid = 0;
        this.dateStart = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        this.dateEnd = "__/__/____";
        this.creator = CurrentUser.getInstance().getUser().getUserName();
        this.finisher = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(long totalPaid) {
        this.totalPaid = totalPaid;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getFinisher() {
        return finisher;
    }

    public void setFinisher(String finisher) {
        this.finisher = finisher;
    }
}
