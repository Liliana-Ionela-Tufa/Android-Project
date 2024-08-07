package com.example.licenta2024;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FavoritesList extends AppCompatActivity {


    FirebaseFirestore fStore;
    RecyclerView recyclerView;
    ArrayList<DataClass> dataList;
    AdapterFavorites myAdapter;
    LinearLayout layoutHome, layoutProfile;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_list);

        fStore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<DataClass>();
        myAdapter = new AdapterFavorites(FavoritesList.this, dataList);
        recyclerView.setAdapter(myAdapter);
        layoutHome = findViewById(R.id.layoutHome);
        layoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FavoritesList.this, NewMainActivity.class);
                startActivity(intent);
            }
        });

        layoutProfile = findViewById(R.id.layoutProfile);
        layoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FavoritesList.this, UserProfile.class);
                intent.putExtra("page", "favorites");
                startActivity(intent);
            }
        });


        Event();

    }

    private void Event(){
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String userID  = fAuth.getCurrentUser().getUid();
        fStore.collection("favorites").whereEqualTo("user", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("favorites", document.getId() + " => " + document.getData());
                                DataClass data = new DataClass(document.getString("name"),document.getString("desc"), document.getString("type"),document.getString("city"),
                                        document.getString("country"),document.getString("latitude"),document.getString("longitude"), document.getString("imageURL"), document.getString("uuid"),
                                        document.getString("user"));
                                dataList.add(data);
                            }
                            myAdapter.notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
}