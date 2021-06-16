package com.example.ch;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;

public class Link {
    DatabaseReference Refer;

    public Link(DatabaseReference refer) {
        Refer = refer;
    }

    public void setRefer(DatabaseReference data){
        this.Refer=data;
    }
    public DatabaseReference getRefer(){
        return this.Refer;
    }

}
