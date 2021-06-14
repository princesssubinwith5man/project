package org.techtown.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.lang.Math;
import java.util.Map;

import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.content.Intent;
import android.widget.Toast;


public class GMapActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback
        {

    private double lat;
    private double lng;
    private String cn;
    private String fn;

    private boolean permissionDenied = false;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gmap_activity);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat", 0);
        lng = intent.getDoubleExtra("lng", 0);
        cn = intent.getStringExtra("centername");
        fn = intent.getStringExtra("fac");

        ActivityCompat.requestPermissions(this, MainActivity2.REQUIRED_PERMISSIONS,
                MainActivity2.PERMISSIONS_REQUEST_CODE);

    }
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        map = googleMap;


        Log.d("tag:", "result: " + lat +" "+ lng);
        /*LatLng Corona = new LatLng(lat,lng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(Corona);
        markerOptions.title(cn);
        markerOptions.snippet(fn);
        googleMap.addMarker(markerOptions);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Corona, 14));*/
        //---------------------------------------------------------------------------------
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //기본위치(63빌딩)
        LatLng position = new LatLng(lat , lng);

        //화면중앙의 위치와 카메라 줌비율
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14));

        onAddMarker();

        enableMyLocation();
    }
    public void onAddMarker(){
        LatLng position = new LatLng(lat , lng);
        this.map = map;
                //나의위치 마커
        MarkerOptions mymarker = new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(200f))  //마커색상지정
                .title(cn)
                .snippet(fn)
                .position(position);   //마커위치

                //마커추가
        map.addMarker(mymarker);

                //정보창 클릭 리스너
        map.setOnInfoWindowClickListener(infoWindowClickListener);

                //마커 클릭 리스너
        map.setOnMarkerClickListener(markerClickListener);
    }
            //정보창 클릭 리스너
            GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    String markerId = marker.getId();
                    Toast.makeText(GMapActivity.this, "정보창 클릭 Marker ID : "+markerId, Toast.LENGTH_SHORT).show();
                }
            };

            //마커 클릭 리스너
            GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    String markerId = marker.getId();
                    //선택한 타겟위치
                    LatLng location = marker.getPosition();
                    Toast.makeText(GMapActivity.this, "마커 클릭 Marker ID : "+markerId+"("+location.latitude+" "+location.longitude+")", Toast.LENGTH_SHORT).show();

                    return false;
                }
            };
    // ---------------------- 여기 밑으로는 gps 관련 메서드
    private void enableMyLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
                map.setOnMyLocationButtonClickListener(this);
                map.setOnMyLocationClickListener(this);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            ActivityCompat.requestPermissions(this, MainActivity2.REQUIRED_PERMISSIONS,
                    MainActivity2.PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "위도: " + location.getLatitude() + "경도: " + location.getLongitude(), Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
                .show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int LOCATION_PERMISSION_REQUEST_CODE = 1;
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();


        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.

            permissionDenied = false;
        }
    }


}