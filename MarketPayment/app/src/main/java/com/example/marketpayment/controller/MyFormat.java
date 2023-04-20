package com.example.marketpayment.controller;

import android.graphics.Color;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class MyFormat {
    private static final Random random = new Random();
    public static String getCurrentDateString(){
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    }
    public static String getCurrentDateTimeString(){
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
    }
    public static String getIdFromDate(Date date){
        return new SimpleDateFormat("yyyyMMddHHmmss").format(date);
    }
    public static String getDateString(Date date){
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
    }
    public static String getCurrency(long money){
        return String.format("%,d", money) + "₫";
    }
    public static String getCurrencyWithoutUnit(long money){
        return String.format("%,d", money);
    }
    public static int getArgbColor(){
        return Color.argb(255, random.nextInt(255)+1, random.nextInt(255)+1, random.nextInt(255)+1);
    }
    public static int getArgbColor(String red, String green, String blue){
        return Color.argb(255, Integer.parseInt(red), Integer.parseInt(green), Integer.parseInt(blue));
    }
    public static int getArgbDarkColor(){
        return Color.argb(255, random.nextInt(128), random.nextInt(128), random.nextInt(128));
    }
    public static boolean isColorDark(int color){
        double darkness = 1-(0.299*Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;
        return darkness >= 0.2;
    }
    public static String getHexColor(String red, String green, String blue){
        return String.format("#%02x%02x%02x", Integer.parseInt(red), Integer.parseInt(green), Integer.parseInt(blue)).toUpperCase();
    }
}
