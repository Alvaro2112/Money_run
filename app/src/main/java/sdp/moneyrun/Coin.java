package sdp.moneyrun;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

public class Coin {

    private double latitude;
    private double longitude;
    private SymbolOptions symbolOption;
    private int value;

    public Coin(double latitude,double longitude,int value){

        this.latitude = latitude;
        this.longitude = longitude;
        symbolOption = new SymbolOptions().withLatLng(new LatLng(latitude,longitude));
        this.value = value;

    }


    public double getLatitude(){return  latitude;}
    public double getLongitude(){return  longitude;}
    public SymbolOptions getSymbolOption(){return symbolOption;}
    public int getValue(){return value;}

}

