package com.example.ch;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Files extends AppCompatActivity {

    private DatabaseReference databaseRef;

    private ArrayList<String> link;

    private RecyclerView mFileList;

    private ArrayList<Folder> list;
    private String name;

    private ProgressDialog progressDialog;

    private FilesAdapter myFilesAdapter;
    ActivityResultLauncher<Intent> someActivityResultLauncher;

    private FloatingActionButton addLinkBtn;
    private FloatingActionButton addFileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        init();
    }
    private void init(){

        //link = getIntent().getStringArrayListExtra("ref");
        link = Singleton.getInstance().value;
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.file_tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(link.get(link.size()-1));

        addLinkBtn = findViewById(R.id.floatingActionButtonLink);
        addFileBtn = findViewById(R.id.floatingActionButtonFile);

        progressDialog = new ProgressDialog(this);


        databaseRef = FirebaseDatabase.getInstance().getReference().child("Subjects");
        for(int i=0;i<link.size();i++){
            databaseRef = databaseRef.child(link.get(i));
        }

        mFileList = findViewById(R.id.file_list);
        mFileList.setHasFixedSize(true);
        mFileList.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        myFilesAdapter=new FilesAdapter(list, this);
        mFileList.setAdapter(myFilesAdapter);

        databaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if(snapshot.hasChild("File")){

                    progressDialog.dismiss();

                    //link.add(snapshot.getKey());
                    Folder folder = new Folder(true, snapshot.child("name").getValue().toString(),link);
                    folder.link.add("file"+snapshot.getKey());////////////////////// file+
                    folder.setData(snapshot.getRef());
                    list.add(folder);
                    //link.remove(link.size()-1);
                }else if(snapshot.getKey().equals("contain")){

                }else{
                    Folder folder = new Folder(false, snapshot.getKey(), link);
                    folder.setData(snapshot.getRef());
                    list.add(folder);
                }
                myFilesAdapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                for(int i=0;i<list.size();i++){
                    if(list.get(i).getName().equals(name)){
                        list.remove(i);
                        myFilesAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });










        addLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogLink();

            }
        });

        addFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFile();

            }
        });
        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {

                            progressDialog.setTitle("Wait...");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();

                            Intent data = result.getData();
                            Uri uri = data.getData();

                            String type = getMimeType(getApplicationContext(),uri);
                            if(type.equals("pdf") || type.equals("docx") ||type.equals("doc")
                                    || type.equals("jpg") ||type.equals("png") ||type.equals("mp4")
                            ||type.equals("txt")) {

                                String key = uri.getLastPathSegment();

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Subjects");
                                for (int i = 0; i < link.size(); i++) {
                                    databaseReference = databaseReference.child(link.get(i));
                                }

                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.hasChild(key)){
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(),"Данный файл уже загружен в эту папку!", Toast.LENGTH_LONG).show();
                                        }else{
                                            StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Files");
                                            for (int i = 0; i < link.size(); i++) {
                                                filepath = filepath.child(link.get(i));
                                            }
                                            filepath = filepath.child("file" + key);

                                            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("Subjects");
                                                            for (int i = 0; i < link.size(); i++) {
                                                                data = data.child(link.get(i));
                                                            }

                                                            Map map = new HashMap<>();
                                                            map.put("name", name);
                                                            map.put("File/Uri", uri.toString());
                                                            map.put("File/type", type);
                                                            data.child(key).updateChildren(map, new DatabaseReference.CompletionListener() {
                                                                @Override
                                                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                                    databaseRef.child("contain").setValue(true);
                                                                }
                                                            });


                                                            //String type = getMimeType(uri);
                                                            //Toast.makeText(getApplicationContext(), type, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Данный формат не поддерживается", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });






    }
    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }


    private void DialogFile(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Enter a name");

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                name = input.getText().toString();
                if(!name.isEmpty()){

                   // Intent intent = new Intent(Files.this, FileCreate.class);



                    //Singleton.getInstance().value.add(key);

                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("*/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                    //startActivity(intent);
                    someActivityResultLauncher.launch(galleryIntent);

                }

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });



        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void DialogLink(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Enter a name");

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String name = input.getText().toString();
                if(!name.isEmpty()){
                    databaseRef.child(name).child("contain").setValue(false);
                    databaseRef.child("contain").setValue(true);
                }

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });



        AlertDialog dialog = alert.create();
        dialog.show();

    }




    @Override
    protected void onPause() {
        super.onPause();
        //list.clear();
        //databaseRef = FirebaseDatabase.getInstance().getReference().child("Subjects");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        link.remove(link.size()-1);
        Singleton.getInstance().value = link;
        //Toast.makeText(this,"finish", Toast.LENGTH_LONG).show();


    }
}