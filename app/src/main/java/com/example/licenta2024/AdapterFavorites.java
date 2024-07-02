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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdapterFavorites extends RecyclerView.Adapter<AdapterFavorites.ViewHolder> {

    android.content.Context context;
    ArrayList<DataClass> dataClass;

    public AdapterFavorites(Context context, ArrayList<DataClass> dataClass) {
        this.context = context;
        this.dataClass = dataClass;
    }

    @NonNull
    @Override
    public AdapterFavorites.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFavorites.ViewHolder holder, int position) {
        DataClass data = dataClass.get(position);
        Glide.with(context).load(data.getImageURL()).into(holder.image);
        holder.name.setText(data.getDataName());
        holder.type.setText(data.getDataType());
        holder.city.setText(data.getDataCity());
        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AttractionDetailsUser.class );
                intent.putExtra("Image", dataClass.get(holder.getAbsoluteAdapterPosition()).getImageURL());
                intent.putExtra("Name", dataClass.get(holder.getAbsoluteAdapterPosition()).getDataName());
                intent.putExtra("Description", dataClass.get(holder.getAbsoluteAdapterPosition()).getDataDescription());
                intent.putExtra("City", dataClass.get(holder.getAbsoluteAdapterPosition()).getDataCity());
                intent.putExtra("Country", dataClass.get(holder.getAbsoluteAdapterPosition()).getDataCountry());
                intent.putExtra("Type", dataClass.get(holder.getAbsoluteAdapterPosition()).getDataType());
                intent.putExtra("Latitude", dataClass.get(holder.getAbsoluteAdapterPosition()).getLatitude());
                intent.putExtra("Longitude", dataClass.get(holder.getAbsoluteAdapterPosition()).getLongitude());
                intent.putExtra("Uuid", dataClass.get(holder.getAbsoluteAdapterPosition()).getUuid());
                context.startActivity(intent);
            }
        });

        final String key = fAuth.getCurrentUser().getUid() + dataClass.get(holder.getAbsoluteAdapterPosition()).getUuid();
        holder.favoriteChecker(key);

        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                String name = dataClass.get(holder.getAbsoluteAdapterPosition()).getDataName();
                String desc = dataClass.get(holder.getAbsoluteAdapterPosition()).getDataDescription();
                String type = dataClass.get(holder.getAbsoluteAdapterPosition()).getDataType();
                String city = dataClass.get(holder.getAbsoluteAdapterPosition()).getDataCity();
                String country = dataClass.get(holder.getAbsoluteAdapterPosition()).getDataCountry();
                String latitude = dataClass.get(holder.getAbsoluteAdapterPosition()).getLatitude();
                String longitude = dataClass.get(holder.getAbsoluteAdapterPosition()).getLongitude();
                String uuid = dataClass.get(holder.getAbsoluteAdapterPosition()).getUuid();
                String imageURL = dataClass.get(holder.getAbsoluteAdapterPosition()).getImageURL();
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                String userID = fAuth.getCurrentUser().getUid();

                FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                fStore.collection("favorites").document(userID + uuid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            holder.favorite.setImageResource(R.drawable.baseline_favorite_24);
                            DocumentReference documentReference = fStore.collection("favorites").document(userID + uuid);
                            Map<String, Object> favorite = new HashMap<>();
                            favorite.put("name", name);
                            favorite.put("desc", desc);
                            favorite.put("type", type);
                            favorite.put("city", city);
                            favorite.put("country", country);
                            favorite.put("latitude", latitude);
                            favorite.put("longitude", longitude);
                            favorite.put("imageURL", imageURL);
                            favorite.put("uuid", String.valueOf(uuid));
                            favorite.put("user", userID);
                            documentReference.set(favorite).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            holder.favorite.setImageResource(R.drawable.outline_favorite_border_24);
                            fStore.collection("favorites").document(userID + uuid)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Deleted", "DocumentSnapshot successfully deleted!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Not deleted", "Error deleting document", e);
                                        }
                                    });
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataClass.size();
    }

    public void searchDataList(ArrayList<DataClass> searchList)
    {
        dataClass = searchList;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name, type, city;
        CardView item;
        ImageButton favorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameR);
            image = itemView.findViewById(R.id.imageR);
            type = itemView.findViewById(R.id.typeR);
            city = itemView.findViewById(R.id.cityR);
            item = itemView.findViewById(R.id.item);
            favorite = itemView.findViewById(R.id.favoriteButton);
        }

        public void favoriteChecker(String key) {
            favorite = itemView.findViewById(R.id.favoriteButton);
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            fStore.collection("favorites").document(key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists() == false) {
                        favorite.setImageResource(R.drawable.outline_favorite_border_24);
                    } else {
                        favorite.setImageResource(R.drawable.baseline_favorite_24);
                    }

                }
            });
        }

    }
}
