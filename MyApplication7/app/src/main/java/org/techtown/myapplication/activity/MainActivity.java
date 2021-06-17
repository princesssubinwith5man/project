package org.techtown.myapplication.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.techtown.myapplication.R;
import org.techtown.myapplication.map.NearMapActivity;
import org.techtown.myapplication.map.NearMapActivity2;
import org.techtown.myapplication.method.GetJSON;
import org.techtown.myapplication.method.GetNavi;
import org.techtown.myapplication.object.ItemList;

import java.util.ArrayList;

import static org.techtown.myapplication.R.layout.activity_main;


public class MainActivity extends AppCompatActivity {
    public static ArrayList<ItemList> infoList = new ArrayList<>();

    public static String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    public static final int PERMISSIONS_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }//상태바 투명

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

        // getNavi test해봄

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
