package com.example.licenta2024;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadAttraction extends AppCompatActivity {


    ImageView uploadImage;
    Button submit;
    TextView uploadName, uploadDescription, uploadCity, uploadCountry, uploadType;
    String imageURL;
    String id, name, city, country, latitude, longitude;


    FirebaseFirestore fStore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_attraction);

        uploadImage = findViewById(R.id.uploadImage);
        uploadName = findViewById(R.id.uploadName);
        uploadDescription = findViewById(R.id.uploadDesc);
        uploadCity = findViewById(R.id.uploadCity);
        uploadCountry = findViewById(R.id.uploadCountry);
        uploadType = findViewById(R.id.uploadType);
        submit = findViewById(R.id.submit2);
        fStore = FirebaseFirestore.getInstance();


        Bundle bundle = getIntent().getExtras();
        if(!bundle.isEmpty())
        {
            id = bundle.getString("id");
            Glide.with(UploadAttraction.this).load(bundle.getString("photoUrl")).into(uploadImage);
            imageURL = bundle.getString("photoUrl");
            latitude = bundle.getString("lat");
            longitude = bundle.getString("lng");
            name = bundle.getString("name");
            uploadName.setText(name);
            city = bundle.getString("city");
            uploadCity.setText(city);
            country = bundle.getString("country");
            uploadCountry.setText(country);


        }

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
                Intent intent = new Intent(UploadAttraction.this, AdminMain.class);
                startActivity(intent);
            }
        });


    }

    public void uploadData()
    {
        String name = uploadName.getText().toString();
        String desc = uploadDescription.getText().toString();
        String type = uploadType.getText().toString();
        String city = uploadCity.getText().toString();
        String country = uploadCountry.getText().toString();


        DocumentReference documentReference = fStore.collection("touristic-attraction").document(id);
        Map<String, Object> attraction = new HashMap<>();
        attraction.put("name", name);
        attraction.put("desc", desc);
        attraction.put("type", type);
        attraction.put("city", city);
        attraction.put("country", country);
        attraction.put("latitude", latitude);
        attraction.put("longitude", longitude);
        attraction.put("imageURL", imageURL);
        attraction.put("uuid", id);

        documentReference.set(attraction).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(UploadAttraction.this, "Saved", Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(UploadAttraction.this, AdminMain.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadAttraction.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}