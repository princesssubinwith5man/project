package org.techtown.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


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
        ItemList itemList = MainActivity2.infoList.get(i);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        markerOptions.title(itemList.centerName);
        markerOptions.snippet(itemList.facilityName);
        map.addMarker(markerOptions);
        map.setOnInfoWindowClickListener(infoWindowClickListener);

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
            Toast.makeText(NearMapActivity2.this, "마커 클릭 Marker ID : "+markerId+"("+location.latitude+" "+location.longitude+")", Toast.LENGTH_SHORT).show();

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