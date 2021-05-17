package sdp.moneyrun.map;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mapbox.geojson.Feature;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class CoinGenerationHelper {

    public static  final double  VALUE_RADIUS = 100;

    /**
     *      https://stackoverflow.com/a/36919707
     * @param currentLocation  Center of the radius
     * @param maxRadius Biggest distance for a coin compared to the center
     * @param minRadius Mininum distance from a coin to the radius
     * @return a location between min radius and max radius from the currentLocation
     */
    public static  Location getRandomLocation(Location currentLocation, double maxRadius, double minRadius){
        if(maxRadius<=0 || currentLocation == null || minRadius <= 0) throw new IllegalArgumentException();
        if(maxRadius <= minRadius ) throw new IllegalArgumentException("Min radius is bigger than maxRadius");

        double x0 = currentLocation.getLongitude();
        double y0 = currentLocation.getLatitude();

        Random random = new Random();
        double foundLatitude;
        double foundLongitude;
        // Convert radius from meters to degrees.
        double radiusInDegrees = maxRadius / 111320f;

        do {// Get a random distance and a random angle.
            double u = random.nextDouble();
            double v = random.nextDouble();
            double w = radiusInDegrees * Math.sqrt(u);
            double t = 2 * Math.PI * v;
            // Get the x and y delta values.
            double x = w * Math.cos(t);
            double y = w * Math.sin(t);

            // Compensate the x value.
            double new_x = x / Math.cos(Math.toRadians(y0));

            foundLatitude = y0 + y;
            foundLongitude = x0 + new_x;
        }while (TrackedMap.distance(foundLatitude, foundLongitude, currentLocation.getLatitude(), currentLocation.getLongitude()) < minRadius);

        Location copy = new Location("");
        copy.setLatitude(foundLatitude);
        copy.setLongitude(foundLongitude);
        return copy;
    }

    public static boolean checkIndividualFeature(@NonNull Feature feature, @NonNull List<String> inappropriateLocations) {
        String[] relevantFields = new String[]{"type", "class", "name"};
        for (String field : relevantFields) {
            if (feature.properties().has(field)) {
                String locationType = feature.properties().get(field).toString();
                if (inappropriateLocations.contains(locationType.substring(1, locationType.length() - 1).toLowerCase(Locale.ROOT))) {
                    return false;
                }
            }
        }// An inappropriate characteristics may be in different property fields

        return true;
    }

    public static boolean hasAtLeasOneProperty(@NonNull List<Feature> features) {
        for (Feature feature : features) {
            if (!feature.properties().toString().equals("{}")) {
                return true;
            }
        }
        return false;
    }

    public static int coinValue(@Nullable Location coinLoc, @Nullable Location centerLoc) {
        if (coinLoc == null) {
            throw new NullPointerException("coin loc is null");
        }
        if (centerLoc == null) {
            throw new NullPointerException("center location is null");
        }
        double dist = TrackedMap.distance(coinLoc.getLatitude(), coinLoc.getLongitude(), centerLoc.getLatitude(), centerLoc.getLongitude());
        return (int) Math.ceil(dist / VALUE_RADIUS);
    }
}

