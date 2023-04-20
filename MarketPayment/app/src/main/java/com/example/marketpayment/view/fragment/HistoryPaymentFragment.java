package com.example.marketpayment.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.marketpayment.controller.AdapterPaymentHistory;
import com.example.marketpayment.controller.AdapterPaymentHistoryOlder;
import com.example.marketpayment.model.db_entity.Payment;
import com.example.marketpayment.model.db_entity.Season;
import com.example.marketpayment.view.activity.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HistoryPaymentFragment extends Fragment {
    private Season season;

    private DatabaseReference dbRef;

    private RecyclerView rvPaymentHistory;
    private AdapterPaymentHistoryOlder adapterPaymentHistoryOlder;
    private ArrayList<Payment> paymentArrayList;

    public HistoryPaymentFragment(Season season) {
        this.season = season;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_payment, container, false);
        // RecyclerView lịch sử giao dịch
        rvPaymentHistory = view.findViewById(R.id.rvPaymentHistory);
        paymentArrayList = new ArrayList<>();
        adapterPaymentHistoryOlder = new AdapterPaymentHistoryOlder(getContext(), paymentArrayList);
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
        rvPaymentHistory.setAdapter(adapterPaymentHistoryOlder);
        getPaymentHistory();
        return view;
    }

    private void getPaymentHistory() {
        paymentArrayList.clear();
        dbRef.child("DotGiaoDich/" + season.getId() + "/GiaoDich").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (DataSnapshot d: task.getResult().getChildren()){
                    Payment payment = d.getValue(Payment.class);
                    paymentArrayList.add(0, payment);
                }
                paymentArrayList.add(0, new Payment());
                adapterPaymentHistoryOlder.notifyItemRangeChanged(0, paymentArrayList.size());
            }
        });
    }
}