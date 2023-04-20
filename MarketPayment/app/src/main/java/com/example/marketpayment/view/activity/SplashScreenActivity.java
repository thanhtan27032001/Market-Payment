package com.example.marketpayment.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.marketpayment.model.db_entity.User;
import com.example.marketpayment.model.CurrentUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SplashScreenActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        // Đăng nhập tự động
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean(LoginActivity.TAG_CHECKED, false)){
//            TextView txtSigning = findViewById(R.id.txtSigning);
//            txtSigning.setVisibility(View.VISIBLE);
            Toast.makeText(this, R.string.dialog_signing_in, Toast.LENGTH_SHORT).show();
            login(sharedPreferences.getString(LoginActivity.TAG_EMAIL, ""), sharedPreferences.getString(LoginActivity.TAG_PASSWORD, ""));
        }
        else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void login(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Lưu thông tin người dùng đang sử dụng
                            String userName = email.substring(0, email.indexOf("@"));
                            CurrentUser currentUser = CurrentUser.getInstance();
                            dbRef.child("NguoiDung").child(userName).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()){
                                        User user = task.getResult().getValue(User.class);
                                        currentUser.setUser(user);
                                        // Chuyển sang main activity
                                        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(SplashScreenActivity.this, R.string.dialog_login_user_not_exist, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SplashScreenActivity.this, R.string.dialog_login_fail, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                        }
                    }
                });
    }
}