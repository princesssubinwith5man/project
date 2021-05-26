package org.techtown.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.techtown.myapplication.R.layout.activity_main;
import static org.techtown.myapplication.R.layout.activity_main2;


public class MainActivity extends AppCompatActivity {
    public static ArrayList<ItemList> infoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        ImageView name = findViewById(R.id.imageView);
        name.setVisibility(View.INVISIBLE);
        ImageView logo = (ImageView) findViewById(R.id.gif_image);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(logo);
        Glide.with(this).load(R.drawable.logo3).into(gifImage);
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                logo.setVisibility(View.INVISIBLE);
                name.setVisibility(View.VISIBLE);
            }
        }, 2000); //딜레이 타임 조절*/
    }

    public void click(View view) {
        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        startActivity(intent);
    }

    public void click1(View view) {
        Intent intent = new Intent(MainActivity.this, NearMapActivity.class);
        startActivity(intent);
    }

    public void click2(View view) {
        Intent intent = new Intent(MainActivity.this, Search.class);
        startActivity(intent);
    }
}
