package com.example.marketpayment.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.marketpayment.model.CurrentUser;
import com.example.marketpayment.model.db_entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG_EMAIL = "email";
    public static final String TAG_PASSWORD = "password";
    public static final String TAG_CHECKED = "checked";
    public static final String TAG_USER_NAME = "userName";

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private Button btnLogin;
    private Button btnSigUp;
    private EditText edtEmail;
    private EditText edtPassword;
    private CheckBox cbRemember;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        btnLogin = findViewById(R.id.btnLogin);
        btnSigUp = findViewById(R.id.btnSignUp);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        cbRemember = findViewById(R.id.cbRemember);

        // Remember me
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        cbRemember.setChecked(sharedPreferences.getBoolean(TAG_CHECKED, false));
        if(cbRemember.isChecked()){
            edtEmail.setText(sharedPreferences.getString(TAG_EMAIL, ""));
            edtPassword.setText(sharedPreferences.getString(TAG_PASSWORD, ""));
        }

        // Nút Login
        btnLogin.setOnClickListener(v -> {
            String email = "";
            String password = "";
            email = email + edtEmail.getText().toString();
            password = password + edtPassword.getText().toString();
            if(!email.equals("") && !password.equals("")){
                login(email, password);
            }
        });

        // Nút Sign up
        btnSigUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void login(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        // Lưu thông tin đăng nhập
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String userName = email.substring(0, email.indexOf("@"));
                        editor.putString(TAG_USER_NAME, userName);
                        if(cbRemember.isChecked()){
                            editor.putString(TAG_EMAIL, email);
                            editor.putString(TAG_PASSWORD, password);
                            editor.putBoolean(TAG_CHECKED, true);
                            editor.apply();
                        }
                        else if(!cbRemember.isChecked()){
                            editor.remove(TAG_EMAIL);
                            editor.remove(TAG_PASSWORD);
                            editor.remove(TAG_CHECKED);
                            editor.apply();
                        }
                        // Lưu thông tin người dùng đang sử dụng
                        CurrentUser currentUser = CurrentUser.getInstance();
                        dbRef.child("NguoiDung").child(userName).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                User user = task1.getResult().getValue(User.class);
                                currentUser.setUser(user);
                                // Chuyển sang main activity
//                                        getCurrentSeason();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(LoginActivity.this, R.string.dialog_login_user_not_exist, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, R.string.dialog_login_user_not_exist, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
