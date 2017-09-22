package com.usu.mobileservice.annotationchecker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int sum = setIt(1, 2, 3, 5, 7);
    }

    private int setIt(int... hellos) {
        int sum = 0;
        for (int i = 0; i < hellos.length; i++) {
            sum += hellos[i];
        }
        return sum;
    }

    // @StatusInfo
    public class User {
        String firstName;
        String lastName;
        String city;
    }}
