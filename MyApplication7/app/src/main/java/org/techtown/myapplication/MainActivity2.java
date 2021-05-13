package org.techtown.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.content.Intent;


public class MainActivity2 extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double lat;
    private double lng;
    private String lat1;
    private String lng1;
    private String cn;
    private String fn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        lat1 = intent.getStringExtra("lat");
        lng1 = intent.getStringExtra("lng");
        cn = intent.getStringExtra("centername");
        fn = intent.getStringExtra("fac");
        //Log.d("tag:", "result: " + lat1 +" "+ lng1);
        lat =Double.parseDouble(lat1);
        lng =Double.parseDouble(lng1);

    }
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("tag:", "result: " + lat1 +" "+ lng1);
        LatLng Corona = new LatLng(lat,lng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(Corona);
        markerOptions.title(cn);
        markerOptions.snippet(fn);
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Corona, 14));
    }
}