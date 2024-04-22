package com.example.licenta2024;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

        TextInputEditText editTextLastName, editTextFirstName, editTextPhone, editTextEmail, editTextPassword;
        String firstName, lastName, phone, email, password, role;
        TextView textViewError, textViewLogin;
        Button buttonSubmit;
        ProgressBar progressBar;
        ImageView showPassword;
        FirebaseAuth fAuth;
        FirebaseFirestore fStore;
        Boolean exists = false;
        String userID;

        boolean fields = true;
        @SuppressLint("MissingInflatedId")

        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);
            editTextFirstName = findViewById(R.id.firstName);
            editTextLastName = findViewById(R.id.lastName);
            editTextPhone = findViewById(R.id.phone);
            editTextEmail = findViewById(R.id.email);
            editTextPassword = findViewById(R.id.password);
            textViewError = findViewById(R.id.error);
            textViewLogin = findViewById(R.id.loginNow);
            buttonSubmit = findViewById(R.id.submit);
            progressBar = findViewById(R.id.loading);
            showPassword = findViewById(R.id.show_hide_pwd);
            fStore = FirebaseFirestore.getInstance();
            fAuth = FirebaseAuth.getInstance();
            if(fAuth.getCurrentUser()!= null) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

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

            textViewLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                }
            });

            buttonSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    firstName = editTextFirstName.getText().toString();
                    lastName = editTextLastName.getText().toString();
                    phone = editTextPhone.getText().toString();
                    email = editTextEmail.getText().toString();
                    password = editTextPassword.getText().toString();
                    role = "user";

                    byte[] encrypt = new byte[0];
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        encrypt = Base64.getEncoder().encode(password.getBytes());
                    }
                    password = new String(encrypt);

                    if (firstName.length() == 0 || lastName.length() == 0 || phone.length()==0 || email.length()==0 || password.length() == 0) {
                        Toast.makeText(Register.this, "All field are required", Toast.LENGTH_SHORT).show();
                        fields = false;

                    }

                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                                userID = fAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fStore.collection("users").document(userID);
                                Map<String, Object> user = new HashMap<>();
                                user.put("firstName", firstName);
                                user.put("lastName", lastName);
                                user.put("phone", phone);
                                user.put("email", email);
                                user.put("role", "user");
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "User profile created for " + userID);
                                    }
                                });
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
            });
        }

    }
