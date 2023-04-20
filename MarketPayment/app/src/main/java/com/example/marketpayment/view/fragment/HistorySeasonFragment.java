package com.example.marketpayment.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.marketpayment.controller.AdapterSeasonJoiners;
import com.example.marketpayment.controller.MyFormat;
import com.example.marketpayment.model.db_entity.Season;
import com.example.marketpayment.model.db_entity.SeasonJoiner;
import com.example.marketpayment.view.activity.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HistorySeasonFragment extends Fragment {
    private Season season;

    private DatabaseReference dbRef;

    private TextView txtUserCount;
    private TextView txtAveragePaid;
    private TextView txtCreator;
    private TextView txtFinisher;
    private TextView txtPeriod;
    private TextView txtTotalPaid;
    private RecyclerView recyclerViewSeasonJoiners;
    private AdapterSeasonJoiners adapterSeasonJoiners;
    private final ArrayList<SeasonJoiner> seasonJoiners = new ArrayList<>();

    public HistorySeasonFragment(Season season) {
        this.season = season;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_season, container, false);
        txtUserCount = view.findViewById(R.id.txtUserCount);
        txtAveragePaid = view.findViewById(R.id.txtAveragePaid);
        txtCreator = view.findViewById(R.id.txtCreator);
        txtFinisher = view.findViewById(R.id.txtFinisher);
        txtPeriod = view.findViewById(R.id.txtPeriod);
        txtTotalPaid = view.findViewById(R.id.txtTotalPaid);
        recyclerViewSeasonJoiners = view.findViewById(R.id.recyclerViewSeasonJoiners);
        recyclerViewSeasonJoiners.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        adapterSeasonJoiners = new AdapterSeasonJoiners(getContext(), seasonJoiners, season, dbRef);
        recyclerViewSeasonJoiners.setAdapter(adapterSeasonJoiners);
        loadSeasonJoiner();
        return view;
    }
    private void loadSeasonJoiner(){
        txtCreator.setText(season.getCreator());
        txtFinisher.setText(String.valueOf(season.getFinisher()));
        String period = season.getDateStart() + " - " + season.getDateEnd();
        txtPeriod.setText(period);
        txtTotalPaid.setText(MyFormat.getCurrency(season.getTotalPaid()));
        dbRef.child("DotGiaoDich/" + season.getId() + "/ThamGia").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                // Lấy tổng chi tiêu từng người tham gia
                seasonJoiners.clear();
                String userName;
                long totalPaid;
                for (DataSnapshot d: task.getResult().getChildren()){
                    userName = d.getKey();
                    totalPaid = d.getValue(Long.class) != null ? d.getValue(Long.class) : 0;
                    seasonJoiners.add(new SeasonJoiner(userName, totalPaid));
                }
                // Sắp xếp chi tiêu người tham gia từ cao đến thấp
                Comparator<SeasonJoiner> comparator = new Comparator<SeasonJoiner>() {
                    @Override
                    public int compare(SeasonJoiner s1, SeasonJoiner s2) {
                        return Long.compare(s1.getTotalPaid(), s2.getTotalPaid());
                    }
                };
                seasonJoiners.sort(comparator);
                Collections.reverse(seasonJoiners);
                // Set thông tin chi tiêu trung bình
                int joinCount = seasonJoiners.size();
                long avgPaid = joinCount > 0 ? season.getTotalPaid()/joinCount : 0;
                txtUserCount.setText(String.valueOf(joinCount));
                txtAveragePaid.setText(MyFormat.getCurrency(avgPaid));
                adapterSeasonJoiners.notifyItemRangeChanged(0, seasonJoiners.size());
            }
        });
    }
}