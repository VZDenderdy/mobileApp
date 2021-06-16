package com.example.ch;

import java.util.ArrayList;

public final class Singleton {

    private static final Singleton INSTANCE = new Singleton();
    public ArrayList<String> value;

    private Singleton() {}

    public static Singleton getInstance() {
        return INSTANCE;
    }

}
