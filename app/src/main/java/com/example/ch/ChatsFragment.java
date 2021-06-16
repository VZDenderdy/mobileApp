package com.example.ch;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
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
 * Use the {@link ChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {


    private RecyclerView mChatList;
    private View mMainView;

    private FirebaseAuth mAuth;
    private String mCurrentID;

    private DatabaseReference mChatDatabase;
    private DatabaseReference mUserDatabase;

    private ArrayList<Users> list;

    private ChatAdapter myChatAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatsFragment newInstance(String param1, String param2) {
        ChatsFragment fragment = new ChatsFragment();
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

        mMainView= inflater.inflate(R.layout.fragment_chats, container, false);
        mChatList=mMainView.findViewById(R.id.chat_list);

        mAuth = FirebaseAuth.getInstance();
        mCurrentID=mAuth.getCurrentUser().getUid();
////
        mChatDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrentID);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mChatList.setHasFixedSize(true);
        mChatList.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        myChatAdapter=new ChatAdapter(list, getContext(), mUserDatabase);
        mChatList.setAdapter(myChatAdapter);





        return mMainView;



    }

    @Override
    public void onStart() {
        super.onStart();


        mChatDatabase.orderByChild("last_time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userID = dataSnapshot.getKey();
                    Users friend  = new Users();
                    friend.userID = userID;
                    list.add(0,friend);

                }

                myChatAdapter.notifyDataSetChanged();

                mChatDatabase.orderByChild("last_time").removeEventListener(this);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    @Override
    public void onPause() {
        super.onPause();

        list.clear();


    }
}