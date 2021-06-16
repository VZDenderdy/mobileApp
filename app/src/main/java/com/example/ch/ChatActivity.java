package com.example.ch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String mChatUser;

    private DatabaseReference mRootRef;

    private TextView chatName;
    private CircleImageView chatImage;

    private FirebaseAuth mAuth;
    private String mCurrentUserId;

    private ImageButton mChatSendBtn;
    private EditText mChatMessageView;

    private RecyclerView mMessagesList;
    private SwipeRefreshLayout mRefreshLayout;

    private List<messages> messagesList = new ArrayList<>();

    private LinearLayoutManager mLinearLayout;

    private MessageAdapter mAdapter;

    private static  final int TOTAL_ITEMS_TO_LOAD = 30;
    private  int mCurrentPage =1;

    private int itemPos =0;

    private String mLastKey ="";
    private String mPrevKey = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
    }

    private void init(){
        mChatUser = getIntent().getStringExtra("user_id");
        String userName = getIntent().getStringExtra("user_name");

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.chat_app_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mChatMessageView = findViewById(R.id.chat_message_view);
        mChatSendBtn = (ImageButton) findViewById(R.id.chat_send_btn);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth =FirebaseAuth.getInstance();
        mCurrentUserId=mAuth.getCurrentUser().getUid();

        actionBar.setTitle(userName);
        LayoutInflater inflater =(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(action_bar_view);

        chatName = findViewById(R.id.chatName);
        chatImage = findViewById(R.id.custom_bar_image);
        chatName.setText(userName);


        mAdapter = new MessageAdapter(messagesList);

        mMessagesList = findViewById(R.id.messages_list);
        mRefreshLayout = findViewById(R.id.message_swipe_layout);
        mLinearLayout = new LinearLayoutManager(this);
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);
        mMessagesList.setAdapter(mAdapter);

       loadMessages();









        mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               String image=snapshot.child("image").getValue().toString();

               if(!image.equals("default")){
                   Picasso.get().load(image).into(chatImage);
               }


           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });




       mChatSendBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               sendMessage();
           }
       });

       mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
           public void onRefresh() {

                mCurrentPage++;
                itemPos = 0;
               loadMoreMessage();

           }
       });


    }

    private  void loadMoreMessage(){
        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                messages message = snapshot.getValue(messages.class);

                if(!mPrevKey.equals(snapshot.getKey())) {
                    messagesList.add(itemPos++, message);
                }else{
                    mPrevKey = snapshot.getKey();
                }


                if(itemPos ==1){
                    mLastKey = snapshot.getKey();
                }

                mAdapter.notifyDataSetChanged();


                mRefreshLayout.setRefreshing(false);
                mLinearLayout.scrollToPositionWithOffset(TOTAL_ITEMS_TO_LOAD, 0);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mRootRef.child("Chat").child(mCurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild(mChatUser)){
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);
                    chatAddMap.put("last_time", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/"+mCurrentUserId+"/"+mChatUser, chatAddMap);
                    chatUserMap.put("Chat/"+mChatUser+"/"+mCurrentUserId, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                        }
                    });
                    mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("seen").setValue(true);

                }else{
                    mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("seen").setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadMessages(){

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);
        Query messageQuery = messageRef.limitToLast(mCurrentPage*TOTAL_ITEMS_TO_LOAD);


        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {



                messages message = snapshot.getValue(messages.class);


                itemPos++;

                if(itemPos ==1){
                    mLastKey = snapshot.getKey();
                    mPrevKey = mLastKey;
                }


                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                mMessagesList.scrollToPosition(messagesList.size()-1);

                mRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void sendMessage(){
        String message = mChatMessageView.getText().toString();
        if(!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/"+mCurrentUserId+"/"+mChatUser;
            String chat_user_ref  = "messages/"+mChatUser+"/"+mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages").child(mCurrentUserId)
                    .child(mChatUser).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("sender", mCurrentUserId);
            messageMap.put("push_id", push_id);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref+"/"+push_id, messageMap );
            messageUserMap.put(chat_user_ref+"/"+push_id, messageMap );


            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                }
            });

            mChatMessageView.setText("");
            mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("last_time").setValue(ServerValue.TIMESTAMP);
            mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("last_time").setValue(ServerValue.TIMESTAMP);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        Map<String, Object> data = new HashMap<>();
        data.put("seen", false);

        mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).updateChildren(data);
    }
}