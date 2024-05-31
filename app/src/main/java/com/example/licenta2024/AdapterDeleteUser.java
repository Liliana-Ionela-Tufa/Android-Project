package com.example.licenta2024;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.internal.zzaf;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdapterDeleteUser extends RecyclerView.Adapter<AdapterDeleteUser.ViewHolder> {

    ArrayList<DataClass> dataClass;

    Context context;
    String ImageURL;

    public  AdapterDeleteUser(Context context, ArrayList<DataClass>dataClass){
        this.context = context;
        this.dataClass = dataClass;
    }



    @NonNull
    @Override
    public AdapterDeleteUser.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_user_delete, parent, false);
        return new AdapterDeleteUser.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDeleteUser.ViewHolder holder, int position) {
        DataClass data = dataClass.get(position);
        String userID = data.getUserID();


        FirebaseFirestore fStore;
        fStore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                Glide.with(context).load(documentSnapshot.getString("imageURL")).into(holder.image);
                holder.name.setText(documentSnapshot.getString("lastName") + " " + documentSnapshot.getString("firstName"));
                holder.email.setText(documentSnapshot.getString("email"));
                ImageURL = documentSnapshot.getString("imageURL");
                Log.d("url", ImageURL);


            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fStore.collection("users").document(userID)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Deleted", "DocumentSnapshot successfully deleted!");
                                //Intent intent = new Intent(UserProfile.this, Login.class);
                                //startActivity(intent);


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Not deleted", "Error deleting document", e);
                            }
                        });

                FirebaseFirestore fStore = FirebaseFirestore.getInstance();
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
                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(ImageURL);
                reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Toast.makeText(UserProfile.this, "Picture deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(UserProfile.this, "Picture not deleted", Toast.LENGTH_SHORT).show();
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
                dataClass.remove(holder.getAbsoluteAdapterPosition());
                notifyItemRemoved(holder.getAbsoluteAdapterPosition());
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataClass.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, email;

        ImageButton delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            delete = itemView.findViewById(R.id.deleteButton);
        }
    }
}


