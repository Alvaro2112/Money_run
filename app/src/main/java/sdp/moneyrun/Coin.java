package sdp.moneyrun;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

public class Coin {

    private double latitude;
    private double longitude;
    private SymbolOptions symbolOption;

    public Coin(double latitude,double longitude){

        this.latitude = latitude;
        this.longitude = longitude;
        symbolOption = new SymbolOptions().withLatLng(new LatLng(latitude,longitude));

    }


    public double getLatitude(){return  latitude;}
    public double getLongitude(){return  longitude;}
    public SymbolOptions getSymbolOption(){return symbolOption;}

}

