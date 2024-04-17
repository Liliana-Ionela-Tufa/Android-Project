package com.example.licenta2024;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    android.content.Context context;
    ArrayList<DataClass> dataClass;

    public MyAdapter(Context context, ArrayList<DataClass> dataClass) {
        this.context = context;
        this.dataClass = dataClass;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        DataClass data = dataClass.get(position);
        Glide.with(context).load(data.getImageURL()).into(holder.image);
        holder.name.setText(data.getDataName());
        holder.type.setText(data.getDataDescription());
        holder.city.setText(data.getDataCity());

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


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name, type, city;
        CardView item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameR);
            image = itemView.findViewById(R.id.imageR);
            type = itemView.findViewById(R.id.typeR);
            city = itemView.findViewById(R.id.cityR);
            item = itemView.findViewById(R.id.item);
        }
    }
}
