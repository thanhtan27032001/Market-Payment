package com.example.marketpayment.controller;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketpayment.model.db_entity.Season;
import com.example.marketpayment.model.db_entity.SeasonJoiner;
import com.example.marketpayment.view.activity.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class AdapterSeasonJoiners extends RecyclerView.Adapter<AdapterSeasonJoiners.ViewHolder> {

    private Context context;
    private ArrayList<SeasonJoiner> seasonJoiners;
    private Season season;
    private DatabaseReference dbRef;

    public AdapterSeasonJoiners(Context context, ArrayList<SeasonJoiner> seasonJoiners, Season season, DatabaseReference dbRef) {
        this.context = context;
        this.seasonJoiners = seasonJoiners;
        this.season = season;
        this.dbRef = dbRef;
    }

    @NonNull
    @Override
    public AdapterSeasonJoiners.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_season_joiners, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSeasonJoiners.ViewHolder holder, int position) {
        SeasonJoiner seasonJoiner = seasonJoiners.get(position);
        dbRef.child("NguoiDung/" + seasonJoiner.getUserName() + "/nickName").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                holder.txtNickname.setText(task.getResult().getValue(String.class));
            }
        });
        holder.txtTotalPaid.setText(MyFormat.getCurrencyWithoutUnit(seasonJoiner.getTotalPaid()));
        holder.txtIndexPaid.setText(MyFormat.getCurrencyWithoutUnit(seasonJoiner.getTotalPaid() - season.getTotalPaid()/seasonJoiners.size()));
        if(seasonJoiner.getTotalPaid() - season.getTotalPaid()/seasonJoiners.size() >= 0){
            holder.imgUpOrDown.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
            holder.imgUpOrDown.setColorFilter(context.getColor(R.color.positive));
        }
        else {
            holder.imgUpOrDown.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
            holder.imgUpOrDown.setColorFilter(context.getColor(R.color.negative));
        }
    }

    @Override
    public int getItemCount() {
        return seasonJoiners.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtNickname;
        private TextView txtTotalPaid;
        private TextView txtIndexPaid;
        private ImageView imgUpOrDown;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNickname = itemView.findViewById(R.id.txtNickname);
            txtTotalPaid = itemView.findViewById(R.id.txtTotalPaid);
            txtIndexPaid = itemView.findViewById(R.id.txtIndexPaid);
            imgUpOrDown = itemView.findViewById(R.id.imgUpOrDown);
        }
    }
}
