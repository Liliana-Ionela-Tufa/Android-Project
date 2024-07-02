package com.example.licenta2024;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    public AdapterDeleteUser(Context context, ArrayList<DataClass> dataClass) {
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
        Log.d("userID", userID);

        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException error) {
                Glide.with(context).load(documentSnapshot.getString("imageURL")).into(holder.image);
                holder.name.setText(documentSnapshot.getString("lastName") + " " + documentSnapshot.getString("firstName"));
                holder.email.setText(documentSnapshot.getString("email"));
                ImageURL = documentSnapshot.getString("imageURL");
            }
        });

        Button cancel, delete;
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_box_delete_acc_admin);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
                deleteUser(userID, holder, dialog);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }

    private void deleteUser(String userID, ViewHolder holder, Dialog dialog) {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        fStore.collection("users").document(userID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Deleted", "User document successfully deleted!");
                        if (ImageURL != null && !ImageURL.isEmpty()) {
                            deleteImage(ImageURL, new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    deleteFavorites(userID, holder, dialog);
                                }
                            });
                        } else {
                            deleteFavorites(userID, holder, dialog);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Not deleted", "Error deleting user document", e);
                    }
                });
    }

    private void deleteFavorites(String userID, ViewHolder holder, Dialog dialog) {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("favorites").whereEqualTo("user", userID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        WriteBatch batch = fStore.batch();
                        for (DocumentSnapshot snap : task.getResult()) {
                            batch.delete(snap.getReference());
                        }
                        batch.commit()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("favorites", "Favorites successfully deleted");
                                        deleteReviews(userID, holder, dialog);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("favorites", "Error deleting favorites", e);
                                    }
                                });
                    } else {
                        Log.w("favorites", "Error finding favorites", task.getException());
                    }
                });
    }

    private void deleteReviews(String userID, ViewHolder holder, Dialog dialog) {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("reviews").whereEqualTo("userID", userID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        WriteBatch batch = fStore.batch();
                        for (DocumentSnapshot snap : task.getResult()) {
                            List<String> list = (List<String>) snap.get("picturesURL");
                            if (list != null) {
                                for (String URL : list) {
                                    deleteImage(URL, null);
                                }
                            }
                            batch.delete(snap.getReference());
                        }
                        batch.commit()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("reviews", "Reviews successfully deleted");
                                        finishDeletion(holder, dialog);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("reviews", "Error deleting reviews", e);
                                    }
                                });
                    } else {
                        Log.w("reviews", "Error finding reviews", task.getException());
                    }
                });
    }

    private void deleteImage(String URL, OnSuccessListener<Void> onSuccessListener) {
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(URL);
        reference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Image", "Image successfully deleted");
                        if (onSuccessListener != null) {
                            onSuccessListener.onSuccess(aVoid);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Image", "Error deleting image", e);
                    }
                });
    }

    private void finishDeletion(ViewHolder holder, Dialog dialog) {
        dataClass.remove(holder.getAbsoluteAdapterPosition());
        notifyItemRemoved(holder.getAbsoluteAdapterPosition());
        notifyDataSetChanged();
        dialog.dismiss();
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
