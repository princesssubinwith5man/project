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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.techtown.myapplication.R;
import org.techtown.myapplication.activity.MainActivity2;
import org.techtown.myapplication.method.GetNavi;
import org.techtown.myapplication.method.GetPhone;

import java.io.IOException;
import java.util.ArrayList;


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
    TextView tv_marker;
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
        setContentView(R.layout.gmap_activity);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat", 0);
        lng = intent.getDoubleExtra("lng", 0);
        cn = intent.getStringExtra("centername");
        fn = intent.getStringExtra("fac");
        ad = intent.getStringExtra("add");

        ActivityCompat.requestPermissions(this, MainActivity2.REQUIRED_PERMISSIONS,
                MainActivity2.PERMISSIONS_REQUEST_CODE);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        map = googleMap;
        setCustomMarkerView();

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

        //마커 클릭 리스너
        map.setOnMarkerClickListener(markerClickListener);

        enableMyLocation();
    }

    //정보창 클릭 리스너
    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            /*final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();*/


            String markerId = marker.getId();
            Toast.makeText(GMapActivity.this, "정보창 클릭 Marker ID : " + markerId, Toast.LENGTH_SHORT).show();

        }
    };

    //마커 클릭 리스너
    GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            String markerId = marker.getId();
            //선택한 타겟위치
            LatLng location = marker.getPosition();
            //Toast.makeText(GMapActivity.this, "마커 클릭 Marker ID : "+markerId+"("+location.latitude+" "+location.longitude+")", Toast.LENGTH_SHORT).show();
                   /* AlertDialog.Builder builder = new AlertDialog.Builder(GMapActivity.this);

                    builder.setTitle(cn).setMessage(fn);

                    AlertDialog alertDialog = builder.create();

                    alertDialog.show();*/
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
                public void onClick(View view) { // 전화 버튼 눌렀을때 전화 걸기
                    GetPhone getPhone = new GetPhone(getApplicationContext());
                    String phoneNumber;
                    try {
                        if ((phoneNumber = getPhone.getNumber(fn)) != null) {
                            phoneNumber = "tel:" + phoneNumber;
                            startActivity(new Intent("android.intent.action.DIAL", Uri.parse(phoneNumber)));
                        }
                    } catch (IOException e) {
                        System.out.println("오류 발생");
                    }

                    Toast.makeText(GMapActivity.this, "CALLING....", Toast.LENGTH_SHORT).show();

                    bottomSheetDialog.dismiss();
                }
            });

            bottomSheetView.findViewById(R.id.route_find).setOnClickListener(new View.OnClickListener() {
                // 길찾기 버튼 클릭 리스너
                @Override
                public void onClick(View v) {

                    final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (map != null) {
                            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            double longitude = location.getLongitude();
                            double latitude = location.getLatitude();

                            try {
                                GetNavi getNavi = new GetNavi();


                                JSONArray route = getNavi.execute(longitude, latitude, lng, lat).get();

                                ArrayList<LatLng> pointList = new ArrayList<>();
                                int len = route.length();

                                for (int i = 0; i < len; i++) {
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
                                Toast.makeText(getApplicationContext(), "지도 경로 그리는 중...", Toast.LENGTH_LONG);
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(pointList.get(0), 15));
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