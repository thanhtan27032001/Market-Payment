package com.example.marketpayment.view.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketpayment.controller.MyDbHelper;
import com.example.marketpayment.controller.MyFormat;
import com.example.marketpayment.model.CurrentSeason;
import com.example.marketpayment.model.CurrentUser;
import com.example.marketpayment.model.db_entity.Payment;
import com.example.marketpayment.model.db_entity.Season;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class NewPaymentWidget extends AppCompatActivity {
    private String currentSeasonId;

    private DatabaseReference dbRef;
    private SharedPreferences sharedPreferences;

    private EditText edtItemName;
    private EditText edtItemPrice;
    private Button btnCancel;
    private Button btnSubmit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_new_payment);
        //
        dbRef = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        getCurrentSeasonId();
        // Init view
        edtItemName = findViewById(R.id.edtItemName);
        edtItemPrice = findViewById(R.id.edtItemPrice);
        btnCancel = findViewById(R.id.btnCancel);
        btnSubmit = findViewById(R.id.btnSubmit);
        // Event
        btnCancel.setOnClickListener(view -> finish());
        btnSubmit.setOnClickListener(view -> {
            boolean remembered = sharedPreferences.getBoolean(LoginActivity.TAG_CHECKED, false);
            if (remembered){
                String email = sharedPreferences.getString(LoginActivity.TAG_EMAIL, "");
                if (!email.equals("")){
                    String userName = email.substring(0, email.indexOf("@"));
                    if(addPayment(userName)){
                        Toast.makeText(this, R.string.dialog_add_payment_success, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else {
                        Toast.makeText(this, R.string.dialog_add_payment_fail, Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, R.string.dialog_add_payment_error, Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, R.string.dialog_add_payment_not_remember, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean addPayment(String userName) {
        if(!inputHopLe()){
            return false;
        }
        Date date = new Date();
        String paymentId = MyFormat.getIdFromDate(date);
        Payment payment = new Payment(paymentId, edtItemName.getText().toString(), Long.parseLong(edtItemPrice.getText().toString()), MyFormat.getDateString(date), userName);
        // Thêm giao dịch
        String pathPayment = "DotGiaoDich/" + currentSeasonId + "/GiaoDich/";
        pathPayment = pathPayment.concat(paymentId);
        MyDbHelper.getInstance().insert(pathPayment, payment);

        // Cập nhật tổng chi tiêu 1 người dùng
        String pathUserTotalPaid = "DotGiaoDich/" + currentSeasonId + "/ThamGia/" + userName;
        dbRef.child(pathUserTotalPaid).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                long totalPaidUser = 0;
                totalPaidUser = task.getResult().getValue(Long.class) != null ? task.getResult().getValue(Long.class) : 0;
                totalPaidUser += payment.getItemPrice();
                MyDbHelper.getInstance().insert(pathUserTotalPaid, totalPaidUser);
            }
        });
        // Cập nhật tổng chi tiêu của cả đợt
        String pathTotalPaid = "DotGiaoDich/" + currentSeasonId + "/totalPaid";
        dbRef.child(pathTotalPaid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                long totalPaid = 0;
                totalPaid = task.getResult().getValue(Long.class) != null ? task.getResult().getValue(Long.class) : 0;
                totalPaid += payment.getItemPrice();
                MyDbHelper.getInstance().insert(pathTotalPaid, totalPaid);
            }
        });
        return true;
    }
    private boolean inputHopLe(){
        String itemName = edtItemName.getText().toString();
        String itemPrice = edtItemPrice.getText().toString();
        if(itemName.equals("")){
            edtItemName.setError(getText(R.string.error_item_name_empty));
            return false;
        }
        if(itemPrice.equals("")){
            edtItemPrice.setError(getText(R.string.error_item_price_empty));
            return false;
        }
        return true;
    }
    private void getCurrentSeasonId(){
        dbRef.child("DotGiaoDich").orderByKey().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for(DataSnapshot d: task.getResult().getChildren()){
                    currentSeasonId = d.getKey();
                }
            }
        });
    }
}
