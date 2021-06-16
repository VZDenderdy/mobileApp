package com.example.ch;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.MyViewHolder> {

private ArrayList<Folder> list;
private Context context;
DatabaseReference databaseReference;

public FilesAdapter(ArrayList<Folder> list, Context context){
    this.list=list;
    this.context = context;
    databaseReference= FirebaseDatabase.getInstance().getReference();
}


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_single_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Folder folder = list.get(position);
        if(folder.getFile()){
            holder.setNameFile(folder.getName());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);

                    alert.setTitle("Скачать файл?");


                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            //DatabaseReference ref = databaseReference;
                            //for(int i=0;i<folder.getLink().size();i++){
                              //  ref = ref.child(folder.getLink().get(i));
                            //}

                            DatabaseReference ref = folder.getData();
                           // Toast.makeText(context, folder.getLink().get(folder.getLink().size()-1), Toast.LENGTH_SHORT).show();

                            DownloadManager downloadManager =(DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {



                                    String url = snapshot.child("File").child("Uri").getValue().toString();
                                    Uri uri = Uri.parse(url);
                                    DownloadManager.Request request = new DownloadManager.Request(uri);
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    request.setDestinationInExternalFilesDir(context, DIRECTORY_DOWNLOADS, folder.getName()+"."+snapshot.child("File").child("type").getValue().toString());
                                    downloadManager.enqueue(request);





                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


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
            });




            /////////////////////////////////////////////////////////
            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Удалить?");
                    alert.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ProgressDialog progressDialog = new ProgressDialog(context);
                            progressDialog.setTitle("Wait...");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();

                            DatabaseReference ref = folder.getData();

                            ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    StorageReference storage = FirebaseStorage.getInstance().getReference().child("Files");

                                    ArrayList<String> link = folder.getLink();

                                    for(int i=0; i<link.size();i++){
                                        storage = storage.child(link.get(i));
                                    }

                                    Log.d("ahahah", link.get(link.size()-1));

                                    storage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                        }
                                    });


                                }
                            });


                        }
                    });
                    alert.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = alert.create();
                    dialog.show();


                    return false;
                }
            });



        }else{
            holder.setNameLink(folder.getName());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent Intent = new Intent(v.getContext(), Files.class);
                    ArrayList<String> link = folder.getLink();
                    link.add(folder.getName());
                    //Intent.putExtra("ref", link);
                    Singleton.getInstance().value = link;

                    v.getContext().startActivity(Intent);
                }
            });
/*
            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder alertDialog =new AlertDialog.Builder(context);
                    alertDialog.setTitle("Удалить папку?");
                    alertDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            folder.getData().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    ArrayList<String> link = folder.getLink();

                                    StorageReference storage =FirebaseStorage.getInstance().getReference().child("Files");
                                    for(int i=0; i<link.size();i++){
                                        storage = storage.child(link.get(i));
                                    }

                                    storage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });



                                }
                            });

                        }
                    });
                    alertDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog dialog = alertDialog.create();
                    dialog.show();

                    return false;
                }
            });

 */

        }
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

        public void setNameLink(String name){
            TextView Name = mView.findViewById(R.id.folder_name);
            ImageView icon = mView.findViewById(R.id.file_icon);
            icon.setVisibility(View.GONE);
            Name.setText(name);
        }

        public void setNameFile(String name){
            TextView Name = mView.findViewById(R.id.folder_name);
            ImageView icon = mView.findViewById(R.id.file_icon);
            icon.setVisibility(View.VISIBLE);
            Name.setText(name);
        }



    }

}
