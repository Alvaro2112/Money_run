package sdp.moneyrun;

import com.mapbox.mapboxsdk.geometry.LatLng;

public class Coin {

    private double latitude;
    private double longitude;

    public Coin(double latitude,double longitude){

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Coin(LatLng latLng){
        this.latitude = latLng.getLatitude();
        this.longitude = latLng.getLongitude();

    }

    public double getLatitude(){return  latitude;}
    public double getLongitude(){return  longitude;}

}

