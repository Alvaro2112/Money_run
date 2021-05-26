package sdp.moneyrun.location;

import androidx.annotation.Nullable;

public interface LocationService {
    /**
     * Finds the current location of the user and pass it to the given callback function.
     *
     * @return the current location of the user
     */
    @Nullable
    LocationRepresentation getCurrentLocation();
}
