package sdp.moneyrun.map;

import android.location.Location;

import java.util.Random;

public class RandomLocation {

    /**
     https://stackoverflow.com/a/36919707
     * @param currentLocation
     * @param radius
     * @return a Location within a radius in meters
     */
    public static  Location getRandomLocation(Location currentLocation, int radius){
        if(radius<=0 || currentLocation == null) throw new IllegalArgumentException();
        double x0 = currentLocation.getLongitude();
        double y0 = currentLocation.getLatitude();

        Random random = new Random();

        // Convert radius from meters to degrees.
        double radiusInDegrees = radius / 111320f;

        // Get a random distance and a random angle.
        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        // Get the x and y delta values.
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Compensate the x value.
        double new_x = x / Math.cos(Math.toRadians(y0));

        double foundLatitude;
        double foundLongitude;

        foundLatitude = y0 + y;
        foundLongitude = x0 + new_x;

        Location copy = new Location("");
        copy.setLatitude(foundLatitude);
        copy.setLongitude(foundLongitude);
        return copy;
    }
}

