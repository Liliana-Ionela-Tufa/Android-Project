package com.example.licenta2024;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

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
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<DataAttractionAvailability> route;
    LatLng userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        
        route = getIntent().getParcelableArrayListExtra("route");
        userLocation = getIntent().getParcelableExtra("userLocation");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.addMarker(new MarkerOptions().position(userLocation).title("My current location"));
        if (route != null && !route.isEmpty()) {
            LatLng firstLocation = null;

            for (DataAttractionAvailability attraction : route) {
                LatLng location = new LatLng(attraction.getLatitude(), attraction.getLongitude());
                mMap.addMarker(new MarkerOptions().position(location).title(attraction.getName()));
                if (firstLocation == null) {
                    firstLocation = location;
                }
            }

            if (firstLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10));
                drawRoute();
            }
        }

        mMap.setMapStyle(null);

    }

    private void drawRoute() {
        if (route != null && !route.isEmpty() && !userLocation.equals(null)) {
            //String origin = route.get(0).getLatitude() + "," + route.get(0).getLongitude();
            String origin = userLocation.latitude + "," + userLocation.longitude;
            String destination = route.get(route.size() - 1).getLatitude() + "," + route.get(route.size() - 1).getLongitude();
            StringBuilder waypoints = new StringBuilder("optimize:true");
            for (int i = 0; i < route.size() - 1; i++) {
                waypoints.append("|").append(route.get(i).getLatitude()).append(",").append(route.get(i).getLongitude());
            }

            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin +
                    "&destination=" + destination + "&waypoints=" + waypoints.toString() +
                    "&key=AIzaSyBu25Kb4hIc6GtTGinKoL_l7URTyJDSNk8";

            //Log.d("URL", url);

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("API Response", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray routes = jsonResponse.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                            String points = overviewPolyline.getString("points");
                            List<LatLng> decodedPath = PolyUtil.decode(points);
                            mMap.addPolyline(new PolylineOptions().addAll(decodedPath)
                                    .color(ContextCompat.getColor(MapActivity.this, R.color.darkblue)));
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




}
