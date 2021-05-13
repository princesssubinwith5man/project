package org.techtown.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.myapplication.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import static android.R.layout.simple_list_item_1;
import static org.techtown.myapplication.R.layout.activity_main;


public class MainActivity extends AppCompatActivity{
    private String lat1;
    private String lng1;
    private String Centername;
    private String fn;
    private int check = 0;
    public static ArrayList<itemList> infoList = new ArrayList<>();
    ProgressBar pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        //pb = (ProgressBar) findViewById(R.id.progressBar);
        ListView listview = (ListView)findViewById(R.id.list);
        ImageView logo = (ImageView) findViewById(R.id.gif_image);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(logo);
        Glide.with(this).load(R.drawable.logo2).into(gifImage);
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setVisibility(View.INVISIBLE);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                print((String) adapterView.getItemAtPosition(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (infoList.size() == 0) {
            //pb.setVisibility(View.VISIBLE);
            makeRequest();
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                logo.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.VISIBLE);
                check =1;
            }
        }, 8500); //딜레이 타임 조절




    }
    public void makeRequest() {
                processResponse();
    }

    @SuppressLint("SetTextI18n")
    public void processResponse(){
        getJSON getJSON = new getJSON();
        getJSON.execute();
    }


    public void print(String sido_show){
        String address;
        String centerName;
        String lat;
        String lng;
        String m_facn;
        String m_centerName;

        ListView listview = (ListView)findViewById(R.id.list); //ListView id 받아옴
        if(check == 1)
            listview.setVisibility(View.VISIBLE);
        else
            listview.setVisibility(View.INVISIBLE);
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>(); //ArrayList 생성 (이름과, 주소)
        ArrayList<HashMap<String,String>> list1 = new ArrayList<HashMap<String,String>>(); //ArrayList 생성 (위도, 경도)
        ArrayList<HashMap<String,String>> list2 = new ArrayList<HashMap<String,String>>();
        for(int i = 0;i<infoList.size();i++){
            if(!infoList.get(i).sido.equals(sido_show))
                continue;

            HashMap<String,String> item = new HashMap<String, String>();
            HashMap<String,String> item1 = new HashMap<String, String>();
            HashMap<String,String> item2 = new HashMap<String, String>();
            address = infoList.get(i).address;
            centerName = infoList.get(i).centerName + "                                                                      " + infoList.get(i).facilityName;
            lat = infoList.get(i).lat;
            lng = infoList.get(i).lng;
            m_facn = infoList.get(i).facilityName;
            m_centerName = infoList.get(i).centerName;

            item.put("item1", centerName);
            item.put("item2", address);
            item1.put("item1", lat);
            item1.put("item2", lng);
            item2.put("cn", m_centerName);
            item2.put("fn", m_facn);
            list.add(item);
            list1.add(item1);
            list2.add(item2);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,new String[]{"item1","item2"}, new int[] {android.R.id.text1, android.R.id.text2});
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast myToast = Toast.makeText(MainActivity.this,"위도: "+ list1.get(position).get("item1")+" 경도: "+ list1.get(position).get("item2"),Toast.LENGTH_SHORT);
                myToast.show();
                lat1 = list1.get(position).get("item1");
                lng1 = list1.get(position).get("item2");
                Centername = list2.get(position).get("cn");
                fn = list2.get(position).get("fn");
                Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                intent.putExtra("lat",lat1);
                intent.putExtra("lng",lng1);
                intent.putExtra("centername", Centername);
                intent.putExtra("fac",fn);
                startActivity(intent);
            }
        });
        //pb.setVisibility(View.INVISIBLE);
    }

    public void button(View view) {    }
}
