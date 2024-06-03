package com.example.licenta2024;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttractionDetailsUser extends AppCompatActivity {

    ImageView image;
    TextView name, description, city, country, openingHours;
    ImageButton goBack;
    Button review;
    String uuid = "", imageURL = "", latitude, longitude, name_, city_, country_;
    FirebaseFirestore fStore;
    RecyclerView recycler;
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
        openingHours = findViewById(R.id.openingHoursOb);
        goBack = findViewById(R.id.goBack);
        review = findViewById(R.id.review);

        fStore = FirebaseFirestore.getInstance();
        recycler = findViewById(R.id.recycler);
        recycler.hasFixedSize();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<>();
        adapterReviews = new AdapterReviews(AttractionDetailsUser.this, dataList);
        recycler.setAdapter(adapterReviews);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Glide.with(this).load(bundle.getString("Image")).into(image);
            name.setText(bundle.getString("Name"));
            city.setText(bundle.getString("City"));
            country.setText(bundle.getString("Country"));
            description.setText(bundle.getString("Description"));
            uuid = bundle.getString("Uuid");
            imageURL = bundle.getString("Image");
            latitude = bundle.getString("Latitude");
            longitude = bundle.getString("Longitude");
            name_ = bundle.getString("Name");
            city_ = bundle.getString("City");
            country_ = bundle.getString("Country");
        }

        goBack.setOnClickListener(view -> {
            Intent intent = new Intent(AttractionDetailsUser.this, NewMainActivity.class);
            startActivity(intent);
            finish();
        });

        review.setOnClickListener(view -> {
            Intent intent = new Intent(AttractionDetailsUser.this, ReviewActivity.class);
            intent.putExtra("Image", imageURL);
            intent.putExtra("Name", name_);
            intent.putExtra("City", city_);
            intent.putExtra("Country", country_);
            intent.putExtra("Latitude", latitude);
            intent.putExtra("Longitude", longitude);
            intent.putExtra("Uuid", uuid);
            startActivity(intent);
            finish();
        });

        fStore.collection("touristic-attraction").document(uuid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> placeDetails = document.getData();
                    if (placeDetails != null) {
                        List<Map<String, Object>> periods = (List<Map<String, Object>>) placeDetails.get("openinghours");
                        displayOpeningHours(periods);
                    }
                } else {
                    Log.d("Firestore", "No such document");
                }
            } else {
                Log.d("Firestore", "get failed with ", task.getException());
            }
        });

        loadReviews();
        fetchPlaceDetails(uuid);
    }

    private void displayOpeningHours(List<Map<String, Object>> periods) {
        if (periods == null || periods.isEmpty()) {
            openingHours.setText("Opening hours not available.");
            return;
        }

        StringBuilder openingHoursText = new StringBuilder();
        Map<String, Integer> dayMap = createDayMap();
        if (dayMap == null) {
            Log.e("AttractionDetailsUser", "Error creating day map.");
            return;
        }

        for (Map<String, Object> period : periods) {
            Log.d("AttractionDetailsUser", "Period: " + period);

            Map<String, Object> open = (Map<String, Object>) period.get("open");
            Map<String, Object> close = (Map<String, Object>) period.get("close");

            if (open != null && close != null) {
                String openDayString = (String) open.get("day");
                String closeDayString = (String) close.get("day");

                Map<String, Object> openTimeMap = (Map<String, Object>) open.get("time");
                Map<String, Object> closeTimeMap = (Map<String, Object>) close.get("time");

                if (openDayString != null && closeDayString != null && openTimeMap != null && closeTimeMap != null) {
                    Log.d("AttractionDetailsUser", "Open Day: " + openDayString + ", Close Day: " + closeDayString);
                    Log.d("AttractionDetailsUser", "Open Time Map: " + openTimeMap + ", Close Time Map: " + closeTimeMap);

                    String openTime = formatTime(openTimeMap);
                    String closeTime = formatTime(closeTimeMap);

                    openingHoursText.append(openDayString)
                            .append(": ")
                            .append(openTime)
                            .append(" - ")
                            .append(closeTime)
                            .append("\n");
                } else {
                    Log.e("AttractionDetailsUser", "Error parsing open/close time or day.");
                }
            } else {
                Log.e("AttractionDetailsUser", "Open or close time is null");
            }
        }

        if (openingHoursText.length() > 0) {
            openingHours.setText(openingHoursText.toString());
        } else {
            openingHours.setText("Opening hours not available.");
        }
    }

    private String formatTime(Map<String, Object> timeMap) {
        if (timeMap == null) {
            return "";
        }

        long hours = (long) timeMap.get("hours");
        long minutes = (long) timeMap.get("minutes");
        String amPm = (hours >= 12) ? "PM" : "AM";

        if (hours > 12) {
            hours -= 12;
        } else if (hours == 0) {
            hours = 12;
        }

        return String.format("%02d:%02d %s", hours, minutes, amPm);
    }


    private Map<String, Integer> createDayMap() {
        Map<String, Integer> dayMap = new HashMap<>();
        dayMap.put("SUNDAY", 0);
        dayMap.put("MONDAY", 1);
        dayMap.put("TUESDAY", 2);
        dayMap.put("WEDNESDAY", 3);
        dayMap.put("THURSDAY", 4);
        dayMap.put("FRIDAY", 5);
        dayMap.put("SATURDAY", 6);
        return dayMap;
    }



    private void fetchPlaceDetails(String uuid) {
        Log.d("Firestore", "Fetching place details for UUID: " + uuid);

        fStore.collection("touristic-attraction").document(uuid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("Firestore", "DocumentSnapshot data: " + document.getData());
                    Map<String, Object> placeDetails = document.getData();
                    if (placeDetails != null) {
                        List<Map<String, Object>> periods = (List<Map<String, Object>>) placeDetails.get("openinghours");
                        if (periods != null && !periods.isEmpty()) {
                            displayOpeningHours(periods);
                        } else {
                            Log.d("Firestore", "No opening hours available for this place.");
                            openingHours.setText("Opening hours not available.");
                        }
                    } else {
                        Log.e("Firestore", "Place details are null.");
                        openingHours.setText("Opening hours not available.");
                    }
                } else {
                    Log.e("Firestore", "No such document with UUID: " + uuid);
                    openingHours.setText("Opening hours not available.");
                }
            } else {
                Log.e("Firestore", "Error fetching document with UUID: " + uuid, task.getException());
                openingHours.setText("Failed to fetch opening hours.");
            }
        });
    }
    private void loadReviews() {
        fStore.collection("reviews").whereEqualTo("attractionID", uuid).get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("reviews", document.getId() + " => " + document.getData());
                        ArrayList<String> list = (ArrayList<String>) document.get("picturesURL");
                        DataClass data = new DataClass(document.getString("attractionID"), document.getString("userID"),
                                document.getString("uuid"), document.getString("title"),
                                document.getString("review"), list, document.getString("date"));
                        dataList.add(data);
                    }
                    adapterReviews.notifyDataSetChanged();
                }).addOnFailureListener(e -> Log.e("Firestore", "Error getting documents: ", e));
    }
}


