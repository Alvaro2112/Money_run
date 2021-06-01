package sdp.moneyrun.map;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Random;

public class CoinGenerationHelper {

    public static final double VALUE_RADIUS = 100;
    /**
     * https://stackoverflow.com/a/36919707
     *
     * @param currentLocation Center of the radius
     * @param maxRadius       Biggest distance for a coin compared to the center
     * @param minRadius       Minimum distance from a coin to the radius
     * @return a location between min radius and max radius from the currentLocation
     */
    @NonNull
    public static Location getRandomLocation(@Nullable Location currentLocation, double maxRadius, double minRadius) {
        if (maxRadius <= 0 || currentLocation == null || minRadius <= 0)
            throw new IllegalArgumentException();
        if (maxRadius <= minRadius)
            throw new IllegalArgumentException("Min radius is bigger than maxRadius");

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
        } while (TrackedMap.distance(foundLatitude, foundLongitude, currentLocation.getLatitude(), currentLocation.getLongitude()) < minRadius);

        Location copy = new Location("");
        copy.setLatitude(foundLatitude);
        copy.setLongitude(foundLongitude);
        return copy;
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

    /**
     * computes the distance between the location and the closest coin
     * @param loc
     * @param coins
     * @return the distance  between the location and the closest coin
     */
    public static double minDistWithExistingCoins(Location loc, List<Coin> coins){
        if (coins == null) {
            throw new IllegalStateException();
        }
        if(coins.isEmpty()){
            return Double.MAX_VALUE;
        }
        double minDist = Double.MAX_VALUE;
        int retained_index = -1;

        for(int i = 0; i < coins.size(); i++){
            Coin coin = coins.get(i);
            double distance = TrackedMap.distance(loc.getLatitude(), loc.getLongitude(), coin.getLatitude(), coin.getLongitude());
            if (minDist > distance){
                minDist = distance;
                retained_index = i;
            }
        }
        if (retained_index < 0) return -1.0;
        else return minDist;
    }


}

