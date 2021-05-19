package org.techtown.myapplication;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;

class GetJSON extends AsyncTask<String, String, String> {

    class SiDoGunGuComparator implements Comparator<ItemList> {
        @Override
        public int compare(ItemList itemList1, ItemList itemList2) {
            if(itemList1.sido.equals(itemList2.sido))
                return itemList1.sigungu.compareTo(itemList2.sigungu);
            return itemList1.sido.compareTo(itemList2.sido);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
    @Override
    protected String doInBackground(String... strings) {
        Log.d("Task3", "POST");
        String temp = "Not Gained";
        try {
            temp = GET();
            Log.d("REST", temp);
            return temp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    private String GET() throws IOException {
        String API = "https://api.odcloud.kr/api/15077586/v1/centers?page=1&perPage=500&returnType=JSON&serviceKey=Oe0qPyqmZ3ua9MBvDCUWFQOM%2B5XLKbR6XPz2aQTFXEVG0vI7bMz9B1%2FVI%2FdK%2FYZgWb1U4QdoBnQ0wqSv4TTXjg%3D%3D";
        String data = "";

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

            JSONArray itemArray = root.getJSONArray("data");
            for (int i = 0; i < itemArray.length(); i++) {
                JSONObject item = itemArray.getJSONObject(i);
                Log.d("vaccine", item.getString("centerName"));
                ItemList itemList = new ItemList(
                        item.getString("address"),
                        item.getString("centerName"),
                        item.getString("centerType"),
                        item.getString("facilityName"),
                        item.getString("id"),
                        Double.parseDouble(item.getString("lat")),
                        Double.parseDouble(item.getString("lng")),
                        item.getString("org"),
                        item.getString("sido"),
                        item.getString("sigungu"),
                        item.getString("zipCode")

                );
                MainActivity.infoList.add(itemList);
            }

            Collections.sort(MainActivity.infoList, new SiDoGunGuComparator());

            Log.d("vaccine", MainActivity.infoList.size() + "개");

        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
        }


        return data;
    }


}

