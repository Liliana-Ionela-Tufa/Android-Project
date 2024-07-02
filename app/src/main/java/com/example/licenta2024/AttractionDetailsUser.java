package com.example.licenta2024;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.StringValue;

import org.checkerframework.common.value.qual.DoubleVal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttractionDetailsUser extends AppCompatActivity {

    ImageView image;
    TextView name, description, city, openingHours;
    ImageButton goBack, copy, favoriteButton;
    Button review, seeLocation;
    String uuid = "", imageURL = "", latitude, longitude, name_, city_, country_, desc_, type_;
    FirebaseFirestore fStore;
    RecyclerView recycler;
    ArrayList<DataClass> dataList;
    AdapterReviews adapterReviews;
    FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    String text;

    LatLng userLocation, locationAttr;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_details_user);

        image = findViewById(R.id.imageOb);
        name = findViewById(R.id.nameOb);
        description = findViewById(R.id.descOb);
        city = findViewById(R.id.cityCountryOb);
        openingHours = findViewById(R.id.openingHoursOb);
        goBack = findViewById(R.id.goBack);
        copy = findViewById(R.id.copy);
        favoriteButton = findViewById(R.id.favoriteButton);
        review = findViewById(R.id.review);
        seeLocation = findViewById(R.id.seeLocation);

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
            city.setText(bundle.getString("City") + ", " + bundle.getString("Country"));
            description.setText(bundle.getString("Description"));
            uuid = bundle.getString("Uuid");
            imageURL = bundle.getString("Image");
            name_ = bundle.getString("Name");
            city_ = bundle.getString("City");
            country_ = bundle.getString("Country");
            desc_ = bundle.getString("Description");
            type_ = bundle.getString("Type");
        }

        fStore.collection("touristic-attraction").whereEqualTo("uuid", uuid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    latitude = String.valueOf(document.getDouble("lat"));
                    longitude = String.valueOf(document.getDouble("lng"));
                    locationAttr = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
                    Log.d("locatie", latitude + ' ' + longitude);

                }
            }
        });
        goBack.setOnClickListener(view -> {
//            Intent intent = new Intent(AttractionDetailsUser.this, NewMainActivity.class);
//            startActivity(intent);
            finish();
        });

        seeLocation.setOnClickListener(view ->  {

                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                getCurrentLocation();
                //LatLng userLocation = new LatLng(44.4355, 26.0996);
//                Intent intent = new Intent(AttractionDetailsUser.this, MapAttraction.class);
//                intent.putExtra("userLocation", userLocation );
//                LatLng location = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
//                intent.putExtra("location", location);
//                intent.putExtra("name", name_);
//                startActivity(intent);
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

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                String userID = fAuth.getCurrentUser().getUid();
                fStore.collection("favorites").document(userID + uuid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            favoriteButton.setImageResource(R.drawable.baseline_favorite_24);
                            DocumentReference documentReference = fStore.collection("favorites").document(userID + uuid);
                            Map<String, Object> favorite = new HashMap<>();
                            favorite.put("name", name);
                            favorite.put("desc", desc_);
                            favorite.put("type", type_);
                            favorite.put("city", city_);
                            favorite.put("country", country_);
                            favorite.put("latitude", latitude);
                            favorite.put("longitude", longitude);
                            favorite.put("imageURL", imageURL);
                            favorite.put("uuid", String.valueOf(uuid));
                            favorite.put("user", userID);
                            documentReference.set(favorite).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(AttractionDetailsUser.this, "Saved", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            favoriteButton.setImageResource(R.drawable.outline_favorite_border_24);
                            fStore.collection("favorites").document(userID + uuid)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Deleted", "DocumentSnapshot successfully deleted!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Not deleted", "Error deleting document", e);
                                        }
                                    });
                        }
                    }
                });
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fStore.collection("touristic-attraction").whereEqualTo("uuid", uuid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            text  = document.getString("address");
                            Log.d("adresa", text );

                        }
                    }
                });
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                // Create a ClipData object with the text
                ClipData clip = ClipData.newPlainText("label", text);

                // Set the ClipData content to the clipboard
                clipboard.setPrimaryClip(clip);

                // Show a toast message
                Toast.makeText(AttractionDetailsUser.this, "Address copied to clipboard", Toast.LENGTH_SHORT).show();
            }
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
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String key = fAuth.getCurrentUser().getUid() + uuid;
        favoriteChecker(key);

    }

    public void favoriteChecker(String key) {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("favorites").document(key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() == false) {
                    favoriteButton.setImageResource(R.drawable.outline_favorite_border_24);
                } else {
                    favoriteButton.setImageResource(R.drawable.baseline_favorite_24);
                }

            }
        });
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

                    openingHoursText.append(openDayString.substring(0,1).toUpperCase() + openDayString.substring(1).toLowerCase())
                            .append(": ")
                            .append(openTime)
                            .append(" - ")
                            .append(closeTime)
                            .append("\n");
                }
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
        dayMap.put("Sunday", 0);
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
                }
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

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        Task<Location> locationTask = fusedLocationClient.getLastLocation();
        locationTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    if(latitude==37.4220936 && longitude==-122.083922)
                        userLocation = new LatLng(44.4357, 26.0982);
                        else
                            userLocation = new LatLng(latitude, longitude);
                    //Log.d("locatie lat", String.valueOf(latitude ));
                    //Log.d("locatie long", String.valueOf(longitude ));
                    Intent intent = new Intent(AttractionDetailsUser.this, MapAttraction.class);
                    intent.putExtra("userLocation", userLocation );
                    intent.putExtra("lat", String.valueOf(locationAttr.latitude));
                    intent.putExtra("lng", String.valueOf(locationAttr.longitude));
                    intent.putExtra("name", name_);
                    startActivity(intent);


                }
            }
        });
    }



//    private void requestNewLocationUpdate() {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
//            return;
//        }
//
//        LocationRequest locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setNumUpdates(1);
//
//        try {
//            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
//                @Override
//                public void onLocationResult(LocationResult locationResult) {
//                    Location location = locationResult.getLastLocation();
//                    if (location != null) {
//                        double latitude = location.getLatitude();
//                        double longitude = location.getLongitude();
//                        userLocation = new LatLng(latitude, longitude);
//                        Intent intent = new Intent(AttractionDetailsUser.this, MapAttraction.class);
//                        intent.putExtra("userLocation", userLocation );
//                        intent.putExtra("lat", String.valueOf(locationAttr.latitude));
//                        intent.putExtra("lng", String.valueOf(locationAttr.longitude));
//
//                        intent.putExtra("name", name_);
//                        startActivity(intent);
//                    }
//                }
//            }, Looper.getMainLooper());
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        }
//    }
//
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


