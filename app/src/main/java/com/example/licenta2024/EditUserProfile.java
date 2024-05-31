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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
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

public class EditUserProfile extends AppCompatActivity {

    ImageView updateImage;
    Button update;
    TextView updateFirstName, updateLastName, updateNumber;
    String imageURL;
    Uri uri;
    TextView textView;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        updateImage = findViewById(R.id.userPicture);
        updateFirstName = findViewById(R.id.firstName);
        updateLastName = findViewById(R.id.lastName);
        updateNumber = findViewById(R.id.phone);
        fStore = FirebaseFirestore.getInstance();
        textView = findViewById(R.id.noedit);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditUserProfile.this, UserProfile.class);
                startActivity(intent);
            }
        });

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String userID = fAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                updateFirstName.setText(documentSnapshot.getString("firstName"));
                updateLastName.setText(documentSnapshot.getString("lastName"));
                updateNumber.setText(documentSnapshot.getString("phone"));
                Glide.with(EditUserProfile.this).load(documentSnapshot.getString("imageURL")).into(updateImage);
                imageURL = documentSnapshot.getString("imageURL");


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
                            updateImage.setImageURI(uri);
                        } else {
                            Toast.makeText(EditUserProfile.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        update =findViewById(R.id.profle_update_btn);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
    }

    public void saveData(){
        if(uri != null) {
            // Upload new image
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("User Pictures")
                    .child(uri.getLastPathSegment());
            AlertDialog.Builder builder = new AlertDialog.Builder(EditUserProfile.this);
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
        String Fname = updateFirstName.getText().toString();
        String Lname = updateLastName.getText().toString();
        String phone = updateNumber.getText().toString();
        String role = "user";
        String email = getIntent().getStringExtra("email");

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String userID = fAuth.getCurrentUser().getUid();




        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.update("email", email,"firstName", Fname, "lastName", Lname, "phone", phone, "role", role, "imageURL", imageURL )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EditUserProfile.this, "Saved", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(EditUserProfile.this, UserProfile.class);
                        Log.d("iax", Fname);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditUserProfile.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();

                    }
                });

    }
}