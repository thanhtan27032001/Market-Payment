package com.example.marketpayment.view.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.marketpayment.controller.MyFormat;
import com.example.marketpayment.model.CurrentSeason;
import com.example.marketpayment.model.CurrentUser;
import com.example.marketpayment.model.db_entity.Season;
import com.example.marketpayment.model.db_entity.SeasonJoiner;
import com.example.marketpayment.model.db_entity.User;
import com.example.marketpayment.view.activity.LoginActivity;
import com.example.marketpayment.view.activity.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SettingFragment extends Fragment {
    private DatabaseReference dbRef;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;

    private TextView txtAvatar, txtNickname, txtEmail;
    private AppCompatButton btnPickAvtColor, btnChangeNickname, btnChangePassword, btnFeedback, btnNewSeason, btnSignOut;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbRef = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_setting, container, false);
        // Thông tin user login
        txtAvatar = view.findViewById(R.id.txtAvatar);
        txtNickname = view.findViewById(R.id.txtNickname);
        txtEmail = view.findViewById(R.id.txtEmail);
        loadUI();
        // Các nút
        btnPickAvtColor = view.findViewById(R.id.btnPickAvtColor);
        btnPickAvtColor.setOnClickListener(view1 -> pickColor());
        btnChangeNickname = view.findViewById(R.id.btnChangeNickname);
        btnChangeNickname.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View view2 = LayoutInflater.from(getContext()).inflate(R.layout.dialog_nickname_change, null, false);
            EditText edtNewNickName = view2.findViewById(R.id.edtNewNickname);
            Button btnCancel = view2.findViewById(R.id.btnCancel);
            Button btnSubmit = view2.findViewById(R.id.btnSubmit);
            builder.setView(view2);
            AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            btnCancel.setOnClickListener(view3 -> alertDialog.dismiss());
            btnSubmit.setOnClickListener(view3 -> {
                String newNickName = edtNewNickName.getText().toString();
                if (!newNickName.equals("")){
                    dbRef.child("NguoiDung/" + CurrentUser.getInstance().getUser().getUserName() + "/nickName").setValue(newNickName);
                    CurrentUser.getInstance().getUser().setNickName(newNickName);
                    loadUI();
                    alertDialog.dismiss();
                    Toast.makeText(getContext(), R.string.dialog_change_nickname_successful, Toast.LENGTH_SHORT).show();
                }
                else {
                    edtNewNickName.setError(getText(R.string.error_nickname_at_minimum_length));
                }
            });
            alertDialog.show();
        });
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(view1 -> {
            // Init view
            View view2 = LayoutInflater.from(getContext()).inflate(R.layout.dialog_password_change, null, false);
            EditText edtPassword = view2.findViewById(R.id.edtPassword);
            EditText edtPasswordConfirm = view2.findViewById(R.id.edtPasswordConfirm);
            Button btnCancel = view2.findViewById(R.id.btnCancel);
            Button btnSubmit = view2.findViewById(R.id.btnSubmit);
            // Init dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setView(view2);
            AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // Event
            btnCancel.setOnClickListener(view3 -> alertDialog.dismiss());
            btnSubmit.setOnClickListener(view3 -> {
                if(changePassword(edtPassword, edtPasswordConfirm)){
                    sharedPreferences.edit().putString(LoginActivity.TAG_PASSWORD, edtPassword.getText().toString()).apply();
                    alertDialog.dismiss();
                    Toast.makeText(getContext(), R.string.dialog_change_password_successful, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(), R.string.dialog_change_password_fail, Toast.LENGTH_SHORT).show();
                }
            });
            alertDialog.show();
        });
        btnFeedback = view.findViewById(R.id.btnGiveFeedback);
        btnFeedback.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:thanhtan27032001@gmail.com"));
