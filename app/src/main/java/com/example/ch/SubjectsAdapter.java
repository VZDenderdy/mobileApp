package com.example.ch;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.MyViewHolder> {

    private ArrayList<String> list;

    private Context context;

    private DatabaseReference subjectDatabase;

    public SubjectsAdapter(ArrayList<String> list, Context context, DatabaseReference databaseReference){
        this.context=context;
        this.list=list;
        this.subjectDatabase = databaseReference;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subject_single_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String name = list.get(position);

        holder.setName(name);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(v.getContext(), Files.class);
                ArrayList<String> link = new ArrayList<>();
                link.add(name);
                //Intent.putExtra("ref", link);
                Singleton.getInstance().value = link;
                v.getContext().startActivity(Intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  static  class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setName(String name){
            TextView userName = mView.findViewById(R.id.subject_name);
            userName.setText(name);
        }




    }


}
