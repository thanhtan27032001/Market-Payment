package com.example.marketpayment.view.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.marketpayment.controller.AdapterPaymentHistory;
import com.example.marketpayment.controller.AdapterSeasonHistory;
import com.example.marketpayment.model.CurrentSeason;
import com.example.marketpayment.model.db_entity.Payment;
import com.example.marketpayment.model.db_entity.Season;
import com.example.marketpayment.model.db_entity.User;
import com.example.marketpayment.view.activity.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryFragment extends Fragment {

    private DatabaseReference dbRef;
    private SharedPreferences sharedPreferences;

    private SwipeRefreshLayout swipeRefreshHistory;

    private RecyclerView rvPaymentHistory;
    private AdapterPaymentHistory adapterPaymentHistory;
    private ArrayList<Payment> paymentArrayList;
    private HashMap<String, User> joinerHashMap;

    private RecyclerView rvSeasonHistory;
    private AdapterSeasonHistory adapterSeasonHistory;
    private ArrayList<Season> seasonArrayList;

    private LinearLayout btnExpandCollapse;
    private ImageView imgExpandCollapse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbRef = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        // SwipeRefreshLayout
        swipeRefreshHistory = view.findViewById(R.id.swipeRefreshHistory);
        swipeRefreshHistory.setOnRefreshListener(() -> {
            getPaymentHistory();
            getSeasonHistory();
            swipeRefreshHistory.setRefreshing(false);
        });
        // RecyclerView lịch sử giao dịch
        rvPaymentHistory = view.findViewById(R.id.rvPaymentHistory);
        paymentArrayList = new ArrayList<>();
        joinerHashMap = new HashMap<>();
        adapterPaymentHistory = new AdapterPaymentHistory(getContext(), paymentArrayList, joinerHashMap);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public int computeVerticalScrollOffset(RecyclerView.State state) {
                if (findFirstCompletelyVisibleItemPosition() == 0) {
                    return 0;
                } else {
                    return super.computeVerticalScrollOffset(state);
                }
            }
        };
        rvPaymentHistory.setLayoutManager(layoutManager);
        rvPaymentHistory.setAdapter(adapterPaymentHistory);
        getPaymentHistory();

        imgExpandCollapse = view.findViewById(R.id.imgExpandCollapse);
        btnExpandCollapse = view.findViewById(R.id.btnExpandCollapse);
        btnExpandCollapse.setOnClickListener(view1 -> {
            if(rvPaymentHistory.getVisibility() == View.VISIBLE){
                rvPaymentHistory.setVisibility(View.GONE);
                imgExpandCollapse.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24);
            }
            else {
                rvPaymentHistory.setVisibility(View.VISIBLE);
                imgExpandCollapse.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24);
            }
        });
        // RecyclerView lịch sử các đợt giao dịch
        rvSeasonHistory = view.findViewById(R.id.rvOlderSeason);
        seasonArrayList = new ArrayList<>();
        adapterSeasonHistory = new AdapterSeasonHistory(getContext(), seasonArrayList);
        rvSeasonHistory.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvSeasonHistory.setAdapter(adapterSeasonHistory);
        getSeasonHistory();
        return view;
    }
    private void getSeasonHistory(){
        seasonArrayList.clear();
        dbRef.child("DotGiaoDich").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (DataSnapshot d: task.getResult().getChildren()){
                    if (!String.valueOf(d.getKey()).equals(String.valueOf(CurrentSeason.getInstance().getSeason().getId()))){
                        seasonArrayList.add(d.getValue(Season.class));
                    }
                }
                adapterSeasonHistory.notifyItemRangeChanged(0, seasonArrayList.size());
            }
        });
    }

    private void getPaymentHistory() { // Lấy thông tin người tham gia trước -> lấy các lịch sử giao dịch
        joinerHashMap.clear();
        FirebaseDatabase.getInstance().getReference().child("NguoiDung").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (DataSnapshot d: task.getResult().getChildren()){
                    User user = d.getValue(User.class);
                    if (user != null){
                        joinerHashMap.put(user.getUserName(), user);
                    }
                }
                paymentArrayList.clear();
                dbRef.child("DotGiaoDich/" + CurrentSeason.getInstance().getSeason().getId() + "/GiaoDich").get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        for (DataSnapshot d: task1.getResult().getChildren()){
                            Payment payment = d.getValue(Payment.class);
                            paymentArrayList.add(0, payment);
                        }
                        paymentArrayList.add(0, new Payment());
                        adapterPaymentHistory.notifyItemRangeChanged(0, paymentArrayList.size());
                    }
                });
            }
        });
    }

    public void addPayment(Payment payment){
        paymentArrayList.add(1, payment);
        adapterPaymentHistory.notifyItemInserted(1);
    }
}
