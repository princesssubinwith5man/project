package org.techtown.myapplication.object;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.techtown.myapplication.R;
import org.techtown.myapplication.object.ListViewItem;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {


    public ArrayList<ListViewItem> listViewItemList = new ArrayList<>();

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
        ImageView iconImageView;
        TextView titleTextView;
        TextView facNameTextView;
        TextView address;

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_item, viewGroup, false);
        }

        titleTextView = (TextView) view.findViewById(R.id.textViewCenterame);
        facNameTextView = (TextView) view.findViewById(R.id.textViewFacName);
        iconImageView = (ImageView) view.findViewById(R.id.iconImage);
        address = (TextView) view.findViewById(R.id.textViewaddress);

        ListViewItem listViewItem = listViewItemList.get(i);

        titleTextView.setText(listViewItem.getCenterNameStr());
        iconImageView.setImageResource(listViewItem.getIcon());
        facNameTextView.setText(listViewItem.getFacNameStr());
        address.setText(listViewItem.getAddressStr());

        return view;
    }

    public void addItem(int icon, String centerName, String facName, String address, double lat, double lng){
        ListViewItem item = new ListViewItem(R.drawable.duri, centerName, facName, address, lat, lng);

        listViewItemList.add(item);
    }
}
