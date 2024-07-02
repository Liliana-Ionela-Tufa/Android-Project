package com.example.licenta2024;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Base64;

public class Login extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword;
    String email, password;
    Button login;
    Typeface font;

    ImageView showPassword;
    TextView goToRegister;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        goToRegister = findViewById(R.id.registerNow);
        login = findViewById(R.id.submit);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        showPassword = findViewById(R.id.show_hide_pwd);

        showPassword.setImageResource(R.drawable.ic_hide_pwd);
        editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTextPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showPassword.setImageResource(R.drawable.ic_show_pwd);
                } else {
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showPassword.setImageResource(R.drawable.ic_hide_pwd);
                }
            }
        });

        goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = editTextEmail.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();

                if (validateFields()) {
                    byte[] encrypt = new byte[0];
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        encrypt = Base64.getEncoder().encode(password.getBytes());
                    }
                    password = new String(encrypt);

                    fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                fStore.collection("users").whereEqualTo("email", email).whereEqualTo("role", "user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (!task.getResult().isEmpty()) {
                                                Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), NewMainActivity.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), AdminMain.class);
                                                startActivity(intent);
                                            }
                                        } else {
                                            Toast.makeText(Login.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(Login.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean validateFields() {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
