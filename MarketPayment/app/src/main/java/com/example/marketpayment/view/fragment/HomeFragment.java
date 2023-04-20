package com.example.marketpayment.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.marketpayment.controller.AdapterSeasonJoiners;
import com.example.marketpayment.model.CurrentSeason;
import com.example.marketpayment.model.CurrentUser;
import com.example.marketpayment.controller.MyFormat;
import com.example.marketpayment.model.db_entity.SeasonJoiner;
import com.example.marketpayment.view.activity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HomeFragment extends Fragment {

    private DatabaseReference dbRef;

    private SwipeRefreshLayout swipeRefreshHome;
    private TextView txtUserCount;
    private TextView txtUserNickname;
    private LinearLayout btnExpandCollapse;
    private ImageView imgExpandCollapse;
    private TableLayout tbSeasonDetail;
    private TextView txtAveragePaid;
    private TextView txtCreator;
    private TextView txtSeason;
    private TextView txtPeriod;
    private TextView txtTotalPaid;
    private RecyclerView recyclerViewSeasonJoiners;
    private AdapterSeasonJoiners adapterSeasonJoiners;
    private final ArrayList<SeasonJoiner> seasonJoiners = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        swipeRefreshHome = view.findViewById(R.id.swipeRefreshHome);
        swipeRefreshHome.setOnRefreshListener(() -> {
            loadUIData();
            swipeRefreshHome.setRefreshing(false);
        });
        txtUserCount = view.findViewById(R.id.txtUserCount);
        txtUserNickname = view.findViewById(R.id.txtLoginNickname);
        tbSeasonDetail = view.findViewById(R.id.tbSeasonDetail);
        imgExpandCollapse = view.findViewById(R.id.imgExpandCollapse);
        btnExpandCollapse = view.findViewById(R.id.btnExpandCollapse);
        btnExpandCollapse.setOnClickListener(view1 -> {
            if(tbSeasonDetail.getVisibility() == View.VISIBLE){
                tbSeasonDetail.setVisibility(View.GONE);
                imgExpandCollapse.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24);
            }
            else {
                tbSeasonDetail.setVisibility(View.VISIBLE);
                imgExpandCollapse.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
            }
        });
        txtAveragePaid = view.findViewById(R.id.txtAveragePaid);
        txtCreator = view.findViewById(R.id.txtCreator);
        txtSeason = view.findViewById(R.id.txtSeason);
        txtPeriod = view.findViewById(R.id.txtPeriod);
        txtTotalPaid = view.findViewById(R.id.txtTotalPaid);
        recyclerViewSeasonJoiners = view.findViewById(R.id.recyclerViewSeasonJoiners);
        recyclerViewSeasonJoiners.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        adapterSeasonJoiners = new AdapterSeasonJoiners(getContext(), seasonJoiners, CurrentSeason.getInstance().getSeason(), dbRef);
        recyclerViewSeasonJoiners.setAdapter(adapterSeasonJoiners);
        loadUIData();
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        String token = task.getResult();
//                        txtUserNickname.setText(token);
//                    }
//                });
        return view;
    }
    private void loadUIData() {
        loadUserInfo();
        loadCurrentSeasonJoiner();
    }
    private void loadUserInfo(){
        // Nickname
        String userNickname = getString(R.string.greeting_text);
        userNickname = userNickname.concat(" " + CurrentUser.getInstance().getUser().getNickName());
        txtUserNickname.setText(userNickname);
    }
    private void loadCurrentSeasonJoiner(){
        dbRef.child("DotGiaoDich/" + CurrentSeason.getInstance().getSeason().getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Set thông tin tổng/trung bình chi tiêu của season hiện tại
                txtCreator.setText(CurrentSeason.getInstance().getSeason().getCreator());
                txtSeason.setText(String.valueOf(CurrentSeason.getInstance().getSeason().getId()));
                String period = CurrentSeason.getInstance().getSeason().getDateStart() + " - " + CurrentSeason.getInstance().getSeason().getDateEnd();
                txtPeriod.setText(period);
                txtTotalPaid.setText(MyFormat.getCurrency(CurrentSeason.getInstance().getSeason().getTotalPaid()));
                dbRef.child("DotGiaoDich/" + CurrentSeason.getInstance().getSeason().getId() + "/ThamGia").get().addOnCompleteListener(task -> {
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
                        long avgPaid = joinCount > 0 ? CurrentSeason.getInstance().getSeason().getTotalPaid()/joinCount : 0;
                        txtUserCount.setText(String.valueOf(joinCount));
                        txtAveragePaid.setText(MyFormat.getCurrency(avgPaid));
                        adapterSeasonJoiners.notifyItemRangeChanged(0, seasonJoiners.size());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}