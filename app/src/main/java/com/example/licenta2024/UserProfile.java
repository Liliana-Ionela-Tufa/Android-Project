package com.example.licenta2024;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class UserProfile extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageButton drawerButton, backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerButton = findViewById(R.id.buttonDrawer);
        backButton = findViewById(R.id.goBack);

        drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.open();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this, NewMainActivity.class);
                startActivity(intent);
            }
        });
    }
}