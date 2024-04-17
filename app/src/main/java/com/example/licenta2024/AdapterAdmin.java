package com.example.licenta2024;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterAdmin extends RecyclerView.Adapter<ViewHolderAdmin> {

    android.content.Context context;
    static ArrayList<DataClass> dataClass;

    public AdapterAdmin(Context context, ArrayList<DataClass> dataClass) {
        this.context = context;
        this.dataClass = dataClass;
    }


    @NonNull
    @Override
    public ViewHolderAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin, parent, false);
        return new ViewHolderAdmin(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAdmin holder, int position) {
        DataClass data = dataClass.get(position);
        Glide.with(context).load(data.getImageURL()).into(holder.image);
        holder.name.setText(data.getDataName());
        holder.type.setText(data.getDataType());
        holder.city.setText(data.getDataCity());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore fAuth;
                fAuth = FirebaseFirestore.getInstance();
                fAuth.collection("touristic-attraction").document(data.getUuid())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Deleted", "DocumentSnapshot successfully deleted!");
                                Intent intent = new Intent(context, UpdateDeleteActivity.class);
                                context.startActivity(intent);


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Not deleted", "Error deleting document", e);
                            }
                        });
                String URL = data.getImageURL();
                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(URL);
                reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Picture deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Picture not deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateActivity.class);
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

}

class ViewHolderAdmin extends RecyclerView.ViewHolder{

    ImageView image;
    TextView name, type, city;
    CardView item;

    Button delete, edit;

    public ViewHolderAdmin(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.nameR);
        image = itemView.findViewById(R.id.imageR);
        type = itemView.findViewById(R.id.typeR);
        city = itemView.findViewById(R.id.cityR);
        item = itemView.findViewById(R.id.item_admin);
        delete = itemView.findViewById(R.id.delete);
        edit = itemView.findViewById(R.id.edit);

    }

}