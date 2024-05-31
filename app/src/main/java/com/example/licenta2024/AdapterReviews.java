package com.example.licenta2024;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class AdapterReviews extends RecyclerView.Adapter<AdapterReviews.ViewHolder> {

    ArrayList<DataClass> dataClass;

    Context context;

    public  AdapterReviews(Context context, ArrayList<DataClass>dataClass){
        this.context = context;
        this.dataClass = dataClass;
    }

    @NonNull
    @Override
    public AdapterReviews.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterReviews.ViewHolder holder, int position) {
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
        holder.date.setText(data.getDate());
        AdapterPhotos  adapterPhotos;

        holder.recyclerView.hasFixedSize();
        holder.recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        adapterPhotos = new AdapterPhotos(data.getPicturesURL());
        holder.recyclerView.setAdapter(adapterPhotos);
        adapterPhotos.notifyDataSetChanged();



    }

    @Override
    public int getItemCount() {
        return dataClass.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, title, description, date;
        CardView itemReview;
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageUser);
            name = itemView.findViewById(R.id.username);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            itemReview = itemView.findViewById(R.id.item_review);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            date = itemView.findViewById(R.id.date);


        }



    }
}
