package com.example.licenta2024;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class FavoritesDetails extends AppCompatActivity {


    ImageView image;
    TextView name, description, city, country;// latitudeT, longitudeT;
    Button goBack;// goToLocation;
    String uuid="", imageURL= "", latitude, longitude;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_details);
        setContentView(R.layout.activity_attraction_details_user);
        image = findViewById(R.id.imageOb);
        name = findViewById(R.id.nameOb);
        description = findViewById(R.id.descOb);
        city = findViewById(R.id.cityOb);
        country = findViewById(R.id.countryOb);
        goBack = findViewById(R.id.goBack);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            Glide.with(this).load(bundle.getString("Image")).into(image);
            name.setText(bundle.getString("Name"));
            description.setText(bundle.getString("Description"));
            city.setText(bundle.getString("City"));
            country.setText(bundle.getString("Country"));
            uuid = bundle.getString("Uuid");
            imageURL = bundle.getString("Image");
            latitude = bundle.getString("Latitude");
            longitude = bundle.getString("Longitude");
        }

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FavoritesDetails.this, FavoritesList.class);
                startActivity(intent);
                finish();
            }
        });
    }
}