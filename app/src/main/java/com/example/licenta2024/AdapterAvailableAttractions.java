package com.example.licenta2024;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterAvailableAttractions extends RecyclerView.Adapter<AdapterAvailableAttractions.ViewHolder> {

    ArrayList<DataAttractionAvailability>dataList;
    ArrayList<DataAttractionAvailability> fullDataList;
    Context context;

    public AdapterAvailableAttractions (Context context, ArrayList<DataAttractionAvailability>dataList )
    {
        this.context = context;
        this.dataList = dataList;
    }

    public AdapterAvailableAttractions (Context context, ArrayList<DataAttractionAvailability>dataList,  ArrayList<DataAttractionAvailability> fullDataList  )
    {
        this.context = context;
        this.dataList = dataList;
        this.fullDataList = fullDataList;
    }
    @NonNull
    @Override
    public AdapterAvailableAttractions.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_check, parent, false);
        return new AdapterAvailableAttractions.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAvailableAttractions.ViewHolder holder, int position) {
        DataAttractionAvailability data = dataList.get(position);
        holder.name.setText(data.getName());
        holder.availability.setText(data.getAvailability());
        holder.city.setText(data.getCity());

        holder.checkBox.setChecked(data.isChecked());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            data.setChecked(isChecked);
            dataList.set(position, data);
            updateFullDataList(data);
        });


    }

    private void updateFullDataList(DataAttractionAvailability updatedData) {
        if (fullDataList != null) {
            for (int i = 0; i < fullDataList.size(); i++) {
                if (fullDataList.get(i).getAttractionID().equals(updatedData.getAttractionID())) {
                    fullDataList.set(i, updatedData);
                    break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, city, availability;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            availability = itemView.findViewById(R.id.description);
            name = itemView.findViewById(R.id.name);
            city = itemView.findViewById(R.id.city);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
