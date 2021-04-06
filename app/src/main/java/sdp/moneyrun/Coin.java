package sdp.moneyrun;

public class Coin {

    private double latitude;
    private double longitude;

    public Coin(double latitude,double longitude){

        this.latitude = latitude;
        this.longitude = longitude;
    }


    public double getLatitude(){return  latitude;}
    public double getLongitude(){return  longitude;}

}

