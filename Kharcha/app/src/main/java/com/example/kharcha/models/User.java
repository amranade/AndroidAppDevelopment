package com.example.kharcha.models;

import java.sql.Timestamp;

public class User {
    public User(String name, Timestamp ts) {
        this.name = name;
        this.time = ts.toString();
    }

    public String name;
    public String time;
}
