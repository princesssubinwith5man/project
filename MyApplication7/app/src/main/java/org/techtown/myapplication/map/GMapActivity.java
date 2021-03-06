package org.techtown.myapplication.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.techtown.myapplication.R;
import org.techtown.myapplication.method.GetNavi;
import org.techtown.myapplication.method.GetPhone;

import java.io.IOException;
import java.util.ArrayList;

import static org.techtown.myapplication.activity.MainActivity.PERMISSIONS_REQUEST_CODE;
import static org.techtown.myapplication.activity.MainActivity.REQUIRED_PERMISSIONS;


public class GMapActivity extends AppCompatActivity
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
    View marker_root_view;
    private boolean permissionDenied = false;
    private GoogleMap map;


    private void setCustomMarkerView() {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }//????????? ??????

        setContentView(R.layout.gmap_activity);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // ??????????????? ????????? ?????????????????? ?????? ????????????
        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat", 0);
        lng = intent.getDoubleExtra("lng", 0);
        cn = intent.getStringExtra("centername");
        fn = intent.getStringExtra("fac");
        ad = intent.getStringExtra("add");

        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                PERMISSIONS_REQUEST_CODE);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        map = googleMap;
        setCustomMarkerView();

        // ?????? ???????????? & ?????? ????????????
        Log.d("tag:", "result: " + lat + " " + lng);
        LatLng Corona = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(Corona);
        markerOptions.title(cn);
        markerOptions.snippet(fn);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker_root_view)));
        //tv_marker.setText(fn);
        googleMap.addMarker(markerOptions);


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Corona, 14));
        map.setOnInfoWindowClickListener(infoWindowClickListener);

        //?????? ?????? ?????????
        map.setOnMarkerClickListener(markerClickListener);

        enableMyLocation();
    }

    //????????? ?????? ????????? ?????? ????????? ??????
    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            /*final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();*/


            String markerId = marker.getId();
            Toast.makeText(GMapActivity.this, "????????? ?????? Marker ID : " + markerId, Toast.LENGTH_SHORT).show();

        }
    };

    //?????? ?????? ?????????
    // ?????? ?????? ??? ?????? ?????? ???????????? ???????????? & ????????? ?????? ????????? ??????
    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                    GMapActivity.this, R.style.BottomSheetDialogTheme
            );
            View bottomSheetView = LayoutInflater.from(getApplicationContext())
                    .inflate(
                            R.layout.layout_bottom_sheet,
                            (LinearLayout) findViewById(R.id.bottomSheetConteainer)
                    );
            TextView tv = bottomSheetView.findViewById(R.id.center_name_text);
            TextView tv1 = bottomSheetView.findViewById(R.id.fa_name_text);
            TextView tv2 = bottomSheetView.findViewById(R.id.address_text);
            tv.setText(cn);
            tv1.setText(fn);
            tv2.setText(ad);
            bottomSheetView.findViewById(R.id.buttonShare).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { // ?????? ?????? ???????????? ?????? ??????
                    GetPhone getPhone = new GetPhone(getApplicationContext());
                    String phoneNumber;
                    try {
                        if ((phoneNumber = getPhone.getNumber(fn)) != null) {
                            phoneNumber = "tel:" + phoneNumber;
                            startActivity(new Intent("android.intent.action.DIAL", Uri.parse(phoneNumber)));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("?????? ?????? ?????? ??????");
                    }

                    Toast.makeText(GMapActivity.this, "CALLING....", Toast.LENGTH_SHORT).show();

                    bottomSheetDialog.dismiss();
                }
            });

            bottomSheetView.findViewById(R.id.route_find).setOnClickListener(new View.OnClickListener() {
                // ????????? ?????? ?????? ?????????
                @Override
                public void onClick(View v) {

                    ArrayList<LatLng> pointList = new ArrayList<>();
                    final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    double distance;
                    int int_distance;
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (map != null) {
                            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            double longitude = location.getLongitude();
                            double latitude = location.getLatitude();

                            try {
                                // MapBox api ??????
                                GetNavi getNavi = new GetNavi();
                                JSONArray route = getNavi.execute(longitude, latitude, lng, lat).get();

                                int len = route.length();

                                for (int i = 0; i < len; i++) {
                                    // json ?????? -> ????????? -> ????????? ??????
                                    String str = route.get(i).toString();
                                    str = str.replace("[", "").replace("]", "");
                                    String[] token = str.split(",");

                                    pointList.add(new LatLng(Double.parseDouble(token[1]), Double.parseDouble(token[0])));

                                }

                                PolylineOptions polylineOptions = new PolylineOptions().clickable(true).color(Color.BLUE);
                                for (int i = 0; i < pointList.size(); i++) {
                                    polylineOptions.add(pointList.get(i));
                                }
                                map.addPolyline(polylineOptions);
                                Toast.makeText(getApplicationContext(), "?????? ?????? ????????? ???...", Toast.LENGTH_LONG);
                                Log.d("tag","result"+calcLocation(pointList.get(0),pointList.get(pointList.size()-1)));
                                distance = calcLocation(pointList.get(0),pointList.get(pointList.size()-1));
                                int_distance = (int)distance;
                                if(int_distance/1000 < 3)
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(pointList.get((pointList.size()-1)/2), 14));
                                else if(int_distance/1000 < 25)
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(pointList.get((pointList.size()-1)/2), 11));
                                else if(int_distance/1000 < 50)
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(pointList.get((pointList.size()-1)/2), 10));
                                else if(int_distance/1000 < 100)
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(pointList.get((pointList.size()-1)/2), 9));
                                else if(int_distance/1000 < 201)
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(pointList.get((pointList.size()-1)/2), 8));
                                else
                                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(pointList.get((pointList.size()-1)/2), 7));

                                bottomSheetDialog.dismiss();

                            } catch (Exception e) {
                            }
                        }
                    }
                }

            });

            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
            return false;
        }
    };


    // ---------------------- ?????? ???????????? gps ?????? ?????????
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
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                   PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "??????: " + location.getLatitude() + "??????: " + location.getLongitude(), Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
                .show();

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

            permissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {

            permissionDenied = false;
        }
    }
    double getDistance(double alat, double alng, double blat, double blng, String unit) {
        double theta = alng - blng;
        double dist = Math.sin(deg2rad(alat)) * Math.sin(deg2rad(blat)) + Math.cos(deg2rad(alat)) * Math.cos(deg2rad(blat)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if (unit == "meter") {
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
    private float calcLocation(LatLng curLoc, LatLng targetLoc) {
        float[] dist = new float[1];
        Location.distanceBetween(curLoc.latitude, curLoc.longitude, targetLoc.latitude, targetLoc.longitude, dist);
        return dist[0];
    }
}