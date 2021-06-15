package org.techtown.myapplication.object;

public class ListViewItem {
    private int iconDrawable;
    private String facNameStr;
    private String addressStr;
    private String CenterNameStr;
    final private double lat, lng;

    public ListViewItem(int icon, String centerName, String facName, String address, double lat, double lng){
        iconDrawable = icon;
        CenterNameStr = centerName;
        facNameStr = facName;
        addressStr = address;
        this.lat = lat;
        this.lng = lng;
    }

    public void setCenterName(String title){
        CenterNameStr = title;
    }
    public void setFacName(String content){
        facNameStr = content;
    }
    public void setIcon(int icon){
        iconDrawable = icon;
    }
    public void setAddress(String address){addressStr = address;}

    public int getIcon(){
        return this.iconDrawable;
    }
    public double getLat(){return lat;}
    public double getLng(){return lng;}
    public String getFacNameStr(){
        return this.facNameStr;
    }
    public String getCenterNameStr(){
        return this.CenterNameStr;
    }
    public String getAddressStr(){return this.addressStr;}
}
