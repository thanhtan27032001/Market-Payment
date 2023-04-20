package com.example.marketpayment.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marketpayment.controller.MyFormat;
import com.example.marketpayment.model.db_entity.SeasonJoiner;
import com.example.marketpayment.model.db_entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private static final String USER_DEFAULT_ROLE = "normal";

    private FirebaseAuth firebaseAuth;
    private DatabaseReference dbRef;

    private EditText edtNickname;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        Button btnSignup = findViewById(R.id.btnSignUp);
        edtNickname = findViewById(R.id.edtNickname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtPasswordConfirm = findViewById(R.id.edtPasswordConfirm);
        TextView txtReturn = findViewById(R.id.txtReturn);

        btnSignup.setOnClickListener(view -> {
            String nickName = edtNickname.getText().toString();
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();
            String passwordConfirm = edtPasswordConfirm.getText().toString();
            if(inputHopLe(nickName, email, password, passwordConfirm)){
                signUp(nickName, email, password);
            }
        });

        txtReturn.setOnClickListener(view -> {
            finish();
        });
    }

    private void signUp(String nickName, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                // Tạo thông tin người dùng trong database
                String userName = email.substring(0, email.indexOf("@"));
                User user = new User(userName, email, nickName, USER_DEFAULT_ROLE, MyFormat.getArgbColor());
                dbRef.child("NguoiDung").child(userName).setValue(user);
                // Thêm người dùng vào đợt hiện tại
                dbRef.child("DotGiaoDich").get().addOnCompleteListener(task1 -> {
                    if (task1.isComplete()){
                        String currentSeasonId = null;
                        for (DataSnapshot d: task1.getResult().getChildren()){
                            currentSeasonId = d.getKey();
                        }
                        if(currentSeasonId != null){
                            dbRef.child("DotGiaoDich/" + currentSeasonId + "/ThamGia/" + userName).setValue(0);
                        }
                    }
                });

                Toast.makeText(SignUpActivity.this, R.string.dialog_sign_up_successful, Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                Toast.makeText(SignUpActivity.this, R.string.dialog_sign_up_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean inputHopLe(String nickName, String email, String password, String passwordConfirm){
        if(nickName.equals("")){
            edtNickname.setError(getText(R.string.error_nickname_empty));
            return false;
        }
        if(email.equals("")){
            edtEmail.setError(getText(R.string.error_email_empty));
            return false;
        }
        else if(!isEmail(email)) {
            edtEmail.setError(getText(R.string.error_email_invalid));
            return false;
        }
        if(password.equals("")){
            edtPassword.setError(getText(R.string.error_password_empty));
            return false;
        }
        if(!passwordConfirm.equals(password)){
            edtPasswordConfirm.setError(getText(R.string.error_confirm_password_incorrect));
            return false;
        }
        return true;
    }
    private boolean isEmail(String email){
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}