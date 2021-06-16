package com.example.ch;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class FileCreate extends AppCompatActivity {

    private RecyclerView mList;

    ArrayList<String> link;

    private FloatingActionButton addImage, addDocument , addText;

    private ArrayList<createFile> list =new ArrayList<>();

    private CreateFileAdapter createFileAdapter;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_create);
        init();
    }

    private void init(){




        addImage = findViewById(R.id.addImageBtn);
        addDocument = findViewById(R.id.addDocumentBtn);
        addText = findViewById(R.id.addTextBtn);

        mList = findViewById(R.id.containFileList);
        mList.setHasFixedSize(true);
        mList.setLayoutManager(new LinearLayoutManager(this));



        createFileAdapter=new CreateFileAdapter(list);
        mList.setAdapter(createFileAdapter);
        link = Singleton.getInstance().value;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Subjects");
        for(int i=0;i<link.size();i++){
            databaseReference = databaseReference.child(link.get(i));
        }

        databaseReference.child("File").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.child("type").getValue().toString().equals("0")){
                    createFile file = new createFile();
                    file.setType(0);
                    list.add(file);
                    createFileAdapter.notifyDataSetChanged();
                }
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


        addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //list = Singleton.getInstance().value;
                createFile text = new createFile();
                text.setType(0);
                text.setDelete(false);
                String key = databaseReference.child("File").push().getKey();
                databaseReference.child("File").child(key).child("type").setValue("0");
                databaseReference.child("File").child(key).child("text").setValue("");
                text.setDatabaseReference(databaseReference.child("File").child(key));
                list.add(text);
                //list.add(text);
                //Singleton.getInstance().value = list;
                //Singleton.getInstance().value.add(text);
                //createFileAdapter.notifyDataSetChanged();

            }
        });




/*
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {

                            Intent data = result.getData();
                            Uri imageUri = data.getData();
                            //list = Singleton.getInstance().value;
                            createFile image = new createFile();
                            image.setType(1);
                            image.setDelete(false);
                            image.setUri(imageUri.toString());
                            list.add(image);
                            createFileAdapter.notifyDataSetChanged();

                        }
                    }
                });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                someActivityResultLauncher.launch(galleryIntent);
            }
        });





 */
    }

    public void setVisibility(Boolean b){
        if(b){
            addImage.setVisibility(View.VISIBLE);
            addText.setVisibility(View.VISIBLE);
            addDocument.setVisibility(View.VISIBLE);
        }else{
            addImage.setVisibility(View.GONE);
            addText.setVisibility(View.GONE);
            addDocument.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        link.remove(link.size()-1);
        Singleton.getInstance().value = link;
    }
}