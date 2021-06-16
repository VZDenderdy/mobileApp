package com.example.ch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditActivity extends AppCompatActivity {

    private TextInputLayout mStatus;
    private Button saveEditbtn;

    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
    }

    private void init(){
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = currentUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.edit_appBar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Edit Account");
        actionBar.setDisplayHomeAsUpEnabled(true);

        mStatus = findViewById(R.id.status_input);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentStatus = snapshot.child("status").getValue().toString();
                mStatus.getEditText().setText(currentStatus);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        saveEditbtn = findViewById(R.id.save_edit_btn);

        saveEditbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = mStatus.getEditText().getText().toString();

                mDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Intent returnToSettings = new Intent(EditActivity.this,
                                    SettingsActivity.class);
                            startActivity(returnToSettings);
                            finish();

                        }
                    }
                });
            }
        });

    }

}