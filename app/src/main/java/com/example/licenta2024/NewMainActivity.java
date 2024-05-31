package com.example.licenta2024;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NewMainActivity extends AppCompatActivity {

    TextView nameUser, seeAll;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    LinearLayout layoutProfile, layoutFavorites;
    ImageView profilePicture;
    RecyclerView recyclerView;
    ArrayList<DataClass> dataList;
    MyAdapter myAdapter;
    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

        nameUser = findViewById(R.id.nameUser);
        profilePicture = findViewById(R.id.userPicture);
        fAuth = FirebaseAuth.getInstance();
        String userID = fAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();
        seeAll = findViewById(R.id.seeAll);
        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewMainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                nameUser.setText(documentSnapshot.getString("firstName"));
                Glide.with(NewMainActivity.this).load(documentSnapshot.getString("imageURL")).into(profilePicture);

            }
        });


        layoutProfile = findViewById(R.id.layoutProfile);
        layoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewMainActivity.this, UserProfile.class);
                startActivity(intent);
            }
        });

        layoutFavorites = findViewById(R.id.layoutFavorites);
        layoutFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewMainActivity.this, FavoritesList.class);
                startActivity(intent);
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<DataClass>();
        myAdapter = new MyAdapter(NewMainActivity.this, dataList);
        recyclerView.setAdapter(myAdapter);

        Event();
    }

    private void Event(){

        fStore.collection("touristic-attraction")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(dataList.size()<4) {
                                    Log.d("atractii", document.getId() + " => " + document.getData());
                                    DataClass data = new DataClass(document.getString("name"), document.getString("desc"), document.getString("type"), document.getString("city"),
                                            document.getString("country"), document.getString("latitude"), document.getString("longitude"), document.getString("imageURL"), document.getString("uuid"));
                                    dataList.add(data);
                                }
                                else break;
                            }
                            myAdapter.notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
}