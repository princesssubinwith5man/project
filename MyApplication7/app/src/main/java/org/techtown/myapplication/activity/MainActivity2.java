package org.techtown.myapplication.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import org.techtown.myapplication.method.GetJSON;
import org.techtown.myapplication.object.ItemList;
import org.techtown.myapplication.object.ListViewAdapter;
import org.techtown.myapplication.object.ListViewItem;
import org.techtown.myapplication.R;
import org.techtown.myapplication.map.GMapActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.techtown.myapplication.R.layout.activity_main2;


public class MainActivity2 extends AppCompatActivity {

    private int check = 0;
    static MenuItem mSearch;
    public static ArrayList<ItemList> infoList = new ArrayList<>();

    public static String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    public static final int PERMISSIONS_REQUEST_CODE = 100;

    ListView listView;

    Spinner spinnerDo;
    Spinner spinnerSi;
    String siDo;
    ProgressBar pb;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //액션바 불러오기
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);

        mSearch = menu.findItem(R.id.action_search);
        SearchView sv = (SearchView) mSearch.getActionView(); //서치뷰 변수
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchListview(query); //검색시 SearchListview 출력
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.d("tag","result" + newText);
                SearchListview(newText);// 글자마다 Search ListView 출력
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main2);
        pb = (ProgressBar) findViewById(R.id.progressBar);

        listView = (ListView) findViewById(R.id.list);
        spinnerDo = (Spinner) findViewById(R.id.spinner);
        spinnerSi = (Spinner) findViewById(R.id.spinner_si);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        //SearchView sv  = (SearchView) mSearch.getActionView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("코로나 예방접종센터 조회");

        //ImageView logo = (ImageView) findViewById(R.id.gif_image);
        //GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(logo);
        //Glide.with(this).load(R.drawable.logo3).into(gifImage);
        if(check == 0) {
            spinnerDo.setVisibility(View.INVISIBLE);
            spinnerSi.setVisibility(View.INVISIBLE);
            pb.setVisibility(View.VISIBLE);
            mToolbar.setVisibility(View.INVISIBLE);
        }
        spinnerDo.setSelection(0);
        spinnerSi.setSelection(0);
        spinnerDo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView)adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                siDo = (String) adapterView.getItemAtPosition(i);
                Log.d("tab", siDo + "선택됨");
                sigunguSpinnerChanger(siDo);
                printListview(siDo);


                spinnerSi.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {    }
        });

        spinnerSi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    int temp =1;
                }
                else {
                    String selectedSigungu = (String) adapterView.getItemAtPosition(i);
                    printListview(siDo, selectedSigungu);
                    ((TextView)adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (infoList.size() == 0 ) {
            //pb.setVisibility(View.VISIBLE);
            makeRequest();
        }

        if(check ==0) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pb.setVisibility(View.INVISIBLE);
                    spinnerDo.setVisibility(View.VISIBLE);
                    spinnerSi.setVisibility(View.VISIBLE);
                    mToolbar.setVisibility(View.VISIBLE);
                    check = 1;
                }
            }, 1300); //딜레이 타임 조절*/
        }
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS ,
                PERMISSIONS_REQUEST_CODE);

    }

    public void sigunguSpinnerChanger(String seletedSi) {
        LinkedHashMap<String, Integer> hashMap = new LinkedHashMap<>();
        Log.d("tag", "siHandler 호출됨..");

        hashMap.put("시/군/구를 선택하세요", 1);

        for (int i = 0; i < infoList.size(); i++) {
            if (infoList.get(i).sido.equals(seletedSi) && !hashMap.containsKey(infoList.get(i).sigungu)) {
                hashMap.put(infoList.get(i).sigungu, 1);
            }
        }
        Log.d("tag", "hashmap 갯수는 : " + hashMap.keySet().size());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, new ArrayList<>(hashMap.keySet()));
        spinnerSi.setAdapter(adapter);

    }

    public void makeRequest() {
        processResponse();
    }

    @SuppressLint("SetTextI18n")
    public void processResponse() {
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }


    public void printListview(String... sido_show) {
        String address, facName;
        String centerName;
        double lat;
        double lng;

        int inputSize = sido_show.length;

        Log.d("tag", "print실행중..." + inputSize);


        if (check == 1)
            listView.setVisibility(View.VISIBLE);
        else
            listView.setVisibility(View.INVISIBLE);


        ListViewAdapter adapter = new ListViewAdapter();

        for (int i = 0; i < infoList.size(); i++) {
            if (!infoList.get(i).sido.equals(sido_show[0]))
                continue;
            else if(sido_show.length==2 && !infoList.get(i).sigungu.equals(sido_show[1]))
                continue;

            address = infoList.get(i).address;
            centerName = infoList.get(i).centerName;
            facName = infoList.get(i).facilityName;
            lat = infoList.get(i).lat;
            lng = infoList.get(i).lng;

            adapter.addItem(0, centerName, facName, address, lat, lng);
        }
        listView.setAdapter(adapter);

        // 리스트뷰 온클릭 리스너
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListViewItem listViewItem = adapter.listViewItemList.get(position);
                double lat = listViewItem.getLat();
                double lng = listViewItem.getLng();
                String centerName = listViewItem.getCenterNameStr();
                String facName = listViewItem.getFacNameStr();
                String address = listViewItem.getAddressStr();

                Toast.makeText(getApplicationContext(), "위도 : " + lat, Toast.LENGTH_LONG);

                Intent intent = new Intent(MainActivity2.this, GMapActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("centername", centerName);
                intent.putExtra("fac", facName);
                intent.putExtra("add", address);
                startActivity(intent);
            }
        });
    }
    public void SearchListview(String s) {
        String address, facName;
        String centerName;
        double lat;
        double lng;
        ListViewAdapter adapter = new ListViewAdapter();

        Log.d("tag", "Search실행중..." + MainActivity.infoList.size());


        if (check == 1)
            listView.setVisibility(View.VISIBLE);
        else
            listView.setVisibility(View.INVISIBLE);


        for (int i = 0; i < MainActivity.infoList.size(); i++) {
            if ((!MainActivity.infoList.get(i).address.contains(s) && !MainActivity.infoList.get(i).centerName.contains(s)&& !MainActivity.infoList.get(i).facilityName.contains(s))|| s.equals("")) {
                //tv.setVisibility(View.VISIBLE);
                continue;
            }
            else {

                address = infoList.get(i).address;
                centerName = infoList.get(i).centerName;
                facName = infoList.get(i).facilityName;
                lat = infoList.get(i).lat;
                lng = infoList.get(i).lng;

                adapter.addItem(0, centerName, facName, address, lat, lng);
            }
        }
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListViewItem listViewItem = adapter.listViewItemList.get(position);
                double lat = listViewItem.getLat();
                double lng = listViewItem.getLng();
                String centerName = listViewItem.getCenterNameStr();
                String facName = listViewItem.getFacNameStr();
                String address = listViewItem.getAddressStr();

                Toast.makeText(getApplicationContext(), "위도 : " + lat, Toast.LENGTH_LONG);

                Intent intent = new Intent(MainActivity2.this, GMapActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("centername", centerName);
                intent.putExtra("fac", facName);
                intent.putExtra("add", address);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}