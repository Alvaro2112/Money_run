package sdp.moneyrun.map;

public class Coin {

    private double latitude;
    private double longitude;
    private int value;

    //For database purpose
    public Coin(){}

    public Coin(double latitude,double longitude,int value){

        this.latitude = latitude;
        this.longitude = longitude;
        this.value = value;

    }

    public double getLatitude(){return  latitude;}
    public double getLongitude(){return  longitude;}
    public int getValue(){return value;}

}

