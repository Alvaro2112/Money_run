package sdp.moneyrun;

import android.location.Location;

/**
 * This class implements a representation of a location, a class containing informations of
 * a location that is not automatically updated by the database.
 *
 * @author Arnaud Poletto
 */
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

    /**
     * @return the latitude of the location
     */
    public double getLatitude(){
        return this.latitude;
    }

    /**
     * @return the longitude of the location
     */
    public double getLongitude(){
        return this.longitude;
    }

    /**
     *
     * @param other a location
     * @return the distance in meters from this location to another location
     */
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
