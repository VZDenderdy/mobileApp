package com.example.ch;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdditionalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdditionalFragment extends Fragment {


    private RecyclerView mFriendList;
    private View mMainView;

    private FirebaseAuth mAuth;
    private String mCurrentID;

    private DatabaseReference mFriendDatabase;
    private DatabaseReference mUserDatabase;

    private ArrayList<Users> list;

    private FriendAdapter myFriendAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AdditionalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdditionalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdditionalFragment newInstance(String param1, String param2) {
        AdditionalFragment fragment = new AdditionalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView= inflater.inflate(R.layout.fragment_additional, container, false);
        mFriendList=mMainView.findViewById(R.id.friends_list);

        mAuth = FirebaseAuth.getInstance();
        mCurrentID=mAuth.getCurrentUser().getUid();

        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentID);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mFriendList.setHasFixedSize(true);
        mFriendList.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        myFriendAdapter=new FriendAdapter(list, getContext(), mUserDatabase);
        mFriendList.setAdapter(myFriendAdapter);

        /*

        mFriendDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userID = dataSnapshot.getKey();
                    Users friend  = new Users();
                    friend.userID = userID;
                    list.add(friend);
                }

                myFriendAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

         */

        mFriendDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String userID = snapshot.getKey();
                Users friend  = new Users();
                friend.userID = userID;
                list.add(friend);
                myFriendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String userID = snapshot.getKey();
                for(int i=0;i<list.size();i++){
                    if(list.get(i).userID.equals(userID)){
                        list.remove(i);
                        Log.d("VseOk", "ok");
                        break;
                    }
                }
                myFriendAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        return mMainView;
    }

}