package sdp.moneyrun.location;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * This class implements a representation of a location, a class containing information of
 * a location that is not automatically updated by the database.
 */
public class LocationRepresentation {
    double latitude = 0.;
    double longitude = 0.;

    public LocationRepresentation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocationRepresentation(@Nullable Location location) {
        if (location == null) {
            throw new IllegalArgumentException("location should not be null.");
        }
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    /**
     * Empty constructor for database
     */
    public LocationRepresentation() {
    }

    /**
     * @return the latitude of the location
     */
    public double getLatitude() {
        return this.latitude;
    }

    /**
     * @return the longitude of the location
     */
    public double getLongitude() {
        return this.longitude;
    }

    /**
     * @param other a location
     * @return the distance in meters from this location to another location
     */
    public double distanceTo(@NonNull LocationRepresentation other) {
        // constant for meters
        double R = 6371e3;

        // latitudes in radians
        double phi1 = this.latitude * Math.PI / 180;
        double phi2 = other.latitude * Math.PI / 180;
        double theta1 = this.latitude * Math.PI / 180;
        double theta2 = other.latitude * Math.PI / 180;

        // latitude, longitude differences
        double dphi = phi1 - phi2;
        double dtheta = theta1 - theta2;

        double a = Math.sin(dphi / 2) * Math.sin(dphi / 2) +
                Math.cos(phi1) * Math.cos(phi2) * Math.sin(dtheta / 2) * Math.sin(dtheta / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
