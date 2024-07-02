package com.example.licenta2024;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

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

        Button cancel, delete;

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_box_delete_attraction);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_bg));
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
                FirebaseFirestore fStore;
                fStore = FirebaseFirestore.getInstance();
                fStore.collection("touristic-attraction").document(data.getUuid())
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


                fStore.collection("reviews").whereEqualTo("attractionID", data.getUuid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                                        Toast.makeText(context, "Picture not deleted", Toast.LENGTH_SHORT).show();
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
                String URL = data.getImageURL();
                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(URL);
                reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Toast.makeText(context, "Picture deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(context, "Picture not deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                dataClass.remove(holder.getAbsoluteAdapterPosition());
                notifyItemRemoved(holder.getAbsoluteAdapterPosition());
                notifyDataSetChanged();
                dialog.dismiss();

            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
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

    ImageButton delete, edit;

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