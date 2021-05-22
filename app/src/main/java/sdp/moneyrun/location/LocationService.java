package sdp.moneyrun.location;
import android.location.Criteria;

public interface LocationService {
    /**
     * Finds the current location of the user and pass it to the given callback function.
     * @return the current location of the user
     */
    LocationRepresentation getCurrentLocation();
}
