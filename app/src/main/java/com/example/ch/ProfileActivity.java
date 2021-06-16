package com.example.ch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileName, profileStatus;
    private CircleImageView profileImage;
    private Button profileSendBtn;
    private DatabaseReference mUsersDatabase;

    private String mCurrent_state;
    private DatabaseReference mFriendReqDatabase;
    private FirebaseUser mCurrentUser;

    private DatabaseReference mFriendDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
    }

    private void init(){

        String profile_id = getIntent().getStringExtra("user_id");

        profileSendBtn = findViewById(R.id.profileSendButton);
        profileName = findViewById(R.id.profileName);
        profileImage = findViewById(R.id.profile_image);
        profileStatus = findViewById(R.id.profile_status);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(profile_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("FriendReq");
        mCurrent_state = "not_friends";
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                String image = snapshot.child("image").getValue().toString();

                profileName.setText(name);
                profileStatus.setText(status);
                if(!image.equals("default")) {
                    Picasso.get().load(image).into(profileImage);
                }
                mFriendReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.hasChild(profile_id)){
                            String req_type = snapshot.child(profile_id).child("request_type")
                                    .getValue().toString();

                            if(req_type.equals("received")){
                                mCurrent_state="req_received";
                                profileSendBtn.setText("Accept friend request");

                            }else if(req_type.equals("sent")){
                                mCurrent_state = "req_sent";
                                profileSendBtn.setText("Cancel Friend Request");
                            }


                        }else{

                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if(snapshot.hasChild(profile_id)){

                                        mCurrent_state = "friends";
                                        profileSendBtn.setText("Unfriend");

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profileSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profileSendBtn.setEnabled(false);

                //------------------------NOT FRIENDS------------
                if(mCurrent_state.equals("not_friends")){
                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(profile_id)
                            .child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                mFriendReqDatabase.child(profile_id).child(mCurrentUser.getUid())
                                        .child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        mCurrent_state = "req_sent";
                                        profileSendBtn.setText("Cancel friend request");

                                    }
                                });

                            }
                            profileSendBtn.setEnabled(true);
                        }
                    });
                }


                //----------------CANCEL FRIEND REQUEST------------
                else if(mCurrent_state.equals("req_sent")){
                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(profile_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendReqDatabase.child(profile_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                     mCurrent_state = "not_friends";
                                     profileSendBtn.setText("send request");
                                     profileSendBtn.setEnabled(true);
                                }
                            });
                        }
                    });
                }

                //--------------REQ RECEIVED----------------
                else if(mCurrent_state.equals("req_received")){

                    String current_date = DateFormat.getDateInstance().format(new Date());
                    mFriendDatabase.child(mCurrentUser.getUid()).child(profile_id)
                            .setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendDatabase.child(profile_id).child(mCurrentUser.getUid())
                                    .setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(profile_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendReqDatabase.child(profile_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    profileSendBtn.setEnabled(true);
                                                    mCurrent_state = "friends";
                                                    profileSendBtn.setText("Unfriend");
                                                }
                                            });
                                        }
                                    });



                                }
                            });

                        }
                    });

                }


                //---------------UNFRIEND-------
                else{
                    mFriendReqDatabase.child(mCurrentUser.getUid()).child(profile_id).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendReqDatabase.child(profile_id).child(mCurrentUser.getUid()).child("request_type").setValue("sent").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendDatabase.child(profile_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendDatabase.child(mCurrentUser.getUid()).child(profile_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mCurrent_state = "req_received";
                                                    profileSendBtn.setText("Accept friend request");
                                                    profileSendBtn.setEnabled(true);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }




            }
        });



    }

}