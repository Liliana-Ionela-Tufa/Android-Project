package com.example.licenta2024;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChooseAttractions extends AppCompatActivity {


    FirebaseFirestore fStore;

    String date;

    DayOfWeek chosenDay;

    ArrayList <DataAttractionAvailability> dataList;

    RecyclerView recyclerView;
    AdapterAvailableAttractions adapterAvailableAttractions;

    TextView save;

    String result="";
    List<Map<String, Object>> periods;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_attractions);
        fStore = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            date = bundle.getString("date");
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<DataAttractionAvailability>();
        adapterAvailableAttractions = new AdapterAvailableAttractions(ChooseAttractions.this, dataList, true);
        recyclerView.setAdapter(adapterAvailableAttractions);

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
                if (dataList.size() != 0) {
                    for (DataAttractionAvailability data : dataList) {
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
                    DocumentReference documentReference = fStore.collection("attractions-day-plan").document(String.valueOf(uuid));
                    Map<String, Object> plan = new HashMap<>();
                    plan.put("attrIDS", checkedItems);
                    plan.put("availability", availability);
                    plan.put("userID", userID);
                    plan.put("name", name);
                    plan.put("city", city);
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

                    Intent intent = new Intent(ChooseAttractions.this, UserProfile.class);
                    startActivity(intent);
                }
            }
        });

        String [] DMY = date.split("/");
        int day = Integer.valueOf(DMY[0]);
        int month = Integer.valueOf(DMY[1]);
        int year = Integer.valueOf(DMY[2]);
        chosenDay = getDayOfWeekForDate(day, month, year);
;
        fStore.collection("touristic-attraction").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshot) {
                for(DocumentSnapshot snap : snapshot)
                {
                    Map<String, Object> placeDetails = snap.getData();
                    //List<Map<String, Object>> periods = (List<Map<String, Object>>) placeDetails.get("openinghours");
                    if(placeDetails.get("openinghours")!=null) {
                        periods = (List<Map<String, Object>>) placeDetails.get("openinghours");
                        result = getPlaceOpeningHoursOnDay(periods, chosenDay);
                        if(result==null)
                            result = "Opening hours not available";
                    }
                    else
                    {
                        result = "Opening hours not available";
                    }
                    Log.d((String) placeDetails.get("name"), result);
                    if(result!=null)
                    {
                        DataAttractionAvailability data = new DataAttractionAvailability((String) placeDetails.get("uuid"),
                                (String) placeDetails.get("name"), result, (String) placeDetails.get("city"), (Double) placeDetails.get("lat"), (Double) placeDetails.get("lng"), false);
                        dataList.add(data);
                    }
                }
                adapterAvailableAttractions.notifyDataSetChanged();

            }
        });
    }


    public String getPlaceOpeningHoursOnDay(List<Map<String, Object>> openingHours, DayOfWeek chosenDay) {
        for (Map<String, Object> period : openingHours) {
            Map<String, Object> open = (Map<String, Object>) period.get("open");
            Map<String, Object> close = (Map<String, Object>) period.get("close");

            if (open != null && close != null) {
                String openDay = (String) open.get("day");

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