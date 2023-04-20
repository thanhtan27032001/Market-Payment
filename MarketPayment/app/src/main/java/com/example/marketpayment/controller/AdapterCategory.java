package com.example.marketpayment.controller;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.marketpayment.model.db_entity.ItemCategory;
import com.example.marketpayment.view.activity.R;

import java.util.ArrayList;

public class AdapterCategory extends ArrayAdapter<ItemCategory> {
    public AdapterCategory(@NonNull Context context, int resource, @NonNull ArrayList<ItemCategory> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected, parent, false);
        ItemCategory itemCategory = getItem(position);
        TextView textView = convertView.findViewById(R.id.textCategory);
        if(itemCategory != null){
            textView.setText(itemCategory.getTenVatPham());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        ImageView imgColor = convertView.findViewById(R.id.imgColor);
        TextView txtCategory = convertView.findViewById(R.id.textCategory);
        TextView txtPrice = convertView.findViewById(R.id.textPrice);
        ItemCategory itemCategory = getItem(position);
        if(itemCategory != null){
            imgColor.setColorFilter(MyFormat.getArgbColor());
            txtCategory.setText((itemCategory.getTenVatPham()));
            txtPrice.setText(MyFormat.getCurrency(itemCategory.getGiaTien()));
        }
        return convertView;
    }
}
