package com.example.ch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
private Button mRegBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        init();
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(regIntent);
                finish();
            }
        });
    }
    private void init(){
        mRegBtn = findViewById(R.id.startRegBtn);
    }

    public void onClickLogInTransport(View view){
        Intent LogInTr = new Intent(StartActivity.this, LogInActivity.class);
        startActivity(LogInTr);
        finish();
    }
}