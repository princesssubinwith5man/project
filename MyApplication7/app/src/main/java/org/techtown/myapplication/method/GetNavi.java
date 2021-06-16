package org.techtown.myapplication.method;


import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techtown.myapplication.activity.MainActivity;
import org.techtown.myapplication.activity.MainActivity2;
import org.techtown.myapplication.object.CoordList;
import org.techtown.myapplication.object.ItemList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;

public class GetNavi extends AsyncTask<Double, String, JSONArray> {

    public JSONArray coordinates;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONArray s) {
        super.onPostExecute(s);

    }
    @Override
    protected JSONArray doInBackground(Double... doubles) {
        Log.d("Task3", "POST");
        JSONArray temp = null;
        try {
            if(doubles.length ==4)
            temp = GET(doubles[0], doubles[1], doubles[2], doubles[3]);

            return temp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    private JSONArray GET(double startLng, double startLat, double targetLng, double targetLat) throws IOException {
        String token = "pk.eyJ1IjoianctamluIiwiYSI6ImNrcHlpN24wdjAwajEzMW1wcjYwbzE4YmYifQ.C0xheHu9IRuLdF-8KJ7XsA";
        String[] mode={"driving", "walking", "cycling"};
/*        double startLng = 128.87605666666664; // 걍 임시로 넣은거 강릉시청 -> 강릉예방접종센터
        double startLat = 37.75185166666667; // 이 값들도 어떻게 받아올지 생각해야되는데 해줭
        double targetLng = 128.8929531;
        double targetLat = 37.7725668;*/

        String API = "https://api.mapbox.com/directions/v5/mapbox/" + mode[0] + "/" + startLng + "," + startLat + ";" + targetLng + "," + targetLat + "?" + "geometries=geojson&access_token=" + token;
        String data = "";

    /*
    https://api.mapbox.com/directions/v5/mapbox/walking/128.87605666666664,37.75185166666667;128.8929531,37.7725668?geometries=geojson&access_token=pk.eyJ1IjoianctamluIiwiYSI6ImNrcHlpN24wdjAwajEzMW1wcjYwbzE4YmYifQ.C0xheHu9IRuLdF-8KJ7XsA
    위에 사이트 드가면 강릉시청 -> 강릉 예방접종센터 경로 정보 나옴
    참고 : https://velog.io/@michael00987/%ED%95%9C%EA%B5%AD%EC%97%90%EC%84%9C-%EA%B5%AC%EA%B8%80%EC%A7%80%EB%8F%84%EC%97%90-%EA%B8%B8%EC%B0%BE%EA%B8%B0-%EC%B6%94%EA%B0%80-%ED%95%98%EA%B8%B0
    polyline 그리기
    https://developers.google.com/maps/documentation/android-sdk/polygon-tutorial?hl=ko
     */

        try {
            URL url = new URL(API);
            Log.d("Api", "The response is :" + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            String line;
            String result = "";

            BufferedReader bf;
            bf = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((line = bf.readLine()) != null) {
                result = result.concat(line);

            }
            Log.d("Json", "The response is :" + result);
            JSONObject root = new JSONObject(result);
            JSONArray routes = root.getJSONArray("routes");
            for (int i = 0; i < routes.length(); i++) {
                JSONObject c = routes.getJSONObject(i);
                JSONObject geometry = c.getJSONObject("geometry");
                Log.d("geo", "The response is :" + geometry);
                coordinates = geometry.getJSONArray("coordinates");
                Log.d("coo", "The response is :" + coordinates); // coordinates에 좌표값 들어있음 lng , lat 쌍
                /*
                coordinates에 좌표값 들어있음 latlng 순서 아니고 lng , lat 쌍
                이제 이값도 반환해서
                Polyline polyline1 = googleMap.addPolyline(new PolylineOptions().clickable(true).add(new LatLng(lat값, lng값)));
                하면 경로 그려줌 해줭
                배열에 들어있는값 다 그리면 경로 완성!
                https://developers.google.com/maps/documentation/android-sdk/polygon-tutorial?hl=ko
                다중선을 추가하여 지도에 선그리기 참고
                난 잘게
                 */

            }


        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
        }


        return coordinates;
    }


}


