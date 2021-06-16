package com.example.ch;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class CreateFileAdapter extends RecyclerView.Adapter<CreateFileAdapter.MyViewHolder>{

    private ArrayList<createFile> list;

    public CreateFileAdapter(ArrayList<createFile> list){
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.container_file_single_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        createFile file = list.get(position);

        if(file.getType()==0){
            holder.initText();
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView fileName, ok;
        EditText Text;
        ImageView fileIcon, image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            fileIcon = mView.findViewById(R.id.file_create_icon);
            image = mView.findViewById(R.id.imageView);
            fileName = mView.findViewById(R.id.file_name);
            Text = mView.findViewById(R.id.enterText);
            ok = mView.findViewById(R.id.textView3);

        }

        public void initText(){
            fileIcon.setVisibility(View.GONE);
            image.setVisibility(View.GONE);
            fileName.setVisibility(View.GONE);
            Text.setVisibility(View.VISIBLE);
            ok.setVisibility(View.VISIBLE);
        }




    }
}
