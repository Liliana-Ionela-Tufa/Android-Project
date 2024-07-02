package com.example.licenta2024;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class AdminMap extends AppCompatActivity implements OnMapReadyCallback {


    GoogleMap map;
    //Button backButton;
    Marker marker = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng point) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title("Selected location");
                markerOptions.position(point);
                if(marker!=null){
                    marker.remove();
                }
                marker = map.addMarker(markerOptions);



                String latitude = String.valueOf(marker.getPosition().latitude);
                String longitude = String.valueOf(marker.getPosition().longitude);


                Intent intent = new Intent(AdminMap.this, UpdateActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
                finish();
            }

        });
        map.getUiSettings().setZoomControlsEnabled(true);

    }
}