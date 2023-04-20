package com.example.marketpayment.controller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketpayment.model.CurrentUser;
import com.example.marketpayment.model.db_entity.Payment;
import com.example.marketpayment.model.db_entity.User;
import com.example.marketpayment.view.activity.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterPaymentHistoryOlder extends RecyclerView.Adapter<AdapterPaymentHistoryOlder.ViewHolder> {
    private final Context context;
    private final ArrayList<Payment> paymentArrayList;
    private HashMap<String, User> userHashMap = new HashMap<>();

    public AdapterPaymentHistoryOlder(Context context, ArrayList<Payment> paymentArrayList) {
        this.context = context;
        this.paymentArrayList = paymentArrayList;
        getUser();
    }

    public void getUser (){
        FirebaseDatabase.getInstance().getReference().child("NguoiDung").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (DataSnapshot d: task.getResult().getChildren()){
                    User user = d.getValue(User.class);
                    if (user != null){
                        userHashMap.put(user.getUserName(), user);
                    }
                }
            }
        });
    }
    private void openDetail(Payment payment, int position){
        // Init view
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_detail_read_only, null, false);
        ImageView imgWall = view.findViewById(R.id.imgWall);
        TextView txtItemName = view.findViewById(R.id.txtItemName);
        TextView txtItemPrice = view.findViewById(R.id.txtItemPrice);
        TextView txtNickname = view.findViewById(R.id.txtNickname);
        TextView txtPaymentId = view.findViewById(R.id.txtPaymentId);
        TextView txtDateAdded = view.findViewById(R.id.txtDateAdded);
        Button btnOk = view.findViewById(R.id.btnOk);
        setImgWall(imgWall, payment.getItemName());
        txtItemName.setText(payment.getItemName());
        txtItemPrice.setText(MyFormat.getCurrency(payment.getItemPrice()));
        txtNickname.setText(payment.getUserName());
        txtPaymentId.setText(payment.getId());
        txtDateAdded.setText(payment.getInsertDate());
        // Init dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Set sự kiện
        btnOk.setOnClickListener(view1 -> alertDialog.dismiss());
        alertDialog.show();
    }

    private void setImgWall(ImageView img, String itemName){
        if (itemName.startsWith("Đi chợ")){
            img.setImageResource(R.drawable.img_market);
        }
        else if (itemName.startsWith("Nước")){
            img.setImageResource(R.drawable.img_water);
        }
        else if (itemName.startsWith("Gạo")){
            img.setImageResource(R.drawable.img_rice);
        }
        else if (itemName.startsWith("Cơm")){
            img.setImageResource(R.drawable.img_com_tam);
        }
        else {
            img.setImageResource(R.drawable.img_other);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = new Space(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_payment_history, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == 0) {
            return;
        }
        Payment payment = paymentArrayList.get(position);
        User user = userHashMap.get(payment.getUserName());
        holder.txtFirstLetter.setText(payment.getUserName().substring(0, 1).toUpperCase());
        holder.txtFirstLetter.setBackgroundColor((int)userHashMap.get(user.getUserName()).getAvtColor());
        holder.txtFirstLetter.setTextColor(MyFormat.isColorDark((int)userHashMap.get(user.getUserName()).getAvtColor()) ? Color.WHITE : Color.BLACK);
        holder.txtItemName.setText(payment.getItemName());
        holder.txtUserNickname.setText(payment.getUserName());
        holder.txtItemPrice.setText(MyFormat.getCurrency(payment.getItemPrice()));
        holder.itemPaymentHistory.setOnClickListener(view -> openDetail(payment, holder.getLayoutPosition()));
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return paymentArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtFirstLetter;
        private TextView txtItemName;
        private TextView txtUserNickname;
        private TextView txtItemPrice;
        private LinearLayout itemPaymentHistory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFirstLetter = itemView.findViewById(R.id.txtFirstLetter);
            txtItemName = itemView.findViewById(R.id.txtHistoryItemName);
            txtUserNickname = itemView.findViewById(R.id.txtLoginNickname);
            txtItemPrice = itemView.findViewById(R.id.txtHistoryItemPrice);
            itemPaymentHistory = itemView.findViewById(R.id.itemPaymentHistory);
        }
    }
}
