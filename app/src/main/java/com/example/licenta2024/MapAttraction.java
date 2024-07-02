package com.example.licenta2024;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MapAttraction extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap mMap;

    LatLng userLocation, location;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_attraction);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        userLocation = getIntent().getParcelableExtra("userLocation");
        location = new LatLng(Double.parseDouble(getIntent().getStringExtra("lat")), Double.parseDouble(getIntent().getStringExtra("lng")));
        name = getIntent().getStringExtra("name");
        Log.d("locMap", name +  " " + userLocation.latitude +", " + location.longitude );

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(null);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.addMarker(new MarkerOptions().position(userLocation).title("My current location"));
        if(location != null)
            mMap.addMarker(new MarkerOptions().position(location).title(name));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
        drawRoute();

    }

    private void drawRoute() {

            String origin = userLocation.latitude + "," + userLocation.longitude;
            String destination = location.latitude + "," + location.longitude;
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin +
                    "&destination=" + destination +
                    "&key=AIzaSyBu25Kb4hIc6GtTGinKoL_l7URTyJDSNk8";

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("API Response", response); // Log the full response
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray routes = jsonResponse.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                            String points = overviewPolyline.getString("points");
                            List<LatLng> decodedPath = PolyUtil.decode(points);
                            mMap.addPolyline(new PolylineOptions().addAll(decodedPath)
                                    .color(ContextCompat.getColor(MapAttraction.this, R.color.darkblue)));
                        } else {
                            Log.e("Directions API", "No routes found");
                        }
                    } catch (JSONException e) {
                        Log.e("Directions API", "JSON parsing error", e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Directions API", "Error response", error);
                }
            });

            requestQueue.add(stringRequest);
        }
}