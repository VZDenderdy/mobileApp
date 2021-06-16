package com.example.ch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class trash extends AppCompatActivity {
    private EditText mDisplayName, mEmail , mPassword;
    private Button mCreateBtn;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);
        init();
    }
    private void init(){
        mDisplayName = (EditText) findViewById(R.id.reg_display_name);
        mEmail = (EditText)findViewById(R.id.reg_email2);
        mPassword = (EditText)findViewById(R.id.reg_password2);
        mCreateBtn = (Button) findViewById(R.id.reg_create_btn2);
        mAuth = FirebaseAuth.getInstance();
    }
    public void onClickSave(View view){
        String display_name = mDisplayName.getText().toString();
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        register_user(email,password);
    }
    private void register_user(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Intent mainIntent = new Intent(trash.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }else{
                    Toast.makeText(trash.this, "You got some error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}