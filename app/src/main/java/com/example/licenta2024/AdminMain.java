package com.example.licenta2024;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class AdminMain extends AppCompatActivity {

    Button uploadAttraction, updateDelete, deleteReview, deleteUser;
    ImageButton logout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        uploadAttraction = findViewById(R.id.uploadAttraction);
        updateDelete = findViewById(R.id.updateDelete);
        deleteReview = findViewById(R.id.deleteReview);
        deleteUser = findViewById(R.id.deleteUser);
        logout = findViewById(R.id.logout);

        uploadAttraction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMain.this, PickAttraction.class);
                startActivity(intent);
            }
        });


        updateDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMain.this, UpdateDeleteActivity.class);
                startActivity(intent);
            }
        });

        deleteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMain.this, DeleteReviews.class);
                startActivity(intent);
            }
        });

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMain.this, DeleteUser.class);
                startActivity(intent);
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AdminMain.this, Login.class);
                AdminMain.this.startActivity(intent);
            }
        });
    }
}