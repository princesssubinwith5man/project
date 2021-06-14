package org.techtown.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.techtown.myapplication.R.layout.activity_main2;


public class MainActivity2 extends AppCompatActivity {
    private Double lat1;
    private Double lng1;
    private String Centername;
    private String fn;
    private int check = 0;
    static MenuItem mSearch;
    SearchView searchView;
    // MenuItem mSearch;

    public static ArrayList<ItemList> infoList = new ArrayList<>();

    public static String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    public static final int PERMISSIONS_REQUEST_CODE = 100;


    Spinner spinnerDo;
    Spinner spinnerSi;
    String siDo;
    ProgressBar pb;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);

        mSearch = menu.findItem(R.id.action_search);
        //mSearch.expandActionView();
        SearchView sv = (SearchView) mSearch.getActionView();
        //sv.setMaxWidth(Integer.MAX_VALUE);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchListview(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.d("tag","result" + newText);
                SearchListview(newText);
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
            }, 1500); //딜레이 타임 조절*/
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
        String address;
        String centerName;
        double lat;
        double lng;
        String m_facn;
        String m_centerName;

        int inputSize = sido_show.length;

        Log.d("tag", "print실행중..." + inputSize);

        ListView listview = (ListView) findViewById(R.id.list); //ListView id 받아옴
        if (check == 1)
            listview.setVisibility(View.VISIBLE);
        else
            listview.setVisibility(View.INVISIBLE);

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(); //ArrayList 생성 (이름과, 주소)
        ArrayList<HashMap<String, Double>> list1 = new ArrayList<HashMap<String, Double>>(); //ArrayList 생성 (위도, 경도)
        ArrayList<HashMap<String, String>> list2 = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < infoList.size(); i++) {
            if (!infoList.get(i).sido.equals(sido_show[0]))
                continue;
            else if(sido_show.length==2 && !infoList.get(i).sigungu.equals(sido_show[1]))
                continue;

            //else {
            HashMap<String, String> item = new HashMap<String, String>();
            HashMap<String, Double> item1 = new HashMap<String, Double>();
            HashMap<String, String> item2 = new HashMap<String, String>();
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
            // }
        }

        SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2, new String[]{"item1", "item2"}, new int[]{android.R.id.text1, android.R.id.text2});
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast myToast = Toast.makeText(MainActivity2.this, "위도: " + list1.get(position).get("item1") + " 경도: " + list1.get(position).get("item2"), Toast.LENGTH_SHORT);
                myToast.show();
                lat1 = list1.get(position).get("item1");
                lng1 = list1.get(position).get("item2");
                Centername = list2.get(position).get("cn");
                fn = list2.get(position).get("fn");
                Intent intent = new Intent(MainActivity2.this, GMapActivity.class);
                intent.putExtra("lat", lat1);
                intent.putExtra("lng", lng1);
                intent.putExtra("centername", Centername);
                intent.putExtra("fac", fn);
                startActivity(intent);
            }
        });
    }
    public void SearchListview(String s) {
        String address;
        String centerName;
        double lat;
        double lng;
        String m_facn;
        String m_centerName;
        // tv.setVisibility(View.INVISIBLE);
        //int inputSize = sido_show.length;

        //Log.d("tag", "print실행중..." + inputSize);

        ListView listview = (ListView) findViewById(R.id.list); //ListView id 받아옴
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(); //ArrayList 생성 (이름과, 주소)
        ArrayList<HashMap<String, Double>> list1 = new ArrayList<HashMap<String, Double>>(); //ArrayList 생성 (위도, 경도)
        ArrayList<HashMap<String, String>> list2 = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < MainActivity.infoList.size(); i++) {
            if ((!MainActivity.infoList.get(i).address.contains(s) && !MainActivity.infoList.get(i).centerName.contains(s)&& !MainActivity.infoList.get(i).facilityName.contains(s))|| s.equals("")) {
                //tv.setVisibility(View.VISIBLE);
                continue;
            }
            else {
                //tv.setVisibility(View.INVISIBLE);
                HashMap<String, String> item = new HashMap<String, String>();
                HashMap<String, Double> item1 = new HashMap<String, Double>();
                HashMap<String, String> item2 = new HashMap<String, String>();
                address = MainActivity.infoList.get(i).address;
                centerName = MainActivity.infoList.get(i).centerName + "                                                                      " + MainActivity.infoList.get(i).facilityName;
                lat = MainActivity.infoList.get(i).lat;
                lng = MainActivity.infoList.get(i).lng;
                m_facn = MainActivity.infoList.get(i).facilityName;
                m_centerName = MainActivity.infoList.get(i).centerName;

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
        }

        SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2, new String[]{"item1", "item2"}, new int[]{android.R.id.text1, android.R.id.text2});
        listview.setAdapter(adapter);
        //if(adapter.getCount() == 0) //리스트뷰에 아무것도 없다면
        // tv.setVisibility(View.VISIBLE); //검색결과가 없습니다 출력
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast myToast = Toast.makeText(MainActivity2.this, "위도: " + list1.get(position).get("item1") + " 경도: " + list1.get(position).get("item2"), Toast.LENGTH_SHORT);
                myToast.show();
                lat1 = list1.get(position).get("item1");
                lng1 = list1.get(position).get("item2");
                Centername = list2.get(position).get("cn");
                fn = list2.get(position).get("fn");
                Intent intent = new Intent(MainActivity2.this, GMapActivity.class);
                intent.putExtra("lat", lat1);
                intent.putExtra("lng", lng1);
                intent.putExtra("centername", Centername);
                intent.putExtra("fac", fn);
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
