package org.techtown.myapplication;

class ItemList {

    public String address;
    public String centerName;
    public String centerType;
    public String facilityName;
    public String id;
    public double lat;
    public double lng;
    public String org;
    public String sido;
    public String sigungu;
    public String zipCode;


    public ItemList(String address, String centerName, String centerType, String facilityName, String id, double lat, double lng, String org, String sido, String sigungu, String zipCode) {
        this.address = address;
        this.centerName = centerName;
        this.centerType = centerType;
        this.facilityName = facilityName;
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.org = org;
        this.sido = sido;
        this.sigungu = sigungu;
        this.zipCode = zipCode;
    }

}