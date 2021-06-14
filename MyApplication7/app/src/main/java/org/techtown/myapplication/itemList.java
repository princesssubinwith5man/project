package org.techtown.myapplication;

class ItemList {

    public String address;//인천광역시 강화군 강화읍 고비고개로19번길 12
    public String centerName; //코로나19 인천광역시 강화군 예방접종센터
    public String centerType;
    public String facilityName;//강화문예회관
    public String id;
    public double lat;  //위도
    public double lng;  //경도(경찰과도둑)
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