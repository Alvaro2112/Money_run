package sdp.moneyrun;

import android.location.Location;

public class LocationRepresentation {
    double latitude = 0.;
    double longitude = 0.;

    public LocationRepresentation(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Empty constructor for database
     */
    public LocationRepresentation(){ }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public float distanceTo(LocationRepresentation other){
        Location thisLocation = new Location("");
        thisLocation.setLatitude(this.getLatitude());
        thisLocation.setLongitude(this.getLongitude());

        Location otherLocation = new Location("");
        otherLocation.setLatitude(other.getLatitude());
        otherLocation.setLongitude(other.getLongitude());

        return thisLocation.distanceTo(otherLocation);
    }
}
