package org.techtown.myapplication;

public class ListViewItem {
    private int iconDrawable;
    private String contentStr;
    private String addressStr;
    private String CenterNameStr;
    private double lat, lng;

    public ListViewItem(int a, String b, String c, String d, double f, double g){
        iconDrawable = a;
        contentStr = b;
        addressStr = c;
        CenterNameStr = d;
        lat = f;
        lng = g;
    }

    public void setCenterName(String title){
        CenterNameStr = title;
    }
    public void setFacName(String content){
        contentStr = content;
    }
    public void setIcon(int icon){
        iconDrawable = icon;
    }
    public void setAddress(String address){addressStr = address;}

    public int getIcon(){
        return this.iconDrawable;
    }
    public String getContentStr(){
        return this.contentStr;
    }
    public String getCenterNameStr(){
        return this.CenterNameStr;
    }
    public String getAddressStr(){return this.addressStr;}
}
