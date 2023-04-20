package com.example.marketpayment.controller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketpayment.model.CurrentSeason;
import com.example.marketpayment.model.CurrentUser;
import com.example.marketpayment.model.db_entity.Payment;
import com.example.marketpayment.model.db_entity.User;
import com.example.marketpayment.view.activity.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterPaymentHistory extends RecyclerView.Adapter<AdapterPaymentHistory.ViewHolder> {
    private final Context context;
    private final ArrayList<Payment> paymentArrayList;
    private final HashMap<String, User> joinerHashMap;
    // Popup menu

    public AdapterPaymentHistory(Context context, ArrayList<Payment> paymentArrayList, HashMap<String, User> joinerHashMap) {
        this.context = context;
        this.paymentArrayList = paymentArrayList;
        this.joinerHashMap = joinerHashMap;
    }

    private void openDelete(Payment payment, int position){
        // Init view
        View view2 = LayoutInflater.from(context).inflate(R.layout.dialog_alert, null, false);
        TextView txtTitle = view2.findViewById(R.id.txtTitle);
        TextView txtContent = view2.findViewById(R.id.txtContent);
        Button btnNegative = view2.findViewById(R.id.btnNegative);
        Button btnPositive = view2.findViewById(R.id.btnPositive);
        txtTitle.setText("Delete payment");
        txtContent.setText("Are you sure to delete this payment?\nYou can not restore after delete.");
        // Init dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view2);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        // Sự kiện
        btnNegative.setOnClickListener(view3 -> alertDialog.dismiss());
        btnPositive.setOnClickListener(view3 -> {
            deletePayment(payment, position);
            alertDialog.dismiss();
        });
    }

    private void deletePayment(Payment payment, int position){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("DotGiaoDich/" + CurrentSeason.getInstance().getSeason().getId() + "/GiaoDich/" + payment.getId()).removeValue((error, ref) -> { // Xóa giao dịch
            if (error == null){
                // Cập nhật danh sách giao dịch
                paymentArrayList.remove(payment);
                this.notifyItemRemoved(position);
                // Cập nhật tổng chi tiêu
                dbRef.child("DotGiaoDich/" + CurrentSeason.getInstance().getSeason().getId() + "/totalPaid").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        dbRef.child("DotGiaoDich/" + CurrentSeason.getInstance().getSeason().getId() + "/totalPaid").setValue(task.getResult().getValue(Long.class) - payment.getItemPrice());
                        CurrentSeason.getInstance().getSeason().setTotalPaid(task.getResult().getValue(Long.class) - payment.getItemPrice());
                    }
                });
                // Cập nhật chi tiêu cá nhân
                dbRef.child("DotGiaoDich/" + CurrentSeason.getInstance().getSeason().getId() + "/ThamGia/" + payment.getUserName()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        long newPaid = task.getResult().getValue(Long.class) - payment.getItemPrice();
                        dbRef.child("DotGiaoDich/" + CurrentSeason.getInstance().getSeason().getId() + "/ThamGia/" + payment.getUserName()).setValue(newPaid);
                    }
                });
                Toast.makeText(context, "Delete payment successful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDetail(Payment payment, int position){
        // Init view
        View view;
        Button btnEdit = null;
        if (CurrentUser.getInstance().getUser().getUserName().equals(payment.getUserName())){
            view = LayoutInflater.from(context).inflate(R.layout.dialog_detail, null, false);
            btnEdit = view.findViewById(R.id.btnEdit);
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.dialog_detail_read_only, null, false);
        }
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
        // Gán sự kiện
        if (btnEdit != null){
            btnEdit.setOnClickListener(view1 -> {
                openEdit(payment, position);
                alertDialog.dismiss();
            });
        }
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
    private void openEdit(Payment payment, int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_payment_edit, null, false);
        EditText edtItemName = view.findViewById(R.id.edtItemName);
        EditText edtItemPrice = view.findViewById(R.id.edtItemPrice);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);
        edtItemName.setText(payment.getItemName());
        edtItemPrice.setText(String.valueOf(payment.getItemPrice()));
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnSubmit.setOnClickListener(view1 -> {
            if(inputHopLe(edtItemName, edtItemPrice)){
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
                String newItemName = edtItemName.getText().toString();
                long newItemPrice = Long.parseLong(edtItemPrice.getText().toString());
                long difference = newItemPrice - payment.getItemPrice();
                // Cập nhật giao dịch
                dbRef.child("DotGiaoDich/" + CurrentSeason.getInstance().getSeason().getId() + "/GiaoDich/" + payment.getId() + "/itemName").setValue(newItemName);
                dbRef.child("DotGiaoDich/" + CurrentSeason.getInstance().getSeason().getId() + "/GiaoDich/" + payment.getId() + "/itemPrice").setValue(newItemPrice);
                // Cập nhật tổng chi tiêu
                dbRef.child("DotGiaoDich/" + CurrentSeason.getInstance().getSeason().getId() + "/totalPaid").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        dbRef.child("DotGiaoDich/" + CurrentSeason.getInstance().getSeason().getId() + "/totalPaid").setValue((long)task.getResult().getValue(Long.class) + difference);
                        CurrentSeason.getInstance().getSeason().setTotalPaid((long)task.getResult().getValue(Long.class) + difference);
                    }
                });
                // Cập nhật chi tiêu cá nhân
                dbRef.child("DotGiaoDich/" + CurrentSeason.getInstance().getSeason().getId() + "/ThamGia/" + payment.getUserName()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        dbRef.child("DotGiaoDich/" + CurrentSeason.getInstance().getSeason().getId() + "/ThamGia/" + payment.getUserName()).setValue((long)task.getResult().getValue(Long.class) + difference);
                    }
                });
                // Cập nhật giao dịch
                payment.setItemName(newItemName);
                payment.setItemPrice(newItemPrice);
                AdapterPaymentHistory.this.notifyItemChanged(position);
                alertDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(view1 -> alertDialog.dismiss());
        alertDialog.show();
    }

    private boolean inputHopLe(EditText edtItemName, EditText edtItemPrice){
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
        User user  = joinerHashMap.get(payment.getUserName());
        holder.txtFirstLetter.setText(payment.getUserName().substring(0, 1).toUpperCase());
        holder.txtFirstLetter.setBackgroundColor((int) joinerHashMap.get(user.getUserName()).getAvtColor());
        holder.txtFirstLetter.setTextColor(MyFormat.isColorDark((int) joinerHashMap.get(user.getUserName()).getAvtColor()) ? Color.WHITE : Color.BLACK);
        holder.txtItemName.setText(payment.getItemName());
        holder.txtUserNickname.setText(payment.getUserName());
        holder.txtItemPrice.setText(MyFormat.getCurrency(payment.getItemPrice()));
        holder.itemPaymentHistory.setOnClickListener(view -> openDetail(payment, holder.getLayoutPosition()));
        holder.itemPaymentHistory.setOnLongClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, view);
            if(CurrentUser.getInstance().getUser().getUserName().equals(payment.getUserName())){
                popupMenu.inflate(R.menu.menu_popup_payment);
            }
            else {
                popupMenu.inflate(R.menu.menu_popup_payment_read_only);
            }
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()){
                    case R.id.detail:
                        openDetail(payment, holder.getLayoutPosition());
                        break;
                    case R.id.edit:
                        openEdit(payment, holder.getLayoutPosition());
                        break;
                    case R.id.delete:
                        openDelete(payment, holder.getLayoutPosition());
                        break;
                }
                return false;
            });
            return false;
        });
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
