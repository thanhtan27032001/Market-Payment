package com.example.marketpayment.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.marketpayment.controller.AdapterCategory;
import com.example.marketpayment.controller.MyDbHelper;
import com.example.marketpayment.model.db_entity.ItemCategory;
import com.example.marketpayment.model.CurrentSeason;
import com.example.marketpayment.model.db_entity.Payment;
import com.example.marketpayment.model.db_entity.Season;
import com.example.marketpayment.controller.MyFormat;
import com.example.marketpayment.model.CurrentUser;
import com.example.marketpayment.controller.AdapterViewPager;
import com.example.marketpayment.view.fragment.CalculatorFragment;
import com.example.marketpayment.view.fragment.HistoryFragment;
import com.example.marketpayment.view.fragment.HomeFragment;
import com.example.marketpayment.view.fragment.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference dbRef;

    private AdapterCategory adapterCategory;
    private ArrayList<ItemCategory> itemCategoryArrayList;

    private BottomNavigationView bottomNavigationView;
    private AlertDialog myDialog;
    private EditText edtItemName;
    private EditText edtItemPrice;
    private Button btnCancel;
    private Button btnSubmit;
    private Spinner itemSpinner;

    private HomeFragment homeFragment;
    private HistoryFragment historyFragment;
    private CalculatorFragment calculatorFragment;
    private SettingFragment settingFragment;

    private String currentSeasonId;
    private Season currentSeason;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbRef = FirebaseDatabase.getInstance().getReference();
        //
        bottomNavigationView = findViewById(R.id.bottomNavBarView);
        bottomNavigationView.setBackground(null);
        // Tạo alert dialog thêm chi tiêu
        createAlertDialogAddPayment();
        // Sự kiện click btnAdd chi tiêu
        FloatingActionButton btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(view -> myDialog.show());
        getCurrentSeason();
//        startService(new Intent(this, NotificationReceiverService.class));
    }
    private void getCurrentSeason(){
        dbRef.child("DotGiaoDich").orderByKey().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for(DataSnapshot d: task.getResult().getChildren()){
                    currentSeasonId = d.getKey();
                }
                dbRef.child("DotGiaoDich").child(currentSeasonId).get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        currentSeason = task1.getResult().getValue(Season.class);
                        CurrentSeason.getInstance().setSeason(currentSeason);
                        createFragment();
                    }
                });
            }
        });
    }
    private boolean addPayment() {
        if(!inputHopLe()){
            return false;
        }
        Season currentSeason = CurrentSeason.getInstance().getSeason();
        Date date = new Date();
        String paymentId = MyFormat.getIdFromDate(date);
        Payment payment = new Payment(paymentId, edtItemName.getText().toString(), Long.parseLong(edtItemPrice.getText().toString()), MyFormat.getDateString(date), CurrentUser.getInstance().getUser().getUserName());
        // Thêm giao dịch
        String pathPayment = "DotGiaoDich/" + currentSeason.getId() + "/GiaoDich/";
        pathPayment = pathPayment.concat(paymentId);
        MyDbHelper.getInstance().insert(pathPayment, payment);

        // Cập nhật tổng chi tiêu 1 người dùng
        String pathUserTotalPaid = "DotGiaoDich/" + currentSeason.getId() + "/ThamGia/" + CurrentUser.getInstance().getUser().getUserName();
        dbRef.child(pathUserTotalPaid).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                long totalPaidUser = 0;
                totalPaidUser = task.getResult().getValue(Long.class) != null ? task.getResult().getValue(Long.class) : 0;
                totalPaidUser += payment.getItemPrice();
                MyDbHelper.getInstance().insert(pathUserTotalPaid, totalPaidUser);
            }
        });
        // Cập nhật tổng chi tiêu của cả đợt
        String pathTotalPaid = "DotGiaoDich/" + currentSeason.getId() + "/totalPaid";
        dbRef.child(pathTotalPaid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                long totalPaid = 0;
                totalPaid = task.getResult().getValue(Long.class) != null ? task.getResult().getValue(Long.class) : 0;
                totalPaid += payment.getItemPrice();
                MyDbHelper.getInstance().insert(pathTotalPaid, totalPaid);
            }
        });
        // Refresh dữ liệu season
        currentSeason.setTotalPaid(currentSeason.getTotalPaid() + payment.getItemPrice());
        // Refresh lịch sử giao dịch
        historyFragment.addPayment(payment);
        return true;
    }
    private boolean inputHopLe(){
        String itemName = edtItemName.getText().toString();
        String itemPrice = edtItemPrice.getText().toString();
        if(itemName.equals("")){
            edtItemName.setError("Tên vật phẩm không được trống");
            return false;
        }
        if(itemPrice.equals("")){
            edtItemPrice.setError("Giá vật phẩm không được trống");
            return false;
        }
        return true;
    }
    private void resetSpinner(){
        itemSpinner.setSelection(0);
    }
    private void createAlertDialogAddPayment(){
        // alert dialog thêm chi tiêu và sự kiện click
        View view = getLayoutInflater().inflate(R.layout.dialog_payment_add, null);
        edtItemName = view.findViewById(R.id.edtItemName);
        edtItemPrice = view.findViewById(R.id.edtItemPrice);
        itemSpinner = view.findViewById(R.id.itemSpinner);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        myDialog = builder.create();
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnCancel.setOnClickListener(view1 -> {
            resetSpinner();
            myDialog.cancel();
        });
        btnSubmit.setOnClickListener(view1 -> {
            if(addPayment()){
                Toast.makeText(this, R.string.dialog_add_payment_success, Toast.LENGTH_SHORT).show();
                resetSpinner();
                myDialog.cancel();
            }
        });
        itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ItemCategory item = itemCategoryArrayList.get(position);
                if(!item.getTenVatPham().equals("Khác")){
                    edtItemName.setText(item.getTenVatPham());
                    edtItemPrice.setText(String.valueOf(item.getGiaTien()));
                }
                else {
                    edtItemName.setText("");
                    edtItemPrice.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Dữ liệu vật phẩm trong spinner
        itemCategoryArrayList = new ArrayList<>();
        dbRef.child("VatPham").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (DataSnapshot d: task.getResult().getChildren()){
                    ItemCategory item = d.getValue(ItemCategory.class);
                    if(item != null){
                        itemCategoryArrayList.add(item);
                    }
                }
                adapterCategory = new AdapterCategory(MainActivity.this, R.layout.item_selected, itemCategoryArrayList);
                itemSpinner.setAdapter(adapterCategory);
            }
        });
    }
    private void createFragment(){
        homeFragment = new HomeFragment();
        historyFragment = new HistoryFragment();
        calculatorFragment = new CalculatorFragment();
        settingFragment = new SettingFragment();
        // Setup viewpager hiển thị các fragment
        AdapterViewPager adapterViewPager = new AdapterViewPager(getSupportFragmentManager(), new Fragment[]{homeFragment, historyFragment, calculatorFragment, settingFragment});
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapterViewPager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(0);
        // Set background bottomNavigationView bằng null
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // Sử dụng BottomNavigationView + ViewPager
            switch (item.getItemId()){
                case R.id.home:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.history:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.calculator:
                    viewPager.setCurrentItem(2);
                    break;
                case R.id.options:
                    viewPager.setCurrentItem(3);
                    break;
            }
            return true;
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.history).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.calculator).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.options).setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}