package com.example.ch;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<messages> mMessageList;
    private FirebaseAuth mAuth;



    public MessageAdapter(List<messages> list){
        mMessageList = list;
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent, false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        messages c = mMessageList.get(position);

        holder.setText(c.getMessage(),c.getSender());

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public  class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageTextSob, messageTextCurr;

        public RelativeLayout layout;
        //public CircleImageView profileImage;

        public MessageViewHolder(View view){
            super(view);

            messageTextSob = view.findViewById(R.id.message_text_layout_sob);
            //profileImage = view.findViewById(R.id.message_profile_layout);
            messageTextCurr=view.findViewById(R.id.message_text_layout_curr);


        }

        public void setText(String text, String id){

            if(id.equals(mAuth.getCurrentUser().getUid())){

                messageTextSob.setVisibility(View.GONE);
                messageTextCurr.setVisibility(View.VISIBLE);
                messageTextCurr.setText(text);



            }else{
                messageTextCurr.setVisibility(View.GONE);
                messageTextSob.setText(text);
                messageTextSob.setVisibility(View.VISIBLE);

            }

        }




    }



}
