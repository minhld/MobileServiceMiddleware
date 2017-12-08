package com.usu.observer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ZoomActivity extends AppCompatActivity {
    private ImageView fullView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);

        fullView = (ImageView) findViewById(R.id.fullView);

        Intent intent = this.getIntent();
        String file = intent.getStringExtra("file");

        Picasso.with(this).load(file).fit().into(fullView);
    }
}
