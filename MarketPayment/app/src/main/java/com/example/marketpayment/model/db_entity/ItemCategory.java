package com.example.marketpayment.model.db_entity;

public class ItemCategory {
    private String tenVatPham;
    private long giaTien;

    public ItemCategory() {
    }

    public ItemCategory(String tenVatPham, long giaTien) {
        this.tenVatPham = tenVatPham;
        this.giaTien = giaTien;
    }

    public String getTenVatPham() {
        return tenVatPham;
    }

    public void setTenVatPham(String tenVatPham) {
        this.tenVatPham = tenVatPham;
    }

    public long getGiaTien() {
        return giaTien;
    }

    public void setGiaTien(long giaTien) {
        this.giaTien = giaTien;
    }
}
