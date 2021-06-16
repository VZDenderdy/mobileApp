package com.example.ch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private ArrayList<Users> list;
    private Context context;
    private DatabaseReference mDatabase;

    public ChatAdapter(ArrayList<Users> list, Context context, DatabaseReference mDatabase) {
        this.list = list;
        this.context=context;
        this.mDatabase = mDatabase;

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

        String user_id =  user.userID;

        mDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = snapshot.child("name").getValue().toString();
                String userImage = snapshot.child("image").getValue().toString();
                String userStatus = snapshot.child("status").getValue().toString();

                holder.setDisplayName(userName);
                holder.setUserImage(userImage);
                holder.setUserStatus(userStatus);


                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent chatIntent = new Intent(v.getContext(), ChatActivity.class);
                        chatIntent.putExtra("user_id",user_id);
                        chatIntent.putExtra("user_name",userName);
                        v.getContext().startActivity(chatIntent);

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

            CircleImageView userImageView = mView.findViewById(R.id.user_single_image);

            if(!image.equals("default")) {

                Picasso.get().load(image).into(userImageView);

            }else{

                Picasso.get().load(R.drawable.avatarik).into(userImageView);

            }
        }


    }
}
