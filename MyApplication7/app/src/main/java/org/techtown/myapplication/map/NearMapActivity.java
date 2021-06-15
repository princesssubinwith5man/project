package org.techtown.myapplication.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.lang.Math;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.techtown.myapplication.method.GetPhone;
import org.techtown.myapplication.object.ItemList;
import org.techtown.myapplication.activity.MainActivity2;
import org.techtown.myapplication.R;


public class NearMapActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private double lat;
    private double lng;
    private String cn;
    private String fn;
    private String ad;

    private boolean permissionDenied = false;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        //lat = intent.getDoubleExtra("lat", 0);
        //lng = intent.getDoubleExtra("lng", 0);
        //cn = intent.getStringExtra("centername");
        //fn = intent.getStringExtra("fac");

        ActivityCompat.requestPermissions(this, MainActivity2.REQUIRED_PERMISSIONS,
                MainActivity2.PERMISSIONS_REQUEST_CODE);

    }
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        map = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
                enableMyLocation();
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            ActivityCompat.requestPermissions(this, MainActivity2.REQUIRED_PERMISSIONS,
                    MainActivity2.PERMISSIONS_REQUEST_CODE);
        }




    }

    // ---------------------- 여기 밑으로는 gps 관련 메서드
    private void enableMyLocation() {
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                String provider = location.getProvider();
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                double altitude = location.getAltitude();
                Log.d("Tag","result"+ latitude+"\n"+longitude);
                Toast.makeText(this, "위도: " + location.getLatitude() + "경도: " + location.getLongitude(), Toast.LENGTH_LONG)
                        .show();

                ItemList itemList = MainActivity2.infoList.get(0);
                double minDistance = Double.MAX_VALUE;

                for(int i=0; i<MainActivity2.infoList.size(); i++){
                    double distance = getDistance(latitude, longitude,
                            MainActivity2.infoList.get(i).lat, MainActivity2.infoList.get(i).lng, "kilometer");

                    if(minDistance > distance){
                        itemList = MainActivity2.infoList.get(i);
                        Log.d("log:","result: "+ i +" "+ minDistance+ " " + itemList.centerName+ " 거리차이는: "+ distance);
                        minDistance = distance;
                    }

                }
                cn = itemList.centerName;
                fn = itemList.facilityName;
                ad = itemList.address;
                LatLng nearCenter = new LatLng(itemList.lat, itemList.lng);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(nearCenter);
                markerOptions.title(itemList.centerName);
                markerOptions.snippet(itemList.facilityName);
                map.addMarker(markerOptions);

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(nearCenter, 14));
                map.setOnInfoWindowClickListener(infoWindowClickListener);

                //마커 클릭 리스너
                map.setOnMarkerClickListener(markerClickListener);

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

        ItemList itemList = MainActivity2.infoList.get(0);
        double minDistance = Double.MAX_VALUE;

        for(int i=0; i<MainActivity2.infoList.size(); i++){
            double distance = getDistance(location.getLatitude(), location.getLongitude(),
                    MainActivity2.infoList.get(i).lat, MainActivity2.infoList.get(i).lng, "kilometer");

            if(minDistance > distance){
                itemList = MainActivity2.infoList.get(i);
                Log.d("log:","result: "+ i +" "+ minDistance+ " " + itemList.centerName+ "거리차이는 "+ distance);
                minDistance = distance;
            }

        }

        LatLng nearCenter = new LatLng(itemList.lat, itemList.lng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(nearCenter);
        markerOptions.title(itemList.centerName);
        markerOptions.snippet(itemList.facilityName);
        map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(nearCenter, 14));
        map.setOnInfoWindowClickListener(infoWindowClickListener);

        //마커 클릭 리스너
        map.setOnMarkerClickListener(markerClickListener);
    }
    //정보창 클릭 리스너
    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            String markerId = marker.getId();
            Toast.makeText(NearMapActivity.this, "정보창 클릭 Marker ID : "+markerId, Toast.LENGTH_SHORT).show();
        }
    };

    //마커 클릭 리스너
    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            String markerId = marker.getId();
            //선택한 타겟위치
            LatLng location = marker.getPosition();
            //Toast.makeText(NearMapActivity.this, "마커 클릭 Marker ID : "+markerId+"("+location.latitude+" "+location.longitude+")", Toast.LENGTH_SHORT).show();
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                    NearMapActivity.this, R.style.BottomSheetDialogTheme
            );
            View bottomSheetView = LayoutInflater.from(getApplicationContext())
                    .inflate(
                            R.layout.layout_bottom_sheet,
                            (LinearLayout)findViewById(R.id.bottomSheetConteainer)
                    );
            TextView tv = bottomSheetView.findViewById(R.id.center_name_text);
            TextView tv1 = bottomSheetView.findViewById(R.id.fa_name_text);
            TextView tv2 = bottomSheetView.findViewById(R.id.address_text);
            tv.setText(cn);
            tv1.setText(fn);
            tv2.setText(ad);
            bottomSheetView.findViewById(R.id.buttonShare).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){ // 전화 버튼 눌렀을때 전화 걸기
                    Toast.makeText(NearMapActivity.this,"CALLING....",Toast.LENGTH_SHORT).show();

                    GetPhone getPhone = new GetPhone(getApplicationContext());
                    String phoneNumber;
                    try{
                        if((phoneNumber = getPhone.getNumber(fn)) != null) {
                            phoneNumber = "tel:" + phoneNumber;
                            startActivity(new Intent("android.intent.action.DIAL", Uri.parse(phoneNumber)));
                        }
                    }catch (IOException e) {
                        System.out.println("오류 발생");
                    }

                    bottomSheetDialog.dismiss();
                }
            });
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
            return false;
        }
    };
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

    double getDistance(double alat, double alng, double blat, double blng, String unit){
        double theta = alng - blng;
        double dist = Math.sin(deg2rad(alat)) * Math.sin(deg2rad(blat)) + Math.cos(deg2rad(alat)) * Math.cos(deg2rad(blat)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if(unit == "meter"){
            dist = dist * 1609.344;
        }

        return (dist);
    }


    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}