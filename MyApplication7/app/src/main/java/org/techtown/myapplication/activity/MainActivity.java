package org.techtown.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;

import org.techtown.myapplication.method.GetJSON;
import org.techtown.myapplication.method.GetNavi;
import org.techtown.myapplication.object.ItemList;
import org.techtown.myapplication.R;
import org.techtown.myapplication.map.NearMapActivity;
import org.techtown.myapplication.map.NearMapActivity2;

import static org.techtown.myapplication.R.layout.activity_main;


public class MainActivity extends AppCompatActivity {
    public static ArrayList<ItemList> infoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        ImageView name = findViewById(R.id.imageView);
        name.setVisibility(View.INVISIBLE);
        Button button2 = findViewById(R.id.button2);
        button2.setVisibility(View.INVISIBLE);
        Button button3 = findViewById(R.id.button3);
        button3.setVisibility(View.INVISIBLE);
        Button button4 = findViewById(R.id.button4);
        button4.setVisibility(View.INVISIBLE);
        ImageView logo = (ImageView) findViewById(R.id.gif_image);
        logo.setVisibility(View.VISIBLE);
        if(infoList.size()==0) {
            GetJSON getJSON = new GetJSON();
            getJSON.execute();
        }
        GetNavi getNavi = new GetNavi();
        getNavi.execute();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                logo.setVisibility(View.INVISIBLE);
                name.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);
                button3.setVisibility(View.VISIBLE);
                button4.setVisibility(View.VISIBLE);
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

    public void click3(View view) {
        Intent intent = new Intent(MainActivity.this, NearMapActivity2.class);
        startActivity(intent);
    }
}
