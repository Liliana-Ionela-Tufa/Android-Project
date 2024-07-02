package com.example.licenta2024;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ViewAllFilteredAttr extends AppCompatActivity {

    RecyclerView recyclerView;
    AdapterAvailableAttractions adapterAvailableAttractions;
    TextView cancel, save;
    ArrayList<DataAttractionAvailability> filteredAttractions;

    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_filtered_attr);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        filteredAttractions = getIntent().getParcelableArrayListExtra("filteredAttractions");
        if (filteredAttractions != null) {
            for (DataAttractionAvailability data : filteredAttractions) {
                Log.d("ViewAllFilteredAttr", "Attraction: " + data.getName());
            }
        }
        filteredAttractions = getIntent().getParcelableArrayListExtra("filteredAttractions");

        adapterAvailableAttractions = new AdapterAvailableAttractions(this, filteredAttractions, true);
        recyclerView.setAdapter(adapterAvailableAttractions);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            date = bundle.getString("date");
        }
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(ViewAllFilteredAttr.this, UserProfile.class);
                startActivity(intent);
            }
        });

        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> checkedItems = new ArrayList<>();
                ArrayList<String> availability = new ArrayList<>();
                ArrayList<String> name = new ArrayList<>();
                ArrayList<String> city = new ArrayList<>();
                ArrayList<Double> latitude = new ArrayList<>();
                ArrayList<Double> longitude = new ArrayList<>();
                if (filteredAttractions.size() != 0) {
                    for (DataAttractionAvailability data : filteredAttractions) {
                        if (data.isChecked()) {
                            checkedItems.add(data.getAttractionID());
                            availability.add(data.getAvailability());
                            name.add(data.getName());
                            city.add(data.getCity());
                            latitude.add(data.getLatitude());
                            longitude.add(data.getLongitude());
                        }
                    }
                    UUID uuid = UUID.randomUUID();
                    FirebaseAuth fAuth = FirebaseAuth.getInstance();
                    fAuth = FirebaseAuth.getInstance();
                    String userID = fAuth.getCurrentUser().getUid();
                    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                    DocumentReference documentReference = fStore.collection("attractions-day-plan").document(String.valueOf(uuid));
                    Map<String, Object> plan = new HashMap<>();
                    plan.put("attrIDS", checkedItems);
                    plan.put("availability", availability);
                    plan.put("name", name);
                    plan.put("city", city);
                    plan.put("userID", userID);
                    plan.put("date", date);
                    plan.put("lat", latitude);
                    plan.put("lng", longitude);
                    plan.put("uuid", String.valueOf(uuid));

                    documentReference.set(plan).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("uuid", String.valueOf(checkedItems));
                        }
                    });

                    Intent intent = new Intent(ViewAllFilteredAttr.this, UserProfile.class);
                    startActivity(intent);
                }
            }
        });

    }
}