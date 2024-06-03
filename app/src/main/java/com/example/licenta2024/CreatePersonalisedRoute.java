package com.example.licenta2024;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreatePersonalisedRoute extends AppCompatActivity {

    FirebaseFirestore fStore;
    ArrayList<DataAttractionAvailability> dataList;
    ArrayList<DataAttractionAvailability> allData;


    RecyclerView recyclerView;
    AdapterAvailableAttractions adapterAvailableAttractions;

    TextView save, seeAll;
    int nr;

    String date;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_personalised_route);
        fStore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<DataAttractionAvailability>();
        allData = new ArrayList<DataAttractionAvailability>();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            nr = (int) bundle.get("nr");
            date = bundle.getString("date");
        }

        allData = getIntent().getParcelableArrayListExtra("filteredAttractions");
        for (DataAttractionAvailability data : allData) {
            Log.d("CreatePersonalisedRoute", "Attraction: " + data.getName());
        }
        if (allData.size() > nr) {
            Collections.shuffle(allData);
            dataList = new ArrayList<>(allData.subList(0, nr));
        }
        adapterAvailableAttractions = new AdapterAvailableAttractions(CreatePersonalisedRoute.this, dataList, allData);
        recyclerView.setAdapter(adapterAvailableAttractions);

        seeAll = findViewById(R.id.seeAll);
        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreatePersonalisedRoute.this, ViewAllFilteredAttr.class);
                intent.putParcelableArrayListExtra("filteredAttractions",allData);
                intent.putExtra("date", date);
                startActivity(intent);
            }
        });

        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> checkedItems = new ArrayList<>();
                if (dataList.size() != 0) {
                    for (DataAttractionAvailability data : dataList) {
                        if (data.isChecked()) {
                            checkedItems.add(data.getAttractionID());
                        }
                    }
                    UUID uuid = UUID.randomUUID();
                    FirebaseAuth fAuth = FirebaseAuth.getInstance();
                    fAuth = FirebaseAuth.getInstance();
                    String userID = fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("attractions-day-plan").document(String.valueOf(uuid));
                    Map<String, Object> plan = new HashMap<>();
                    plan.put("attrIDS", checkedItems);
                    plan.put("userID", userID);
                    plan.put("date", date);
                    plan.put("uuid", String.valueOf(uuid));

                    documentReference.set(plan).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("uuid", String.valueOf(checkedItems));
                        }
                    });

                    Intent intent = new Intent(CreatePersonalisedRoute.this, UserProfile.class);
                    startActivity(intent);
                }
            }
        });
    }
}