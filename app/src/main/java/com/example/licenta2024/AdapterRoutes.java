package com.example.licenta2024;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdapterRoutes extends RecyclerView.Adapter<AdapterRoutes.ViewHolder> {

    Context context;
    ArrayList<DataAttractionAvailability>dataList;
    FirebaseFirestore fStore;
    boolean deleted = false;
    Integer count = 0;
    ArrayList<String>list = new ArrayList<>();




    public AdapterRoutes(Context context, ArrayList<DataAttractionAvailability> dataList) {
        this.context = context;
        this.dataList = dataList;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_route, parent, false);
        return new AdapterRoutes.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataAttractionAvailability data = dataList.get(position);
        Log.d("text", "See what you chose to visit on " + data.getDate());
        fStore = FirebaseFirestore.getInstance();
        holder.name.setText("See what you chose to visit on " + data.getDate());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(context, DetailsRoute.class);
//                intent.putExtra("id", data.getAttractionID());
//                context.startActivity(intent);

                fStore.collection("attractions-day-plan").document(data.getAttractionID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            list = (ArrayList<String>) document.get("attrIDS");
                            count = 0;
                            deleted = false;
                            if (document.exists()) {
                                for(String id : list)
                            {

                                Log.d("id", id);
                                fStore.collection("touristic-attraction").whereEqualTo("uuid",id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @SuppressLint("SuspiciousIndentation")
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        QuerySnapshot documentSnapshot = task.getResult();
                                        if(!documentSnapshot.isEmpty())
                                            count+=1;
                                        else
                                            deleted = true;
                                            if(count == list.size() && deleted==false) {

                                                seeDetails(data);
                                            }

                                    }

                                });


                            }
                                if(deleted==true)
                                    Toast.makeText(context, "One of the tourist attractions was deleted. You cannot access this route.", Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });

            }
        });
    }

//                fStore.collection("attractions-day-plan").whereEqualTo("uuid", data.getAttractionID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        for(DocumentSnapshot snap : task.getResult())
//                        {
//                            count=0;
//                            list = (ArrayList<String>) snap.get("attrIDS");
//                            for(String id : list)
//                            {
//
//                                Log.d("id", id);
//                                fStore.collection("touristic-attraction").whereEqualTo("uuid",id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                        QuerySnapshot documentSnapshot = task.getResult();
//                                        if(!documentSnapshot.isEmpty())
//                                            count+=1;
//                                    }
//
//                                });
//
//
//                            }
//                            if(count==list.size())
//                                seeDetails(data, deleted);
//                        }
////                        if(count!=list.size())
////                            Toast.makeText(context, "One of the tourist attractions was deleted. You cannot access this route.", Toast.LENGTH_LONG).show();
//
//                    }
//
//                });
//
//            }
//        });
//
//
//    }


    public void seeDetails(DataAttractionAvailability data) {
            Intent intent = new Intent(context, DetailsRoute.class);
            intent.putExtra("id", data.getAttractionID());
            context.startActivity(intent);

    }

    @Override

    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        CardView item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            item = itemView.findViewById(R.id.item_route);
        }
    }

}
