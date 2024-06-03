package com.example.licenta2024;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class UpdateActivity extends AppCompatActivity {
    ImageView updateImage;
    Button addAddress, submit;
    TextView updateName, updateDescription, updateCity, updateCountry, updateType;
    String imageURL, latitude, longitude;
    Uri uri;

    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        updateImage = findViewById(R.id.updateImage);
        updateName = findViewById(R.id.updateName);
        updateDescription = findViewById(R.id.updateDesc);
        updateCity = findViewById(R.id.updateCity);
        updateCountry = findViewById(R.id.updateCountry);
        updateType = findViewById(R.id.updateType);
        addAddress = findViewById(R.id.updateAddAddress);
        submit = findViewById(R.id.submit2);
        fStore = FirebaseFirestore.getInstance();

//        addAddress.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(UpdateActivity.this, AdminMap.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            updateImage.setImageURI(uri);
                        } else {
                            Toast.makeText(UpdateActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            Glide.with(this).load(bundle.getString("Image")).into(updateImage);
            updateName.setText(bundle.getString("Name"));
            updateType.setText(bundle.getString("Type"));
            updateDescription.setText(bundle.getString("Description"));
            updateCity.setText(bundle.getString("City"));
            updateCountry.setText(bundle.getString("Country"));
            imageURL = bundle.getString("Image");
            latitude = bundle.getString("Latitude");
            longitude = bundle.getString("Longitude");

        }
        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

    }
    public void saveData(){
        if(uri != null) {
            // Upload new image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
                    .child(uri.getLastPathSegment());
            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
            builder.setCancelable(false);
            builder.setView(R.layout.progress_layout);
            AlertDialog dialog = builder.create();
            dialog.show();
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete());
                    Uri urlImage = uriTask.getResult();
                    imageURL = urlImage != null ? urlImage.toString() : null;
                    updateData();
                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                }
            });
        } else {
            // No new image selected, update data with the existing image URL
            updateData();
        }
    }
    public void updateData()
    {
        String name = updateName.getText().toString();
        String desc = updateDescription.getText().toString();
        String type = updateType.getText().toString();
        String city = updateCity.getText().toString();
        String country = updateCountry.getText().toString();
        String latitude = getIntent().getStringExtra("latitude");
        String longitude = getIntent().getStringExtra("longitude");
        String Uuid = getIntent().getStringExtra("Uuid");

        DocumentReference documentReference = fStore.collection("touristic-attraction").document(Uuid);
        Map<String, Object> attraction = new HashMap<>();
        attraction.put("name", name);
        attraction.put("desc", desc);
        attraction.put("type", type);
        attraction.put("city", city);
        attraction.put("country", country);
        attraction.put("latitude", latitude);
        attraction.put("longitude", longitude);
        attraction.put("imageURL", imageURL);
        attraction.put("uuid", Uuid);


        documentReference.update("name", name, "desc", desc,"type", type, "country", country,"latitude", latitude, "longitude", longitude, "imageURL", imageURL )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(UpdateActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}