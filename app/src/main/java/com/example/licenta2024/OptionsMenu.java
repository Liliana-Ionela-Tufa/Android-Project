package com.example.licenta2024;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OptionsMenu extends AppCompatActivity {

    EditText numAttractions, maxDistance;
    Button btnShowResults, btnSelectTypes;
    Spinner spinnerCity;
    ArrayList<String> attractionTypes = new ArrayList<>();
    ArrayList<String> cities = new ArrayList<>();
    ArrayList<String> selectedAttractionTypes = new ArrayList<>();
    List<Map<String, Object>> periods;
    boolean[] checkedItems;

    String date;
    DayOfWeek chosenDay;

    String result;
    int nr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_menu);

        numAttractions = findViewById(R.id.num_attractions);
        maxDistance = findViewById(R.id.max_distance);
        spinnerCity = findViewById(R.id.spinner_city);
        btnShowResults = findViewById(R.id.btn_show_results);
        btnSelectTypes = findViewById(R.id.btn_select_types);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            date = bundle.getString("date");
        }
        String[] DMY = date.split("/");
        int day = Integer.parseInt(DMY[0]);
        int month = Integer.parseInt(DMY[1]);
        int year = Integer.parseInt(DMY[2]);
        chosenDay = getDayOfWeekForDate(day, month, year);

        getAttractionTypes();
        getCities();

        btnSelectTypes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMultiChoiceDialog();
            }
        });

        btnShowResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numAttractionsStr = numAttractions.getText().toString();
                String maxDistanceStr = maxDistance.getText().toString();
                if (numAttractionsStr.isEmpty() || maxDistanceStr.isEmpty()) {
                    Toast.makeText(OptionsMenu.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    int numAttractions = Integer.parseInt(numAttractionsStr);
                    nr = Integer.parseInt(numAttractionsStr);
                    double maxDistance = Double.parseDouble(maxDistanceStr);

                    if (numAttractions > 7) {
                        Toast.makeText(OptionsMenu.this, "Number of Attractions cannot be more than 7", Toast.LENGTH_SHORT).show();
                    } else if (numAttractions < 1) {
                        Toast.makeText(OptionsMenu.this, "You must choose at least one attraction", Toast.LENGTH_SHORT).show();
                    } else if (selectedAttractionTypes.isEmpty()) {
                        Toast.makeText(OptionsMenu.this, "Please select at least one type of attraction", Toast.LENGTH_SHORT).show();
                    } else {
                        fetchAndFilterAttractions(maxDistance);
                    }
                }
            }
        });
    }

    private void showMultiChoiceDialog() {
        checkedItems = new boolean[attractionTypes.size()];
        for (int i = 0; i < attractionTypes.size(); i++) {
            checkedItems[i] = selectedAttractionTypes.contains(attractionTypes.get(i));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Types of Attractions");
        builder.setMultiChoiceItems(attractionTypes.toArray(new String[0]), checkedItems, (dialog, which, isChecked) -> {
            if (isChecked) {
                if (!selectedAttractionTypes.contains(attractionTypes.get(which))) {
                    selectedAttractionTypes.add(attractionTypes.get(which));
                }
            } else {
                selectedAttractionTypes.remove(attractionTypes.get(which));
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getAttractionTypes() {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("touristic-attraction").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshot) {
                for (DocumentSnapshot snap : snapshot) {
                    Map<String, Object> placeDetails = snap.getData();
                    String type = (String) placeDetails.get("type");
                    if (!attractionTypes.contains(type)) {
                        attractionTypes.add(type);
                    }
                }
            }
        });
    }

    private void getCities() {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("touristic-attraction").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshot) {
                for (DocumentSnapshot snap : snapshot) {
                    Map<String, Object> placeDetails = snap.getData();
                    String city = (String) placeDetails.get("city");
                    if (!cities.contains(city)) {
                        cities.add(city);
                    }
                }
                populateCitySpinner();
            }
        });
    }

    private void populateCitySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);
    }

    private void fetchAndFilterAttractions(double maxDistance) {
        String selectedCity = spinnerCity.getSelectedItem().toString();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("touristic-attraction").whereEqualTo("city", selectedCity).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshot) {
                ArrayList<DataAttractionAvailability> attractions = new ArrayList<>();
                for (DocumentSnapshot snap : snapshot) {
                    Map<String, Object> placeDetails = snap.getData();
                    String name = (String) placeDetails.get("name");
                    String type = (String) placeDetails.get("type");
                    if (placeDetails.get("openinghours") != null) {
                        periods = (List<Map<String, Object>>) placeDetails.get("openinghours");
                        result = getPlaceOpeningHoursOnDay(periods, chosenDay);
                    } else {
                        result = "Opening hours are not available";
                    }
                    if (selectedAttractionTypes.contains(type)) {
                        attractions.add(new DataAttractionAvailability((String) placeDetails.get("uuid"), name, result, (String) placeDetails.get("city"),
                                (Double) placeDetails.get("lat"), (Double) placeDetails.get("lng"), false));
                    }
                }
                filterAttractionsByDistance(attractions, maxDistance);
            }
        });
    }

    private void filterAttractionsByDistance(ArrayList<DataAttractionAvailability> attractions, double maxDistance) {
        ArrayList<DataAttractionAvailability> filteredAttractions = new ArrayList<>();

        for (int i = 0; i < attractions.size(); i++) {
            DataAttractionAvailability attraction1 = attractions.get(i);
            for (int j = i + 1; j < attractions.size(); j++) {
                DataAttractionAvailability attraction2 = attractions.get(j);
                double distance = haversine(Double.valueOf(attraction1.getLatitude()), Double.valueOf(attraction1.getLongitude()),
                        Double.valueOf(attraction2.getLatitude()), Double.valueOf(attraction2.getLongitude()));
                if (distance <= maxDistance) {
                    if (!filteredAttractions.contains(attraction1)) {
                        filteredAttractions.add(attraction1);
                    }
                    if (!filteredAttractions.contains(attraction2)) {
                        filteredAttractions.add(attraction2);
                    }
                }
            }
        }

        Intent intent = new Intent(OptionsMenu.this, CreatePersonalisedRoute.class);
        intent.putParcelableArrayListExtra("filteredAttractions", filteredAttractions);
        intent.putExtra("nr", nr);
        intent.putExtra("date", date);
        startActivity(intent);
    }

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to kilometers
        return distance;
    }

    public String getPlaceOpeningHoursOnDay(List<Map<String, Object>> openingHours, DayOfWeek chosenDay) {
        for (Map<String, Object> period : openingHours) {
            Map<String, Object> open = (Map<String, Object>) period.get("open");
            Map<String, Object> close = (Map<String, Object>) period.get("close");

            if (open != null && close != null) {
                String openDay = (String) open.get("day");
                String closeDay = (String) close.get("day");

                DayOfWeek openDayOfWeek = DayOfWeek.valueOf(openDay);

                if (openDayOfWeek == chosenDay) {
                    Map<String, Object> openTime = (Map<String, Object>) open.get("time");
                    Map<String, Object> closeTime = (Map<String, Object>) close.get("time");

                    int openHour = ((Long) openTime.get("hours")).intValue();
                    int openMinute = ((Long) openTime.get("minutes")).intValue();
                    int closeHour = ((Long) closeTime.get("hours")).intValue();
                    int closeMinute = ((Long) closeTime.get("minutes")).intValue();

                    return String.format("Open from %02d:%02d to %02d:%02d",
                            openHour, openMinute, closeHour, closeMinute);
                }
            }
        }
        return null;
    }

    public DayOfWeek getDayOfWeekForDate(int day, int month, int year) {
        LocalDate date = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            date = LocalDate.of(year, month, day);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return date.getDayOfWeek();
        }
        return null;
    }
}

