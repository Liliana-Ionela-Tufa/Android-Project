package com.example.licenta2024;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePassword extends AppCompatActivity {

    EditText oldPassword, newPassword, reenterPassword;

    String oldPass, newPass, reNewPass;
    Button saveButton;
    FirebaseAuth auth;

    TextView noEdit;

    ImageView showPassword;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPassword = findViewById(R.id.old_password);
        newPassword = findViewById(R.id.new_password);
        reenterPassword = findViewById(R.id.reenter_password);
        saveButton = findViewById(R.id.save);
        noEdit = findViewById(R.id.noedit);
        showPassword = findViewById(R.id.show_hide_pwd);
        auth = FirebaseAuth.getInstance();

        showPassword.setImageResource(R.drawable.ic_hide_pwd);
        //showConfirmPassword.setImageResource(R.drawable.ic_hide_pwd);
        oldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        reenterPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!oldPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    oldPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    newPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    reenterPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showPassword.setImageResource(R.drawable.ic_show_pwd);
                } else {
                    oldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    reenterPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        noEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(ChangePassword.this, EditUserProfile.class);
//                startActivity(intent);
                finish();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPass = oldPassword.getText().toString();
                newPass = newPassword.getText().toString();
                reNewPass = reenterPassword.getText().toString();

                if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(reNewPass)) {
                    Toast.makeText(ChangePassword.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPass.equals(reNewPass)) {
                    Toast.makeText(ChangePassword.this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);

                    user.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPass).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {

                                    Toast.makeText(ChangePassword.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ChangePassword.this, "Password update failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
                    finish();
            }
        });
    }
}
