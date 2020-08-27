package com.project.feeds.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.feeds.R;
import com.project.feeds.entity.User;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText etUname,etConfUname,etPassword,etConfPassword,etMail,etConfMail,etFullname;
    Button btnReg;
    TextView tvHvAcc;
    String userID;

    FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateForm()){
                    return;
                }
                final String uname = etUname.getText().toString().trim();
                String ConfUname = etConfUname.getText().toString().trim();
                final String pass = etPassword.getText().toString();
                String confPw = etConfPassword.getText().toString();
                final String fullNames = etFullname.getText().toString();
                final String mail = etMail.getText().toString().trim();
                String ConfMail = etConfMail.getText().toString().trim();

                mAuth.createUserWithEmailAndPassword(mail,pass)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    user = mAuth.getCurrentUser();
                                    userID = user.getUid();
                                    String urlImages = "https://firebasestorage.googleapis.com/v0/b/feedgram-e410f.appspot.com/o/photos%2F1597399193591.jpg?alt=media&token=eadb6c3a-1279-444d-b286-40090229bd6f";
                                    User userData = new User(userID,uname,mail,"",urlImages,pass,fullNames);
//                                    DocumentReference documentReference = firebaseFirestore.collection("Users").document(userID);
////                                    Map<String,Object> user = new HashMap<>();
////                                    user.put("userName",uname);
////                                    user.put("Email",mail);
////                                    user.put("Password",pass);
////                                    user.put("fullName",fullNames);
//                                    documentReference.set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            Toast.makeText(RegisterActivity.this, "Data tersimpan di database", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });

                                    firebaseFirestore = FirebaseFirestore.getInstance();
                                    firebaseFirestore.collection("users").document(userID).set(userData)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(RegisterActivity.this, "Data Tersimpan di Database", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    onAuthSuccess(task.getResult().getUser());
                                }else{
                                    Toast.makeText(RegisterActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }
            });
        tvHvAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

    }
    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());


        // Go to MainActivity
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }
    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
    private void init(){
        etUname = findViewById(R.id.et_reg_uname);
        etFullname = findViewById(R.id.et_reg_fullname);
        etConfUname = findViewById(R.id.et_reg_conf_uname);
        etPassword = findViewById(R.id.et_reg_password);
        etConfPassword = findViewById(R.id.et_conf_reg_pw);
        etMail = findViewById(R.id.et_reg_email);
        etConfMail = findViewById(R.id.et_reg_conf_email);
        btnReg = findViewById(R.id.btn_layout_regist);
        tvHvAcc = findViewById(R.id.tv_hv_acc);
    }
    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(etMail.getText().toString())) {
            etMail.setError("Required");
            result = false;
        } else {
            etMail.setError(null);
        }

        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            etPassword.setError("Required");
            result = false;
        } else {
            etPassword.setError(null);
        }

        return result;
    }

}