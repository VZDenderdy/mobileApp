package com.example.ch;

import com.google.firebase.database.DatabaseReference;

public class createFile {

    int type; // 0 - text, 1 - image, 2 - document
    String text = new String();
    String uri;
    DatabaseReference databaseReference;

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void setDatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    Boolean delete;

    public createFile(int type, String text, String uri, Boolean delete) {
        this.type = type;
        this.text = text;
        this.uri = uri;
        this.delete=delete;
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public createFile() {

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }


}
