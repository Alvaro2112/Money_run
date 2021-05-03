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

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coin coin = (Coin) o;
        return this.value == coin.value &&
                this.latitude == coin.latitude &&
                this.longitude == coin.longitude;
    }

}

