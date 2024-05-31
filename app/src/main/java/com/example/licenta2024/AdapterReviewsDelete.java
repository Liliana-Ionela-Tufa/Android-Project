package com.example.licenta2024;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterReviewsDelete extends RecyclerView.Adapter<AdapterReviewsDelete.ViewHolder> {

    ArrayList<DataClass> dataClass;

    Context context;

    public  AdapterReviewsDelete(Context context, ArrayList<DataClass>dataClass){
        this.context = context;
        this.dataClass = dataClass;
    }

    @NonNull
    @Override
    public AdapterReviewsDelete.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_review_delete, parent, false);
        return new AdapterReviewsDelete.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterReviewsDelete.ViewHolder holder, int position) {
        DataClass data = dataClass.get(position);
        String userID = data.getUserID();
        if(userID.length()!=0) {
            FirebaseFirestore fStore;
            fStore = FirebaseFirestore.getInstance();
            DocumentReference documentReference = fStore.collection("users").document(userID);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                    Glide.with(context).load(documentSnapshot.getString("imageURL")).into(holder.image);
                    holder.name.setText(documentSnapshot.getString("lastName"));
                }
            });


        }
        holder.title.setText(data.getTitle());
        holder.description.setText(data.getReview());
        AdapterPhotos  adapterPhotos;

        holder.recyclerView.hasFixedSize();
        holder.recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        adapterPhotos = new AdapterPhotos(data.getPicturesURL());
        holder.recyclerView.setAdapter(adapterPhotos);
        adapterPhotos.notifyDataSetChanged();

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore fStore;
                fStore = FirebaseFirestore.getInstance();
                fStore.collection("reviews").document(data.getReviewID())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @SuppressLint("NotifyDataSetChanged")
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

                for(String URL : data.getPicturesURL())
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
                            //Toast.makeText(context, "Picture not deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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
        TextView name, title, description;
        CardView itemReview;
        RecyclerView recyclerView;

        ImageButton delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageUser);
            name = itemView.findViewById(R.id.username);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            itemReview = itemView.findViewById(R.id.item_review);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            delete = itemView.findViewById(R.id.delete);

        }

    }
}

