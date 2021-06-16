package com.example.ch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
private FirebaseAuth mAuth;
private DatabaseReference mUserDatabase;

private ViewPager mViewPager;

private SectionPagerAdapter mSectionPagerAdapter;

private TabLayout mTabLayout;

private RecyclerView friend_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init(){
        mAuth=FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("TFTL");

        mViewPager = findViewById(R.id.main_tabPager);
        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionPagerAdapter);

        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user==null){
            sendToStart();
        }//else{
        //    mUserDatabase.child(mAuth.getCurrentUser().getUid()).child("online").setValue(true);
        //}
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mUserDatabase.child(mAuth.getCurrentUser().getUid()).child("online").setValue(false);
    }

    private void sendToStart() {
        Intent startAct = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startAct);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);

         if(item.getItemId()==R.id.main_logout_btn){
            FirebaseAuth.getInstance().signOut();
            sendToStart();
            finish();
         }
         if(item.getItemId()==R.id.main_settings_btn){
             Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
             startActivity(settingsIntent);

         }
         if(item.getItemId()==R.id.main_all_users_btn){
             Intent UsersIntent = new Intent(MainActivity.this, UsersActivity.class);
             startActivity(UsersIntent);
         }


        return true;

    }


}