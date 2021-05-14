package sdp.moneyrun.weather;


import android.content.Context;
import android.location.Geocoder;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sdp.moneyrun.map.LocationRepresentation;

public class AddressGeocoder {

    private final Geocoder geocoder;

    AddressGeocoder(Geocoder geocoder) {
        this.geocoder = geocoder;
    }

    public static AddressGeocoder fromContext(Context context) {
        return new AddressGeocoder(new Geocoder(context));
    }


    /**
     * This function will return an usable address from a Location
     *
     * @param location The location of the address to be fount
     * @return The address
     * @throws IOException
     */
    public android.location.Address getAddress(@NonNull LocationRepresentation location) throws IOException {
        List<android.location.Address> addressList = this.geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        android.location.Address address;

        if (addressList.size() > 0) {
            address = addressList.get(0);
        } else {
            address = null;
        }

        return address;
    }

    public Address convertToAddress(android.location.Address address) {
        if (address == null) {
            throw new NullPointerException();
        }
        List<String> addressLines = new ArrayList<>();
        for (int i = 0; i <= address.getMaxAddressLineIndex(); ++i)
            addressLines.add(address.getAddressLine(i));

        return new Address(addressLines);
    }
}