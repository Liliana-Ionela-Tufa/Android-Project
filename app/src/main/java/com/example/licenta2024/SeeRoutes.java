package com.example.licenta2024;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SeeRoutes extends AppCompatActivity {

    ImageView goBack;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    AdapterRoutes adapterRoutes;
    RecyclerView recyclerView;
    ArrayList<DataAttractionAvailability> dates;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_routes);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dates = new ArrayList<>();
        adapterRoutes = new AdapterRoutes(SeeRoutes.this, dates);
        recyclerView.setAdapter(adapterRoutes);

        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(view -> {
            Intent intent = new Intent(SeeRoutes.this, UserProfile.class);
            startActivity(intent);
            finish();
        });



        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        String userID = fAuth.getCurrentUser().getUid();

        fStore.collection("attractions-day-plan").whereEqualTo("userID", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {


            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot snap : task.getResult()) {
                        Log.d("date", snap.getString("date"));
                        DataAttractionAvailability data = new DataAttractionAvailability(snap.getString("uuid"), snap.getString("date"));
                        dates.add(data);
                    }
                    adapterRoutes.notifyDataSetChanged();
                }
            }
        });



    }


}

