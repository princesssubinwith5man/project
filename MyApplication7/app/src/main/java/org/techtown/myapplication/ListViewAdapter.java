package org.techtown.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    private ImageView iconImageView;
    private TextView titleTextView;
    private TextView contentTextView;

    private ArrayList<ListViewItem> listViewItemList = new ArrayList<>();

    public ListViewAdapter(){

    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return listViewItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int pos = i;
        final Context context = viewGroup.getContext();

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_item, viewGroup, false);
        }

        titleTextView = (TextView) view.findViewById(R.id.textViewCenterame);
        contentTextView = (TextView) view.findViewById(R.id.textViewFacName);
        iconImageView = (ImageView) view.findViewById(R.id.iconImage);

        ListViewItem listViewItem = listViewItemList.get(i);

        titleTextView.setText(listViewItem.getCenterNameStr());
        iconImageView.setImageResource(listViewItem.getIcon());
        contentTextView.setText(listViewItem.getContentStr());

        // 리스트뷰 온클릭 리스너
/*        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast myToast = Toast.makeText(MainActivity2.this, "위도: " + list1.get(position).get("item1") + " 경도: " + list1.get(position).get("item2"), Toast.LENGTH_SHORT);
                myToast.show();
                double lat = listview.
                        lat1 = list1.get(position).get("item1");
                lng1 = list1.get(position).get("item2");
                Centername = list2.get(position).get("cn");
                fn = list2.get(position).get("fn");
                Intent intent = new Intent(MainActivity2.this, GMapActivity.class);
                intent.putExtra("lat", lat1);
                intent.putExtra("lng", lng1);
                intent.putExtra("centername", Centername);
                intent.putExtra("fac", fn);
                startActivity(intent);
            }
        });*/



        return view;
    }

    public void addItem(int icon, String centerName, String facName, String address, double lat, double lng){
        ListViewItem item = new ListViewItem(R.drawable.listview, centerName, facName, address, lat, lng);

        listViewItemList.add(item);
    }
}
