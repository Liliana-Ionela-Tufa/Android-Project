package com.example.licenta2024;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeoApiContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Duration;
import com.google.maps.model.TravelMode;
import com.google.android.gms.maps.model.LatLng;
import com.google.protobuf.StringValue;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
public class DetailsRoute extends AppCompatActivity {


    ImageButton goBack;
    Button seeOnMap;
    String id;

    FirebaseFirestore fStore;

    RecyclerView recyclerView;
    AdapterAvailableAttractions adapterAvailableAttractions;

    ArrayList<DataAttractionAvailability> dataList;
    LatLng userLocation;


    FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String API_KEY = "AIzaSyBu25Kb4hIc6GtTGinKoL_l7URTyJDSNk8";


    @SuppressLint({"MissingInflatedId", "WrongViewCast", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_route);

        goBack = findViewById(R.id.goBack);
        seeOnMap = findViewById(R.id.seeRoute);

        goBack.setOnClickListener(view -> {
            Intent intent = new Intent(DetailsRoute.this, SeeRoutes.class);
            startActivity(intent);
        });

        dataList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterAvailableAttractions = new AdapterAvailableAttractions(DetailsRoute.this, dataList, false);
        recyclerView.setAdapter(adapterAvailableAttractions);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString("id");
        }

        Log.d("name", "Attraction: " + id);

        fStore = FirebaseFirestore.getInstance();
        fStore.collection("attractions-day-plan").whereEqualTo("uuid", id).get().addOnCompleteListener(task -> {
            for (DocumentSnapshot snap : task.getResult()) {

                ArrayList<String> checkedItems = (ArrayList<String>) snap.get("attrIDS");
                ArrayList<String> availability = (ArrayList<String>) snap.get("availability");
                ArrayList<String> name = (ArrayList<String>) snap.get("name");
                ArrayList<String> city = (ArrayList<String>) snap.get("city");
                ArrayList<Double> latitude = (ArrayList<Double>) snap.get("lat");
                ArrayList<Double> longitude = (ArrayList<Double>) snap.get("lng");
                for (int i = 0; i < checkedItems.size(); i++) {
                    String id = checkedItems.get(i);
                    String hours = availability.get(i);
                    String name_ = name.get(i);
                    String city_ = city.get(i);
                    Double lat = latitude.get(i);
                    Double lng = longitude.get(i);
                    //Log.d("wow", city_ + " " + hours);
                    DataAttractionAvailability data = new DataAttractionAvailability(id, name_, hours, city_, lat, lng, false);
                    dataList.add(data);
                }

                adapterAvailableAttractions.notifyDataSetChanged();
            }
        });

        //userLocation = new LatLng(44.4355, 26.0996);

        seeOnMap.setOnClickListener(view -> {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            getCurrentLocation();
//            Log.d("Debug", "seeOnMap button clicked");
//            if (userLocation != null) {
//                Log.d("Debug", "User location is available: " + userLocation.latitude + ", " + userLocation.longitude);
//                ArrayList<DataAttractionAvailability> smoothRoute = createSmoothRoute(userLocation, new ArrayList<>(dataList));
//                for(DataAttractionAvailability data : smoothRoute)
//                {
//                    Log.d("detalii", data.getName());
//                }
//                Intent intent = new Intent(DetailsRoute.this, MapActivity.class);
//                intent.putExtra("userLocation", userLocation);
//                intent.putParcelableArrayListExtra("route", smoothRoute);
//                startActivity(intent);
////            } else {
//                //Toast.makeText(DetailsRoute.this, "User location not available", Toast.LENGTH_SHORT).show();
//            }
        });
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        getCurrentLocation();
        //userLocation = new LatLng(44.4355, 26.0996);


    }


    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        Task<Location> locationTask = fusedLocationClient.getLastLocation();
        locationTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Location location = task.getResult();
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    userLocation = new LatLng(latitude, longitude);
                    //userLocation = new LatLng(44.4355, 26.0996);
                    Log.d("locatie lat", String.valueOf(latitude ));
                    Log.d("locatie long", String.valueOf(longitude ));
                    //Log.d("Debug", "seeOnMap button clicked");
                    if (userLocation != null) {
                        Log.d("Debug", "User location is available: " + userLocation.latitude + ", " + userLocation.longitude);
                        ArrayList<DataAttractionAvailability> Route = createRoute(userLocation, new ArrayList<>(dataList));
                        for (DataAttractionAvailability data : Route) {
                            Log.d("detalii", data.getName());
                        }
                        Intent intent = new Intent(DetailsRoute.this, MapActivity.class);
                        intent.putExtra("userLocation", userLocation);
                        intent.putParcelableArrayListExtra("route", Route);
                        startActivity(intent);
                    }
                }
//                } else {
//                    requestNewLocationUpdate();
//                }
            }
//            } else {
//                requestNewLocationUpdate();
//            }
        });
    }


//
//    private void requestNewLocationUpdate() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
//                        //userLocation = new LatLng(44.4355, 26.0996);
//                        Log.d("Debug", "seeOnMap button clicked");
//                        if (userLocation != null) {
//                            Log.d("Debug", "User location is available: " + userLocation.latitude + ", " + userLocation.longitude);
//                            ArrayList<DataAttractionAvailability> Route = createRoute(userLocation, new ArrayList<>(dataList));
//                            for(DataAttractionAvailability data : Route)
//                            {
//                                Log.d("detalii", data.getName());
//                            }
//                            Intent intent = new Intent(DetailsRoute.this, MapActivity.class);
//                            intent.putExtra("userLocation", userLocation);
//                            intent.putParcelableArrayListExtra("route", Route);
//                            startActivity(intent);
//                        }
//
//                    }
//                }
//            }, Looper.getMainLooper());
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        double radius = 6371.0; // Radius of the Earth in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return radius * c;
    }

    public void sortAttractionsByDistance(LatLng userLocation, ArrayList<DataAttractionAvailability> attractions) {
        Collections.sort(attractions, (a1, a2) -> {
            double distance1 = haversine(userLocation.latitude, userLocation.longitude, a1.getLatitude(), a1.getLongitude());
            double distance2 = haversine(userLocation.latitude, userLocation.longitude, a2.getLatitude(), a2.getLongitude());
            return Double.compare(distance1, distance2);
        });
    }

    public ArrayList<DataAttractionAvailability> createRoute(LatLng userLocation, ArrayList<DataAttractionAvailability> attractions) {
        ArrayList<DataAttractionAvailability> route = new ArrayList<>();
        LatLng currentLocation = userLocation;

        while (!attractions.isEmpty()) {
            sortAttractionsByDistance(currentLocation, attractions);
            DataAttractionAvailability closestAttraction = attractions.remove(0);
            route.add(closestAttraction);
            currentLocation = new LatLng(closestAttraction.getLatitude(), closestAttraction.getLongitude());
        }

        return route;
    }
}
