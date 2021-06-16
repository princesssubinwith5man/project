package org.techtown.myapplication.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;


import org.techtown.myapplication.method.GetPhone;
import org.techtown.myapplication.object.ItemList;
import org.techtown.myapplication.activity.MainActivity2;
import org.techtown.myapplication.R;

import java.io.IOException;


public class NearMapActivity2 extends AppCompatActivity
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
    private double cur_lat; // 현위치
    private double cur_lng;
    View marker_root_view;
    private boolean permissionDenied = false;
    private GoogleMap map;

    private void setCustomMarkerView(){
        marker_root_view = LayoutInflater.from(this).inflate(R.layout.marker_background, null);
    }
    private Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

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
                cur_lat = latitude;
                cur_lng = longitude;

                Toast.makeText(this, "new2위도: " + location.getLatitude() + "경도: " + location.getLongitude(), Toast.LENGTH_LONG)
                        .show();

                ItemList itemList = MainActivity2.infoList.get(0);

                LatLng curLoc = new LatLng(latitude, longitude); // 현재 위치
                for(int i=0; i<MainActivity2.infoList.size(); i++){
                    double distance = getDistance(latitude, longitude,
                            MainActivity2.infoList.get(i).lat, MainActivity2.infoList.get(i).lng, "kilometer");

                    LatLng targetLoc = new LatLng(MainActivity2.infoList.get(i).lat, MainActivity2.infoList.get(i).lng); // 계산할 위치
                    double result = calcLocation(curLoc, targetLoc);

                        if(result < 10000.0) { // 미터단위
                            Log.d("log:", "위치: " + i + " " +  itemList.centerName + " 거리차이는 " + result);
                            drawMarker(targetLoc, i);
                        }


                }



                map.moveCamera(CameraUpdateFactory.newLatLngZoom(curLoc, 10));

            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            ActivityCompat.requestPermissions(this, MainActivity2.REQUIRED_PERMISSIONS,
                    MainActivity2.PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "new위도: " + location.getLatitude() + "경도: " + location.getLongitude(), Toast.LENGTH_LONG)
                .show();
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        ItemList itemList = MainActivity2.infoList.get(0);
        double minDistance = Double.MAX_VALUE;


        LatLng curLoc = new LatLng(latitude, longitude); // 현재 위치
        for(int i=0; i<MainActivity2.infoList.size(); i++){
            double distance = getDistance(latitude, longitude,
                    MainActivity2.infoList.get(i).lat, MainActivity2.infoList.get(i).lng, "kilometer");

            LatLng targetLoc = new LatLng(MainActivity2.infoList.get(i).lat, MainActivity2.infoList.get(i).lng); // 계산할 위치
            double result = calcLocation(curLoc, targetLoc);

            if(result < 10000.0) { // 미터단위
                Log.d("log:", "위치: " + i + " " +  itemList.centerName + " 거리차이는 " + result);
                drawMarker(targetLoc, i);
            }


        }


        LatLng nowLocation = new LatLng(itemList.lat, itemList.lng);


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(nowLocation, 14));
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

    public void drawMarker(LatLng location, int i)
    {
        setCustomMarkerView();
        ItemList itemList = MainActivity2.infoList.get(i);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        //markerOptions.title(itemList.centerName);
        //markerOptions.snippet(itemList.facilityName);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker_root_view)));
        map.addMarker(markerOptions);

        map.setOnInfoWindowClickListener(infoWindowClickListener);



        cn = itemList.centerName;
        fn = itemList.facilityName;
        ad = itemList.address;
        //마커 클릭 리스너
        map.setOnMarkerClickListener(markerClickListener);

    }
    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            String markerId = marker.getId();
            Toast.makeText(NearMapActivity2.this, "정보창 클릭 Marker ID : "+markerId, Toast.LENGTH_SHORT).show();
        }
    };

    //마커 클릭 리스너
    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {



            String markerId = marker.getId();
            //선택한 타겟위치
            LatLng location = marker.getPosition();
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
            //Toast.makeText(NearMapActivity2.this, "마커 클릭 Marker ID : "+markerId+"("+location.latitude+" "+location.longitude+")", Toast.LENGTH_SHORT).show();
            Log.d("log:", "현재 lat lng : " + cur_lat + "," + cur_lng + "목표 latlng : " + location);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                    NearMapActivity2.this, R.style.BottomSheetDialogTheme
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
                    Toast.makeText(NearMapActivity2.this,"CALLING....",Toast.LENGTH_SHORT).show();

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
            bottomSheetView.findViewById(R.id.buttonShare1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
            return false;
        }
    };
    private float calcLocation(LatLng curLoc, LatLng targetLoc) {
        float[] dist = new float[1];
        Location.distanceBetween(curLoc.latitude, curLoc.longitude, targetLoc.latitude, targetLoc.longitude, dist);
        return dist[0];
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