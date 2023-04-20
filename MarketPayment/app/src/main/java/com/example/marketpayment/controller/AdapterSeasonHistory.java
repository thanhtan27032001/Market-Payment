package com.example.marketpayment.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketpayment.model.db_entity.Season;
import com.example.marketpayment.view.activity.PaymentHistoryActivity;
import com.example.marketpayment.view.activity.R;

import java.util.ArrayList;

public class AdapterSeasonHistory extends RecyclerView.Adapter<AdapterSeasonHistory.ViewHolder> {

    private Context context;
    private ArrayList<Season> seasonArrayList;

    public AdapterSeasonHistory(Context context, ArrayList<Season> seasonArrayList) {
        this.context = context;
        this.seasonArrayList = seasonArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_season_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Season season = seasonArrayList.get(position);
        holder.txtSeasonId.setText(String.valueOf(season.getId()));
        holder.txtSeasonId.setBackgroundColor(MyFormat.getArgbColor());
        holder.txtPeriod.setText("From " + season.getDateStart() + " to " + season.getDateEnd());
        holder.txtCreator.setText(season.getCreator());
        holder.txtFinisher.setText(season.getFinisher());
        holder.txtTotalPaid.setText("Total paid: " + MyFormat.getCurrency(season.getTotalPaid()));
        holder.itemSeasonHistory.setOnClickListener(view -> {
            Intent intent = new Intent(context, PaymentHistoryActivity.class);
            intent.putExtra(PaymentHistoryActivity.TAG_SEASON, season);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return seasonArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtSeasonId;
        private TextView txtPeriod;
        private TextView txtCreator;
        private TextView txtFinisher;
        private TextView txtTotalPaid;
        private LinearLayout itemSeasonHistory;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSeasonId = itemView.findViewById(R.id.txtSeasonId);
            txtPeriod = itemView.findViewById(R.id.txtPeriod);
            txtCreator = itemView.findViewById(R.id.txtCreator);
            txtFinisher = itemView.findViewById(R.id.txtFinisher);
            txtTotalPaid = itemView.findViewById(R.id.txtTotalPaid);
            itemSeasonHistory = itemView.findViewById(R.id.itemSeasonHistory);
        }
    }
}
