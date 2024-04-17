package com.example.licenta2024;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    ImageView uploadImage;
    Button addAddress, submit;
    TextView uploadName, uploadDescription, uploadCity, uploadCountry, uploadType;
    String imageURL;
    Uri uri;
    UUID uuid;

    FirebaseFirestore fStore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        uploadImage = findViewById(R.id.uploadImage);
        uploadName = findViewById(R.id.uploadName);
        uploadDescription = findViewById(R.id.uploadDesc);
        uploadCity = findViewById(R.id.uploadCity);
        uploadCountry = findViewById(R.id.uploadCountry);
        uploadType = findViewById(R.id.uploadType);
        addAddress = findViewById(R.id.addAddress);
        submit = findViewById(R.id.submit2);
        fStore = FirebaseFirestore.getInstance();

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UploadActivity.this, AdminMap.class);
                startActivity(intent);
                finish();
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            uploadImage.setImageURI(uri);
                        } else {
                            Toast.makeText(UploadActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener() {
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
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
                .child(uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
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
                imageURL = urlImage.toString();
                uploadData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    public void uploadData(){
        String name = uploadName.getText().toString();
        String desc = uploadDescription.getText().toString();
        String type = uploadType.getText().toString();
        String city = uploadCity.getText().toString();
        String country = uploadCountry.getText().toString();
        String latitude = getIntent().getStringExtra("latitude");
        String longitude =getIntent().getStringExtra("longitude");
        uuid = UUID.randomUUID();

        DocumentReference documentReference = fStore.collection("touristic-attraction").document(String.valueOf(uuid));
        Map<String, Object> attraction = new HashMap<>();
        attraction.put("name", name);
        attraction.put("desc", desc);
        attraction.put("type", type);
        attraction.put("city", city);
        attraction.put("country", country);
        attraction.put("latitude", latitude);
        attraction.put("longitude", longitude);
        attraction.put("imageURL", imageURL);
        attraction.put("uuid", String.valueOf(uuid));

        documentReference.set(attraction).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(UploadActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(UploadActivity.this, UploadActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}