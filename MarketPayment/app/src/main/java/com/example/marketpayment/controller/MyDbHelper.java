package com.example.marketpayment.controller;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyDbHelper {
    private static MyDbHelper instance;
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private OnCompleteListener<DataSnapshot> listener;

    public static MyDbHelper getInstance(){
        if(instance == null)
            instance = new MyDbHelper();
        return instance;
    }

    synchronized public void insert(String path, Object obj){
        try{
            dbRef.child(path).setValue(obj);
        }
        catch (Exception e){
            throw e;
        }
    }
}
