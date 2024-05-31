package com.example.licenta2024;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.common.value.qual.StringVal;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReviewActivity extends AppCompatActivity {

    TextView cancel, save;
    String uuid="", imageURL= "", latitude, longitude, name_, description_, city_, country_, name, imageUser, userID;

    Button select;

    ArrayList<Uri>uriArrayList = new ArrayList<>();
    ArrayList<String> urlList = new ArrayList<>();

    FirebaseFirestore fStore;
    StorageReference storageReference;

    TextView title, review;

    CheckBox  checkBox;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        cancel = findViewById(R.id.cancel);
        save = findViewById(R.id.save);
        select = findViewById(R.id.select);
        title = findViewById(R.id.title);
        review = findViewById(R.id.review);
        checkBox = findViewById(R.id.checkbox);
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            uuid = bundle.getString("Uuid");
            imageURL = bundle.getString("Image");
            latitude = bundle.getString("Latitude");
            longitude = bundle.getString("Longitude");
            name_ = bundle.getString("Name");
            description_ = bundle.getString("Description");
            city_ = bundle.getString("City");
            country_ = bundle.getString("Country");
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReviewActivity.this, AttractionDetailsUser.class);
                intent.putExtra("Image", imageURL);
                intent.putExtra("Name", name_);
                intent.putExtra("Description", description_);
                intent.putExtra("City", city_);
                intent.putExtra("Country", country_);
                intent.putExtra("Latitude",latitude);
                intent.putExtra("Longitude", longitude);
                intent.putExtra("Uuid", uuid);
                startActivity(intent);
                finish();
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select photo"), 1);

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
    }

    public void saveData(){
        for(int i = 0; i<uriArrayList.size(); i++)
        {
            Uri individualImage = uriArrayList.get(i);
//            if(individualImage!=null)
//            {
//                storageReference.child("Reviews photos").child(individualImage.getLastPathSegment());
//                storageReference.putFile(individualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                urlList.add(uri.toString());
//                                if(urlList.size() == uriArrayList.size())
//                                    uploadData();
//                            }
//                        });
//                    }
//                });
//            }
            if(individualImage!=null)
            {
                // Set the reference to the storage location where you want to upload the file
                StorageReference fileReference = storageReference.child("Reviews photos").child(UUID.randomUUID().toString());

                // Upload the file to the specified storage location
                UploadTask uploadTask = fileReference.putFile(individualImage);

                // Monitor the upload task for success or failure
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // File uploaded successfully, now get the download URL
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Add the download URL to the list
                                urlList.add(uri.toString());
                                // If all files have been uploaded, proceed to upload data
                                if(urlList.size() == uriArrayList.size())
                                    uploadData();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the failure of file upload
                        Toast.makeText(ReviewActivity.this, "Failed to upload file.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public void uploadData(){

        String titleUpload = title.getText().toString();
        String reviewUpload = review.getText().toString();
        UUID id = UUID.randomUUID();
        DateFormat date = new SimpleDateFormat("MMM dd yyyy, h:mm");
        String dateFormat = date.format(Calendar.getInstance().getTime());

        if(checkBox.isChecked())
        {
            userID = "";
        }

        else
        {
            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            userID = fAuth.getCurrentUser().getUid();
        }
        
        DocumentReference documentReference = fStore.collection("reviews").document(String.valueOf(id));
        Map<String, Object> review = new HashMap<>();
        review.put("title", titleUpload);
        review.put("review", reviewUpload);
        review.put("userID", userID);
        review.put("attractionID", uuid);
        review.put("picturesURL", urlList);
        review.put("uuid", String.valueOf(id));
        review.put("date", dateFormat);

        documentReference.set(review).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ReviewActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(ReviewActivity.this, AttractionDetailsUser.class);
                intent.putExtra("Image", imageURL);
                intent.putExtra("Name", name_);
                intent.putExtra("Description", description_);
                intent.putExtra("City", city_);
                intent.putExtra("Country", country_);
                intent.putExtra("Latitude",latitude);
                intent.putExtra("Longitude", longitude);
                intent.putExtra("Uuid", uuid);
                intent.putExtra("date", dateFormat);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null)
        {
            if(data.getClipData()!=null)
            {
                int count = data.getClipData().getItemCount();
                for(int i =0; i<count; i++)
                {
                    Uri imageURI = data.getClipData().getItemAt(i).getUri();
                    uriArrayList.add(imageURI);
                }
            }
            else
            {
                Uri imageURI = data.getData();
                uriArrayList.add(imageURI);
            }
        }
    }
}