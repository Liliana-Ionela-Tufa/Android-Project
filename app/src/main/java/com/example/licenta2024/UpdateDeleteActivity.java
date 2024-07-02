package com.example.licenta2024;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UpdateDeleteActivity extends AppCompatActivity {

    FirebaseFirestore fStore;
    DocumentReference documentReference;
    RecyclerView recyclerView;
    ArrayList<DataClass> dataList;
    AdapterAdmin adapterAdmin;
    SearchView searchView;

    ImageButton goBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();
        fStore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<DataClass>();
        adapterAdmin = new AdapterAdmin(UpdateDeleteActivity.this, dataList);
        recyclerView.setAdapter(adapterAdmin);

        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
        adapterAdmin.searchDataList(searchList);
    }

    private void Event(){

        fStore.collection("touristic-attraction")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DataClass data = new DataClass(document.getString("name"),document.getString("desc"), document.getString("type"),document.getString("city"),
                                        document.getString("country"),document.getString("latitude"),document.getString("longitude"), document.getString("imageURL"), document.getString("uuid") );
                                dataList.add(data);
                            }
                            adapterAdmin.notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

}