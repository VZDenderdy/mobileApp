package com.example.ch;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class Folder {

    Boolean file;
    String name;
    ArrayList<String> link;

    public DatabaseReference getData() {
        return data;
    }

    public void setData(DatabaseReference data) {
        this.data = data;
    }

    DatabaseReference data;
    public Folder(Boolean file, String name, ArrayList<String> link) {
        this.file = file;
        this.name = name;
        this.link = link;
    }

    public ArrayList<String> getLink() {
        return link;
    }

    public void setLink(ArrayList<String> link) {
        this.link = link;
    }

    public Boolean getFile() {
        return file;
    }

    public void setFile(Boolean file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
