package org.techtown.myapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;

import org.techtown.myapplication.R;
import org.techtown.myapplication.map.GMapActivity;
import org.techtown.myapplication.method.GetJSON;
import org.techtown.myapplication.object.ItemList;
import org.techtown.myapplication.object.ListViewAdapter;
import org.techtown.myapplication.object.ListViewItem;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.techtown.myapplication.R.layout.activity_list;
import static org.techtown.myapplication.activity.MainActivity.REQUIRED_PERMISSIONS;


public class ListActivity extends AppCompatActivity {

    private int check = 0;
    static MenuItem mSearch;
    public static ArrayList<ItemList> infoList = new ArrayList<>();



    ListView listView;
    ImageView bgapp;
    Animation frombottom;
    Spinner spinnerDo;
    Spinner spinnerSi;
    String siDo;
    ProgressBar pb;
    LinearLayout LL;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //????????? ????????????
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        mSearch = menu.findItem(R.id.action_search);
        SearchView sv = (SearchView) mSearch.getActionView(); //????????? ??????
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchListview(query); //????????? SearchListview ??????
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.d("tag","result" + newText);
                SearchListview(newText);// ???????????? Search ListView ??????
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }//????????? ??????

        setContentView(activity_list);
        bgapp = (ImageView) findViewById(R.id.temp);
        frombottom= AnimationUtils.loadAnimation(this,R.anim.frombottom);

        bgapp.animate().translationY(-1100).setDuration(1300).setStartDelay(800);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        ImageView iv =(ImageView)findViewById(R.id.temp);
        listView = (ListView) findViewById(R.id.list);
        spinnerDo = (Spinner) findViewById(R.id.spinner);
        spinnerSi = (Spinner) findViewById(R.id.spinner_si);
        TextView TV= (TextView) findViewById(R.id.textview1);
        TextView TV1= (TextView) findViewById(R.id.textview2);
        LL = findViewById(R.id.LL);

        LL.startAnimation(frombottom);

        TV.startAnimation(frombottom);
        TV1.startAnimation(frombottom);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        //SearchView sv  = (SearchView) mSearch.getActionView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("????????? ?????????????????? ??????");

        //ImageView logo = (ImageView) findViewById(R.id.gif_image);
        //GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(logo);
        //Glide.with(this).load(R.drawable.logo3).into(gifImage);
        if (check == 0) {
            spinnerDo.setVisibility(View.INVISIBLE);
            spinnerSi.setVisibility(View.INVISIBLE);
            pb.setVisibility(View.VISIBLE);
            mToolbar.setVisibility(View.INVISIBLE);
            iv.setVisibility(View.INVISIBLE);
            TV.setVisibility(View.INVISIBLE);
            TV1.setVisibility(View.INVISIBLE);
        }
        //spinnerDo.dropDownVerticalOffset = dipToPixels(45f).toInt();
        spinnerDo.setSelection(0);
        spinnerSi.setSelection(0);

/*        try{
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            ListPopupWindow window = (ListPopupWindow)popup.get(spinnerDo);
            window.setHeight(300);
        }catch (Exception e){
            e.printStackTrace();
        }*/

        spinnerDo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // ?????? ???????????? ????????? ?????? ????????? ???????????? ????????? ????????? ?????????
                //((TextView)adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                siDo = (String) adapterView.getItemAtPosition(i);
                Log.d("tab", siDo + "?????????");
                sigunguSpinnerChanger(siDo);
                printListview(siDo);


                spinnerSi.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerSi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    int temp = 1;
                } else {
                    String selectedSigungu = (String) adapterView.getItemAtPosition(i);
                    printListview(siDo, selectedSigungu);
                    //((TextView)adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (infoList.size() == 0) {
            //pb.setVisibility(View.VISIBLE);
            makeRequest();
        }

        if (check == 0) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pb.setVisibility(View.INVISIBLE);
                    spinnerDo.setVisibility(View.VISIBLE);
                    spinnerSi.setVisibility(View.VISIBLE);
                    mToolbar.setVisibility(View.VISIBLE);
                    iv.setVisibility(View.VISIBLE);
                    TV.setVisibility(View.VISIBLE);
                    TV1.setVisibility(View.VISIBLE);
                    check = 1;
                }
            }, 1300); //????????? ?????? ??????*/
        }

        // ?????? ????????? ????????? ????????????

    }

    public void sigunguSpinnerChanger(String seletedSi) {
        LinkedHashMap<String, Integer> hashMap = new LinkedHashMap<>();
        Log.d("tag", "siHandler ?????????..");

        hashMap.put("?????? ??????", 1);

        for (int i = 0; i < infoList.size(); i++) {
            if (infoList.get(i).sido.equals(seletedSi) && !hashMap.containsKey(infoList.get(i).sigungu)) {
                hashMap.put(infoList.get(i).sigungu, 1);
            }
        }
        Log.d("tag", "hashmap ????????? : " + hashMap.keySet().size());
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

        Log.d("tag", "print?????????..." + inputSize);

        if (check == 1) {
            listView.setVisibility(View.VISIBLE);
        }
        else
            listView.setVisibility(View.INVISIBLE);


        ListViewAdapter adapter = new ListViewAdapter();

        for (int i = 0; i < infoList.size(); i++) {
            if (!infoList.get(i).sido.equals(sido_show[0])) // ?????? ?????? ???????????? ????????? continue
                continue;
            else if (sido_show.length == 2 && !infoList.get(i).sigungu.equals(sido_show[1])) // s
                continue;

            address = infoList.get(i).address;
            centerName = infoList.get(i).centerName;
            facName = infoList.get(i).facilityName;
            lat = infoList.get(i).lat;
            lng = infoList.get(i).lng;

            adapter.addItem(0, centerName, facName, address, lat, lng);
        }
        listView.setAdapter(adapter);

        // ???????????? ????????? ?????????
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), REQUIRED_PERMISSIONS[0]) == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getApplicationContext(), "?????? ????????? ???????????? ?????? ????????? ????????? ??? ????????????", Toast.LENGTH_LONG);
                    return;
                }

                ListViewItem listViewItem = adapter.listViewItemList.get(position);
                double lat = listViewItem.getLat();
                double lng = listViewItem.getLng();
                String centerName = listViewItem.getCenterNameStr();
                String facName = listViewItem.getFacNameStr();
                String address = listViewItem.getAddressStr();

                Toast.makeText(getApplicationContext(), "?????? : " + lat, Toast.LENGTH_LONG);

                Intent intent = new Intent(ListActivity.this, GMapActivity.class);
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

        Log.d("tag", "Search?????????..." + MainActivity.infoList.size());


        if (check == 1)
            listView.setVisibility(View.VISIBLE);
        else
            listView.setVisibility(View.INVISIBLE);


        for (int i = 0; i < MainActivity.infoList.size(); i++) {
            if ((!MainActivity.infoList.get(i).address.contains(s) && !MainActivity.infoList.get(i).centerName.contains(s) && !MainActivity.infoList.get(i).facilityName.contains(s)) || s.equals("")) {
                //tv.setVisibility(View.VISIBLE);
                continue;
            } else {

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

                //Toast.makeText(getApplicationContext(), "?????? : " + lat, Toast.LENGTH_LONG);

                Intent intent = new Intent(ListActivity.this, GMapActivity.class);
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
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar??? back??? ????????? ??? ??????
                finish();
                return true;
            }
            case R.id.seungmin:{
                //Toast.makeText(this, "01040550561", Toast.LENGTH_SHORT).show();
                startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:01040550561")));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
