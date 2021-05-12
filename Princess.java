package org.techtown.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
        //implements OnMapReadyCallback {

    //private GoogleMap mMap;
    public static ArrayList<itemList> infoList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);


       /* SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/
        getJSON getJSON = new getJSON();

        getJSON.execute();

        /*for(int i =0;i<1000;i++) {
            Log.d("tag", "result : " + infoList.toString());
            Log.d("tag", "result: " + infoList.toString());
        }*/
       /* for(int i =0;i<infoList.size();i++) {
            Log.d("tag", "result:" + infoList.get(i).address);
        }*/


    }

    public void button(View view) {

        String address;
        String centerName;
        String lat;
        String lng;
        /* for(int i =0;i<infoList.size();i++) {
            Log.d("tag", "result:" + infoList.get(i).address);
        }*/
         ListView listview = (ListView)findViewById(R.id.list); //ListView id 받아옴
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>(); //ArrayList 생성 (이름과, 주소)
        ArrayList<HashMap<String,String>> list1 = new ArrayList<HashMap<String,String>>(); //ArrayList 생성 (위도, 경도)
         for(int i = 0;i<infoList.size();i++){
             HashMap<String,String> item = new HashMap<String, String>();
             HashMap<String,String> item1 = new HashMap<String, String>();
             address = infoList.get(i).address;
             centerName = infoList.get(i).centerName + "                                                                      " + infoList.get(i).facilityName;
             lat = infoList.get(i).lat + " " + infoList.get(i).lng;
             lng = infoList.get(i).lng;
             //Log.d("tag","result: "+ address + centerName);
             item.put("item1", centerName);
             item.put("item2", address);
             item1.put("item1", lat);
             item1.put("item2", lng);
             list.add(item);
             list1.add(item1);
         }
       /*Collections.sort(list, new Comparator<HashMap< String,String >>() {
            @Override
            public int compare(HashMap<String, String> lhs,
                               HashMap<String, String> rhs) {
                return lhs.get("item2").compareTo(rhs.get("item2"));
            }
        }); */
         SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,new String[]{"item1","item2"}, new int[] {android.R.id.text1, android.R.id.text2});
         listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast myToast = Toast.makeText(MainActivity.this,list1.get(position).get("item1"),Toast.LENGTH_SHORT);
                myToast.show();

            }
        });
    }

   /* @Override

    public void onMapReady(final GoogleMap googleMap) {
        final GoogleMap googleMap = null;
        mMap = googleMap;

        LatLng SEOUL = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        mMap.addMarker(markerOptions);


        // 기존에 사용하던 다음 2줄은 문제가 있습니다.

        // CameraUpdateFactory.zoomTo가 오동작하네요.
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 10));
    }*/
}

