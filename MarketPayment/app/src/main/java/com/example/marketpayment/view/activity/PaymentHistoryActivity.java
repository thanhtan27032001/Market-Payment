package com.example.marketpayment.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.marketpayment.model.db_entity.Season;
import com.example.marketpayment.view.fragment.HistoryPaymentFragment;
import com.example.marketpayment.view.fragment.HistorySeasonFragment;

public class PaymentHistoryActivity extends AppCompatActivity {
    public static final String TAG_SEASON = "season";

    private HistorySeasonFragment historySeasonFragment;
    private HistoryPaymentFragment historyPaymentFragment;

    private FragmentManager fragmentManager;

    private TextView txtSeasonId;
    private ImageButton btnBack;
    private RadioButton tabSeason;
    private RadioButton tabPayment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);
        // Lấy season được truyền qua
        Season seasonPassed = (Season) getIntent().getSerializableExtra(TAG_SEASON);
        // Khởi tạo các fragment
        historySeasonFragment = new HistorySeasonFragment(seasonPassed);
        historyPaymentFragment = new HistoryPaymentFragment(seasonPassed);
        // Khởi tạo các view
        txtSeasonId = findViewById(R.id.txtSeasonId);
        String seasonId = getText(R.string.textview_season2) + " " + seasonPassed.getId();
        txtSeasonId.setText(seasonId);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> finish());
        fragmentManager = this.getSupportFragmentManager();
        tabSeason = findViewById(R.id.tabSeason);
        tabSeason.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                if (fragmentManager.findFragmentByTag("payment") != null){
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_right_to_left)
                            .hide(fragmentManager.findFragmentByTag("payment"))
                            .commit();
                }
                if (fragmentManager.findFragmentByTag("season") != null){
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_left_to_right, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_left_to_right)
                            .show(fragmentManager.findFragmentByTag("season"))
                            .commit();
                }
                else {
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_left_to_right, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_left_to_right)
                            .add(R.id.fragmentContainer, historySeasonFragment, "season")
                            .commit();
                }
//                PaymentHistoryActivity.this.getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragmentContainer, seasonFragment, null)
//                        .commit();
            }
        });
        tabPayment = findViewById(R.id.tabPayment);
        tabPayment.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                if (fragmentManager.findFragmentByTag("season") != null){
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_right_to_left)
                            .hide(fragmentManager.findFragmentByTag("season"))
                            .commit();
                }
                if (fragmentManager.findFragmentByTag("payment") != null){
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_right_to_left)
                            .show(fragmentManager.findFragmentByTag("payment"))
                            .commit();
                }
                else {
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right_to_left, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_right_to_left)
                            .add(R.id.fragmentContainer, historyPaymentFragment, "payment")
                            .commit();
                }
//                PaymentHistoryActivity.this.getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragmentContainer, paymentFragment, null)
//                        .commit();
            }
        });
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, historySeasonFragment, "season")
                .commit();
    }
}