package com.example.ch;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private RecyclerView mSubjectList;
    private View mMainView;

    //private FirebaseAuth mAuth;
    //private String mCurrentID;

    private DatabaseReference mSubjectDatabase;
    //private DatabaseReference mUserDatabase;

    private ArrayList<String> list;

    private SubjectsAdapter mySubjectsAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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

        mMainView= inflater.inflate(R.layout.fragment_main, container, false);
        mSubjectList=mMainView.findViewById(R.id.list_subjects);

       // mAuth = FirebaseAuth.getInstance();
        //mCurrentID=mAuth.getCurrentUser().getUid();

        mSubjectDatabase = FirebaseDatabase.getInstance().getReference().child("Subjects");
        //mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mSubjectList.setHasFixedSize(true);
        mSubjectList.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        mySubjectsAdapter=new SubjectsAdapter(list, getContext(), mSubjectDatabase);
        mSubjectList.setAdapter(mySubjectsAdapter);






        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mSubjectDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    String name = dataSnapshot.getKey();
                    list.add(name);

                }

                mySubjectsAdapter.notifyDataSetChanged();

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