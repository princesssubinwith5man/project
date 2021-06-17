package org.techtown.myapplication.trash;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.techtown.myapplication.R;
import org.techtown.myapplication.activity.IntroActivity;
import org.techtown.myapplication.map.GMapActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Search extends AppCompatActivity {
    private SearchView mSearchView;
    private Double lat1;
    private Double lng1;
    private String Centername;
    private String fn;
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        mSearchView = findViewById(R.id.searchView); // SearchView
        tv = findViewById(R.id.textView);
        tv.setVisibility(View.INVISIBLE);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("코로나 예방접종센터 조회");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // 입력받은 문자열 처리
                Log.d("tag","result"+s);
                printListview(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // 입력란의 문자열이 바뀔 때 처리
                //Log.d("tag","result"+s);
                printListview(s);
                return false;
            }
        });
    }
    public void printListview(String s) {
        String address;
        String centerName;
        double lat;
        double lng;
        String m_facn;
        String m_centerName;
        tv.setVisibility(View.INVISIBLE);
        //int inputSize = sido_show.length;

        //Log.d("tag", "print실행중..." + inputSize);

        ListView listview = (ListView) findViewById(R.id.list); //ListView id 받아옴
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(); //ArrayList 생성 (이름과, 주소)
        ArrayList<HashMap<String, Double>> list1 = new ArrayList<HashMap<String, Double>>(); //ArrayList 생성 (위도, 경도)
        ArrayList<HashMap<String, String>> list2 = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < IntroActivity.infoList.size(); i++) {
            if ((!IntroActivity.infoList.get(i).address.contains(s) && !IntroActivity.infoList.get(i).centerName.contains(s)&& !IntroActivity.infoList.get(i).facilityName.contains(s))|| s.equals("")) {
                //tv.setVisibility(View.VISIBLE);
                continue;
            }
            else {
                //tv.setVisibility(View.INVISIBLE);
                HashMap<String, String> item = new HashMap<String, String>();
                HashMap<String, Double> item1 = new HashMap<String, Double>();
                HashMap<String, String> item2 = new HashMap<String, String>();
                address = IntroActivity.infoList.get(i).address;
                centerName = IntroActivity.infoList.get(i).centerName + "                                                                      " + IntroActivity.infoList.get(i).facilityName;
                lat = IntroActivity.infoList.get(i).lat;
                lng = IntroActivity.infoList.get(i).lng;
                m_facn = IntroActivity.infoList.get(i).facilityName;
                m_centerName = IntroActivity.infoList.get(i).centerName;

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
        if(adapter.getCount() == 0) //리스트뷰에 아무것도 없다면
            tv.setVisibility(View.VISIBLE); //검색결과가 없습니다 출력
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast myToast = Toast.makeText(Search.this, "위도: " + list1.get(position).get("item1") + " 경도: " + list1.get(position).get("item2"), Toast.LENGTH_SHORT);
                myToast.show();
                lat1 = list1.get(position).get("item1");
                lng1 = list1.get(position).get("item2");
                Centername = list2.get(position).get("cn");
                fn = list2.get(position).get("fn");
                Intent intent = new Intent(Search.this, GMapActivity.class);
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