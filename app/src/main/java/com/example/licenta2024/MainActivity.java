package com.example.licenta2024;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    DocumentReference documentReference;
    TextView nameUser;
    Button logout, seeFaves;

    ImageButton favorite;
    RecyclerView recyclerView;
    ArrayList <DataClass> dataList;
    MyAdapter myAdapter;

    SearchView searchView;



    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
//        nameUser = findViewById(R.id.nameUser);
//        documentReference = fStore.collection("users").document(userID);
//        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
//                nameUser.setText(documentSnapshot.getString("username"));
//            }
//        });



        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<DataClass>();
        myAdapter = new MyAdapter(MainActivity.this, dataList);
        recyclerView.setAdapter(myAdapter);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });
//
//        seeFaves = findViewById(R.id.seeFaves);
//        seeFaves.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, FavoritesList.class);
//                startActivity(intent);
//            }
//        });


        Event();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchList(s);
                return true;
            }
        });

    }
        public void searchList(String text){
            ArrayList<DataClass> searchList = new ArrayList<>();
            for (DataClass dataClass: dataList){
                if (dataClass.getDataName().toLowerCase().contains(text.toLowerCase()) || dataClass.getDataType().toLowerCase().contains(text.toLowerCase())||
                dataClass.getDataCity().toLowerCase().contains(text.toLowerCase()))
                {
                    searchList.add(dataClass);
                }
            }
            myAdapter.searchDataList(searchList);
        }

    private void Event(){

        fStore.collection("touristic-attraction")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("atractii", document.getId() + " => " + document.getData());
                                DataClass data = new DataClass(document.getString("name"),document.getString("desc"), document.getString("type"),document.getString("city"),
                                        document.getString("country"),document.getString("latitude"),document.getString("longitude"), document.getString("imageURL"), document.getString("uuid") );
                                dataList.add(data);
                            }
                            myAdapter.notifyDataSetChanged();

//                            for(DataClass data : dataList)
//                            {
//                                Log.d("maomor", data.getDataType() + " => " + data.getImageURL());
//
//                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
}