//            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"thanhtan27032001@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback to Tân vippro");
            intent.putExtra(Intent.EXTRA_TEXT, "Nice app!");
            try {
                startActivity(intent);
            }
            catch (Exception e){
                Toast.makeText(getContext(), R.string.error_send_email, Toast.LENGTH_SHORT).show();
            }
        });
        btnNewSeason = view.findViewById(R.id.btnNewSeason);
        btnNewSeason.setOnClickListener(view1 -> {
            // Init view
            View view2 = LayoutInflater.from(getContext()).inflate(R.layout.dialog_alert, null, false);
            TextView txtTitle = view2.findViewById(R.id.txtTitle);
            TextView txtContent = view2.findViewById(R.id.txtContent);
            Button btnNegative = view2.findViewById(R.id.btnNegative);
            Button btnPositive = view2.findViewById(R.id.btnPositive);
            txtTitle.setText(R.string.start_new_season_title);
            txtContent.setText(R.string.dialog_body_start_new_season);
            // Init dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setView(view2);
            AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
            // Sự kiện
            btnNegative.setOnClickListener(view3 -> alertDialog.dismiss());
            btnPositive.setOnClickListener(view3 -> {
                createNewSeason();
                alertDialog.dismiss();
            });
        });
        btnSignOut = view.findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(view1 -> signOut(false));
        return view;
    }

    private boolean inputHopLe(EditText edtPassword, EditText edtPasswordConfirm){
        String password = edtPassword.getText().toString();
        String passwordConfirm = edtPasswordConfirm.getText().toString();
        if (password.equals("")){
            edtPassword.setError(getText(R.string.error_password_at_minimum_length));
            return false;
        }
        if (!password.equals(passwordConfirm)){
            edtPasswordConfirm.setError(getText(R.string.error_confirm_password_incorrect));
            return false;
        }
        return true;
    }
    private boolean changePassword(EditText edtPassword, EditText edtPasswordConfirm){
        if (inputHopLe(edtPassword, edtPasswordConfirm)){
            String password = edtPassword.getText().toString();
            firebaseAuth.getCurrentUser().updatePassword(password);
            return true;
        }
        else {
            return false;
        }
    }

    private void clearFocusEdt(EditText[] editTexts){
        for (EditText e: editTexts){
            e.clearFocus();
        }
    }
    private void pickColor(){
        // Init views and dialod
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_pick_color, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        TextView txtAvt = view.findViewById(R.id.imgColorExample);
        SeekBar seekbarRed = view.findViewById(R.id.seekbarRed);
        SeekBar seekBarGreen = view.findViewById(R.id.seekbarGreen);
        SeekBar seekBarBlue = view.findViewById(R.id.seekbarBlue);
        EditText edtRed = view.findViewById(R.id.edtRed);
        EditText edtGreen = view.findViewById(R.id.edtGreen);
        EditText edtBlue = view.findViewById(R.id.edtBlue);
        EditText edtHex = view.findViewById(R.id.edtHex);
        EditText[] editTexts = new EditText[]{edtRed, edtGreen, edtBlue, edtHex};
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnPick = view.findViewById(R.id.btnPick);
        // Set event views
        seekbarRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(!edtRed.isFocused()){
                    edtRed.setText(String.valueOf(i));
                }
                if (!edtHex.isFocused()){
                    edtHex.setText(MyFormat.getHexColor(
                            edtRed.getText().toString(),
                            edtGreen.getText().toString(),
                            edtBlue.getText().toString()));
                }
                try {
                    int color = MyFormat.getArgbColor(edtRed.getText().toString(), edtGreen.getText().toString(), edtBlue.getText().toString());
                    txtAvt.setBackgroundColor(color);
                    txtAvt.setTextColor(MyFormat.isColorDark(color) ? Color.WHITE : Color.BLACK);
                }
                catch (Exception e){
                    Log.e("ERROR COLOR PICKER", e.getMessage());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                clearFocusEdt(editTexts);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(!edtGreen.isFocused()){
                    edtGreen.setText(String.valueOf(i));
                }
                if (!edtHex.isFocused()){
                    edtHex.setText(MyFormat.getHexColor(
                            edtRed.getText().toString(),
                            edtGreen.getText().toString(),
                            edtBlue.getText().toString()));
                }
                try {
                    int color = MyFormat.getArgbColor(edtRed.getText().toString(), edtGreen.getText().toString(), edtBlue.getText().toString());
                    txtAvt.setBackgroundColor(color);
                    txtAvt.setTextColor(MyFormat.isColorDark(color) ? Color.WHITE : Color.BLACK);
                }
                catch (Exception e){
                    Log.e("ERROR COLOR PICKER", e.getMessage());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                clearFocusEdt(editTexts);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(!edtBlue.isFocused()){
                    edtBlue.setText(String.valueOf(i));
                }
                if (!edtHex.isFocused()){
                    edtHex.setText(MyFormat.getHexColor(
                            edtRed.getText().toString(),
                            edtGreen.getText().toString(),
                            edtBlue.getText().toString()));
                }
                try {
                    int color = MyFormat.getArgbColor(edtRed.getText().toString(), edtGreen.getText().toString(), edtBlue.getText().toString());
                    txtAvt.setBackgroundColor(color);
                    txtAvt.setTextColor(MyFormat.isColorDark(color) ? Color.WHITE : Color.BLACK);
                }
                catch (Exception e){
                    Log.e("ERROR COLOR PICKER", e.getMessage());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                clearFocusEdt(editTexts);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        edtRed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtRed.getText().toString().equals("")){
                    edtRed.setText("0");
                }
                else if (Integer.parseInt(edtRed.getText().toString()) > 255){
                    edtRed.setText("255");
                }
                try {
                    int color = MyFormat.getArgbColor(edtRed.getText().toString(), edtGreen.getText().toString(), edtBlue.getText().toString());
                    txtAvt.setBackgroundColor(color);
                    txtAvt.setTextColor(MyFormat.isColorDark(color) ? Color.WHITE : Color.BLACK);
                    seekbarRed.setProgress(Integer.parseInt(edtRed.getText().toString()));
                }
                catch (Exception e){
                    Log.e("ERROR COLOR PICKER", e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtGreen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtGreen.getText().toString().equals("")){
                    edtGreen.setText("0");
                }
                else if (Integer.parseInt(edtGreen.getText().toString()) > 255){
                    edtGreen.setText("255");
                }
                try {
                    int color = MyFormat.getArgbColor(edtRed.getText().toString(), edtGreen.getText().toString(), edtBlue.getText().toString());
                    txtAvt.setBackgroundColor(color);
                    txtAvt.setTextColor(MyFormat.isColorDark(color) ? Color.WHITE : Color.BLACK);
                    seekbarRed.setProgress(Integer.parseInt(edtRed.getText().toString()));
                }
                catch (Exception e){
                    Log.e("ERROR COLOR PICKER", e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtBlue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtBlue.getText().toString().equals("")){
                    edtBlue.setText("0");
                }
                else if (Integer.parseInt(edtBlue.getText().toString()) > 255){
                    edtBlue.setText("255");
                }
                try {
                    int color = MyFormat.getArgbColor(edtRed.getText().toString(), edtGreen.getText().toString(), edtBlue.getText().toString());
                    txtAvt.setBackgroundColor(color);
                    txtAvt.setTextColor(MyFormat.isColorDark(color) ? Color.WHITE : Color.BLACK);
                    seekbarRed.setProgress(Integer.parseInt(edtRed.getText().toString()));
                }
                catch (Exception e){
                    Log.e("ERROR COLOR PICKER", e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtHex.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String regex = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
                Pattern p = Pattern.compile(regex);
                if (p.matcher(charSequence).matches()){
                    try {
                        int color = Color.parseColor(charSequence.toString());
                        txtAvt.setBackgroundColor(color);
                        txtAvt.setTextColor(MyFormat.isColorDark(color) ? Color.WHITE : Color.BLACK);
                        seekbarRed.setProgress(Color.red(color));
                        seekBarGreen.setProgress(Color.green(color));
                        seekBarBlue.setProgress(Color.blue(color));
                    }
                    catch (Exception e){
                        Log.e("ERROR COLOR PICKER", e.getMessage());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnCancel.setOnClickListener(view1 -> alertDialog.dismiss());
        btnPick.setOnClickListener(view1 -> {
            long color = MyFormat.getArgbColor(edtRed.getText().toString(), edtGreen.getText().toString(), edtBlue.getText().toString());
            dbRef.child("NguoiDung/" + CurrentUser.getInstance().getUser().getUserName() + "/avtColor").setValue(color);
            CurrentUser.getInstance().getUser().setAvtColor(color);
            txtAvatar.setBackgroundColor((int)CurrentUser.getInstance().getUser().getAvtColor());
            txtAvatar.setTextColor(MyFormat.isColorDark((int)CurrentUser.getInstance().getUser().getAvtColor()) ? Color.WHITE : Color.BLACK);
            Toast.makeText(getContext(), R.string.dialog_pick_color_successful, Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        });
        // Set giá trị
        seekbarRed.setProgress(Color.red((int)CurrentUser.getInstance().getUser().getAvtColor()));
        seekBarGreen.setProgress(Color.green((int)CurrentUser.getInstance().getUser().getAvtColor()));
        seekBarBlue.setProgress(Color.blue((int)CurrentUser.getInstance().getUser().getAvtColor()));
        txtAvt.setTextColor(MyFormat.isColorDark((int)CurrentUser.getInstance().getUser().getAvtColor()) ? Color.WHITE : Color.BLACK);
        txtAvt.setText(CurrentUser.getInstance().getUser().getNickName().substring(0, 1).toUpperCase());
        // Show dialog
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    private void createNewSeason() {
        // Kết thúc season hiện tại bằng cách set value finisher và ngày kết thúc
        dbRef.child("DotGiaoDich/" + CurrentSeason.getInstance().getSeason().getId()).child("finisher").setValue(CurrentUser.getInstance().getUser().getUserName());
        dbRef.child("DotGiaoDich/" + CurrentSeason.getInstance().getSeason().getId()).child("dateEnd").setValue(MyFormat.getCurrentDateString());
        // Tạo season mới
        String newSeasonId = String.valueOf(Integer.parseInt(CurrentSeason.getInstance().getSeason().getId())+1);
        dbRef.child("DotGiaoDich").child(newSeasonId).setValue(new Season(newSeasonId));
        // Add tất cả người dùng hiện có vào season mới
        dbRef.child("NguoiDung").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (DataSnapshot d: task.getResult().getChildren()){
                    User user = d.getValue(User.class);
                    dbRef.child("DotGiaoDich/" + newSeasonId + "/ThamGia").child(user.getUserName()).setValue(0);
                }
            }
        });
        // Đăng xuất
        Toast.makeText(getContext(), R.string.dialog_start_new_season_successful, Toast.LENGTH_SHORT).show();
        signOut(true);
    }

    private void signOut(boolean remember){
        if(!remember){
            sharedPreferences.edit().remove(LoginActivity.TAG_CHECKED).apply();
        }
        firebaseAuth.signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }

    private void loadUI(){
        CurrentUser currentUser = CurrentUser.getInstance();
        txtAvatar.setText(currentUser.getUser().getNickName().substring(0, 1).toUpperCase());
        txtAvatar.setBackgroundColor((int)currentUser.getUser().getAvtColor());
        txtAvatar.setTextColor(MyFormat.isColorDark((int)currentUser.getUser().getAvtColor()) ? Color.WHITE : Color.BLACK);
        txtNickname.setText(currentUser.getUser().getNickName());
        txtEmail.setText(currentUser.getUser().getEmail());
    }
}
