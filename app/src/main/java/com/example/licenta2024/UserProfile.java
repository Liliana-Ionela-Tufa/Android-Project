package com.example.licenta2024;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UserProfile extends AppCompatActivity{

    DrawerLayout drawerLayout;
    ImageButton drawerButton, backButton;
    Button cancel, delete;
    Dialog dialog;

    TextView email, name, phone;

    ImageView profilePicture;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    String imageURL;

    RecyclerView recyclerView;
    AdapterReviewsDelete adapterReviewsDelete;

    ArrayList<DataClass> dataList;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerButton = findViewById(R.id.buttonDrawer);
        backButton = findViewById(R.id.goBack);

        drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.open();
            }
        });

        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        profilePicture = findViewById(R.id.userPicture);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        String userID = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                email.setText(documentSnapshot.getString("email"));
                name.setText(documentSnapshot.getString("firstName") + " " + documentSnapshot.getString("lastName"));
                phone.setText(documentSnapshot.getString("phone"));
                Glide.with(UserProfile.this).load(documentSnapshot.getString("imageURL")).into(profilePicture);
                imageURL = documentSnapshot.getString("imageURL");


            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this, NewMainActivity.class);
                startActivity(intent);
            }
        });


        dialog = new Dialog(UserProfile.this);
        dialog.setContentView(R.layout.custom_dialog_box_delete_acc);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_bg));
        dialog.setCancelable(false);


        cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        delete = dialog.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                String userID = user.getUid();

                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("user sters", "User account deleted.");
                                }
                            }
                        });

                fStore.collection("users").document(userID)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Deleted", "DocumentSnapshot successfully deleted!");
                                Intent intent = new Intent(UserProfile.this, Login.class);
                                startActivity(intent);


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Not deleted", "Error deleting document", e);
                            }
                        });

                fStore.collection("favorites").whereEqualTo("user", userID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshot) {
                        WriteBatch batch = FirebaseFirestore.getInstance().batch();
                        List<DocumentSnapshot> snapshotList = snapshot.getDocuments();
                        for(DocumentSnapshot snap : snapshotList)
                        {
                            batch.delete(snap.getReference());
                        }
                        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("favorites", "deleted");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("favorites", "favorites not deleted");
                            }
                        });
                    }
                });
                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(imageURL);
                reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UserProfile.this, "Picture deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfile.this, "Picture not deleted", Toast.LENGTH_SHORT).show();
                    }
                });

                fStore.collection("reviews").whereEqualTo("userID", userID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshot) {
                        WriteBatch batch = FirebaseFirestore.getInstance().batch();
                        List<DocumentSnapshot> snapshotList = snapshot.getDocuments();
                        for(DocumentSnapshot snap : snapshotList)
                        {
                            ArrayList<String>list = (ArrayList<String>) snap.get("picturesURL");
                            for(String URL : list)
                            {
                                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(URL);
                                reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //Toast.makeText(context, "Picture deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Toast.makeText(, "Picture not deleted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            batch.delete(snap.getReference());
                        }
                        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("reviews", "deleted");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("reviews", "reviews not deleted");
                            }
                        });
                    }
                });
                dialog.dismiss();
                Intent intent = new Intent(UserProfile.this, Login.class);
                startActivity(intent);
            }


        });



        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //Log.d("UserProfile", "Item clicked: " + item.getItemId());
                if(item.getItemId()==R.id.nav_logout)
                {
                    Log.d("UserProfile", "Item clicked: " + item.getItemId());
                    Toast.makeText(UserProfile.this, "log out clicked", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(UserProfile.this, Login.class);
                    UserProfile.this.startActivity(intent);
                    return true;
                }

                else if (item.getItemId()==R.id.nav_edit) {
                    Intent intent = new Intent(UserProfile.this, EditUserProfile.class);
                    intent.putExtra("email", email.getText());
                    intent.putExtra("photo", imageURL);
                    startActivity(intent);

                }

                else if (item.getItemId()==R.id.nav_delete)
                {
                        dialog.show();
                }
                else if (item.getItemId()==R.id.nav_route) {
                    Intent intent = new Intent(UserProfile.this, CreateRoute.class);
                    startActivity(intent);
                }
                else if (item.getItemId()==R.id.nav_view){
                    Intent intent = new Intent(UserProfile.this, SeeRoutes.class);
                    startActivity(intent);
                }

                return true;
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<DataClass>();
        adapterReviewsDelete = new AdapterReviewsDelete(UserProfile.this, dataList);
        recyclerView.setAdapter(adapterReviewsDelete);

        Event();

    }

    private void Event(){

        fAuth = FirebaseAuth.getInstance();
        String userID = fAuth.getCurrentUser().getUid();
        fStore.collection("reviews").whereEqualTo("userID", userID).get()
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
