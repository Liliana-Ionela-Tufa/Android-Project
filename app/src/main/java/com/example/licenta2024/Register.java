package com.example.licenta2024;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    TextInputEditText editTextLastName, editTextFirstName, editTextPhone, editTextEmail, editTextPassword, editTextConfirmPassword;
    String firstName, lastName, phone, email, password, confirmPassword, role;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextFirstName = findViewById(R.id.firstName);
        editTextLastName = findViewById(R.id.lastName);
        editTextPhone = findViewById(R.id.phone);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextConfirmPassword = findViewById(R.id.confirmPassword);
        textViewError = findViewById(R.id.error);
        textViewLogin = findViewById(R.id.loginNow);
        buttonSubmit = findViewById(R.id.submit);
        progressBar = findViewById(R.id.loading);
        showPassword = findViewById(R.id.show_hide_pwd);
       // showConfirmPassword = findViewById(R.id.show_hide_confirm_pwd);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        showPassword.setImageResource(R.drawable.ic_hide_pwd);
        //showConfirmPassword.setImageResource(R.drawable.ic_hide_pwd);
        editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        editTextConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTextPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    editTextConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showPassword.setImageResource(R.drawable.ic_show_pwd);
                } else {
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    editTextConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
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

                firstName = editTextFirstName.getText().toString().trim();
                lastName = editTextLastName.getText().toString().trim();
                phone = editTextPhone.getText().toString().trim();
                email = editTextEmail.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();
                confirmPassword = editTextConfirmPassword.getText().toString().trim();
                role = "user";

                if (validateFields()) {
                    byte[] encrypt = new byte[0];
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        encrypt = Base64.getEncoder().encode(password.getBytes());
                    }
                    password = new String(encrypt);

                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                                userID = fAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fStore.collection("users").document(userID);
                                Map<String, Object> user = new HashMap<>();
                                user.put("firstName", firstName);
                                user.put("lastName", lastName);
                                user.put("phone", phone);
                                user.put("email", email);
                                user.put("role", role);
                                user.put("userID", userID);
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "User profile created for " + userID);
                                    }
                                });
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean validateFields() {
        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!firstName.matches("[a-zA-Z]+")) {
            Toast.makeText(this, "First name should contain only letters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!lastName.matches("[a-zA-Z]+")) {
            Toast.makeText(this, "Last name should contain only letters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!phone.matches("\\d{10}")) {
            Toast.makeText(this, "Phone number should be 10 digits", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
