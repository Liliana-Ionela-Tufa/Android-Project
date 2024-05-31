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
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DeleteReviews extends AppCompatActivity {

    ImageButton goBack;
    RecyclerView recyclerView;
    AdapterReviewsDelete adapterReviewsDelete;

    ArrayList<DataClass> dataList;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_reviews);
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeleteReviews.this, AdminMain.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<DataClass>();
        adapterReviewsDelete = new AdapterReviewsDelete(DeleteReviews.this, dataList);
        recyclerView.setAdapter(adapterReviewsDelete);

        Event();
    }

    private void Event(){
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("reviews").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document : task.getResult())
                        {
                            Log.d("reviews", document.getId() + " => " + document.getData());
                            ArrayList<String>list = (ArrayList<String>) document.get("picturesURL");
                            DataClass data = new DataClass(document.getString("attractionID"),document.getString("userID"), document.getString("uuid"),document.getString("title"),
                                    document.getString("review"), list, document.getString("date"));
                            dataList.add(data);
                        }
                        adapterReviewsDelete.notifyDataSetChanged();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error getting documents: ", e);
                    }
                });

    }
}