package com.example.licenta2024;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AttractionDetailsUser extends AppCompatActivity {


    ImageView image;
    TextView name, description, city, country;
    ImageButton goBack;

    Button review;
    String uuid="", imageURL= "", latitude, longitude, name_, description_, city_, country_;

    FirebaseFirestore fStore;
    RecyclerView recycler, recyclerPhotos;
    ArrayList<DataClass> dataList;
    AdapterReviews adapterReviews;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_details_user);
        image = findViewById(R.id.imageOb);
        name = findViewById(R.id.nameOb);
        description = findViewById(R.id.descOb);
        city = findViewById(R.id.cityOb);
        country = findViewById(R.id.countryOb);
        goBack = findViewById(R.id.goBack);
        review = findViewById(R.id.review);

        fStore = FirebaseFirestore.getInstance();
        recycler = findViewById(R.id.recycler);
        recycler.hasFixedSize();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<DataClass>();
        adapterReviews = new AdapterReviews(AttractionDetailsUser.this, dataList);
        recycler.setAdapter(adapterReviews);


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
            name_ = bundle.getString("Name");
            description_ = bundle.getString("Description");
            city_ = bundle.getString("City");
            country_ = bundle.getString("Country");

        }

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AttractionDetailsUser.this, NewMainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AttractionDetailsUser.this, ReviewActivity.class);
                intent.putExtra("Image", imageURL);
                intent.putExtra("Name", name_);
                intent.putExtra("Description", description_);
                intent.putExtra("City", city_);
                intent.putExtra("Country", country_);
                intent.putExtra("Latitude",latitude);
                intent.putExtra("Longitude", longitude);
                intent.putExtra("Uuid", uuid);
                startActivity(intent);
                finish();
            }
        });

        Event();
    }

    private void Event(){

        fStore.collection("reviews").whereEqualTo("attractionID", uuid).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document : task.getResult())
                        {
                            Log.d("reviews", document.getId() + " => " + document.getData());
                            ArrayList<String>list = (ArrayList<String>) document.get("picturesURL");
                            DataClass data = new DataClass(document.getString("attractionID"),document.getString("userID"), document.getString("uuid"),document.getString("title"),
                                    document.getString("review"), list, document.getString("date"));
                            dataList.add(data);
                        }
                        adapterReviews.notifyDataSetChanged();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error getting documents: ", e);
                    }
                });

    }

}