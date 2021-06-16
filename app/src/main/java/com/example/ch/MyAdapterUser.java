package com.example.ch;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapterUser extends RecyclerView.Adapter<MyAdapterUser.MyViewHolder> {


    Context context;

    ArrayList<Users> list;

    public MyAdapterUser(Context context, ArrayList<Users> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_single_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Users user = list.get(position);
        holder.setDisplayName(user.name);
        holder.setUserStatus(user.status);
        holder.setUserImage(user.image);

        String user_id =  user.userID;

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(v.getContext(), ProfileActivity.class);
                profileIntent.putExtra("user_id",user_id);
                v.getContext().startActivity(profileIntent);

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

        public void setDisplayName(String name){
            TextView userName = mView.findViewById(R.id.user_single_name);
            userName.setText(name);
        }
        public void setUserStatus(String status){
            TextView userStatus = mView.findViewById(R.id.user_single_status);
            userStatus.setText(status);
        }
        public void setUserImage(String image){
            if(!image.equals("default")) {
                CircleImageView userImageView = mView.findViewById(R.id.user_single_image);
                Picasso.get().load(image).into(userImageView);
            }
        }


    }

}
