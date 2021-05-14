package sdp.moneyrun;

import android.location.Location;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import sdp.moneyrun.map.LocationRepresentation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class LocationRepresentationTest {

    @Mock
    Location location;

    @Test
    public void constructionOfLocationRepresentationWorks1() {
        LocationRepresentation lr = new LocationRepresentation(0, 0);
        assertEquals(1, 1);
    }

    @Test
    public void constructionOfLocationRepresentationWorks2() {
        new LocationRepresentation();
        assertEquals(1, 1);

        location = Mockito.mock(Location.class);
        when(location.getLatitude()).thenReturn(4.0);
        when(location.getLongitude()).thenReturn(3.0);
        new LocationRepresentation(location);
    }

    @Test
    public void getLatitudeReturnsCorrectNumber() {
        double latitude = 0;
        double longitude = 1;
        LocationRepresentation lr = new LocationRepresentation(latitude, longitude);
        assertEquals(lr.getLatitude(), latitude, 1e-4);
    }

    @Test
    public void getLongitudeReturnsCorrectNumber() {
        double latitude = 0;
        double longitude = 1;
        LocationRepresentation lr = new LocationRepresentation(latitude, longitude);
        assertEquals(lr.getLongitude(), longitude, 1e-4);
    }

    @Test
    public void distanceToWorks1() {
        double latitude1 = 0;
        double longitude1 = 0;
        LocationRepresentation lr1 = new LocationRepresentation(latitude1, longitude1);
        double latitude2 = 0;
        double longitude2 = 0;
        LocationRepresentation lr2 = new LocationRepresentation(latitude2, longitude2);
        double distance = lr1.distanceTo(lr2);
        assertEquals(0, distance, 1e-4);
    }

    @Test
    public void distanceToWorks2() {
        // Moudon gare
        double latitude1 = 46.668114595627486;
        double longitude1 = 6.8024942281470295;
        LocationRepresentation lr1 = new LocationRepresentation(latitude1, longitude1);

        // Lucens gare
        double latitude2 = 46.70772939667488;
        double longitude2 = 6.842141965398342;
        LocationRepresentation lr2 = new LocationRepresentation(latitude2, longitude2);
        double distance = lr1.distanceTo(lr2);
        assertEquals(5350, distance, 10);
    }
}
