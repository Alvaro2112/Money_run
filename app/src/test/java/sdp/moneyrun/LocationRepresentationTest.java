package sdp.moneyrun;

import org.junit.Test;


import static org.junit.Assert.assertEquals;

public class LocationRepresentationTest {
    @Test
    public void constructionOfLocationRepresentationWorks(){
        LocationRepresentation lr = new LocationRepresentation(0, 0);
        assertEquals(1, 1);
    }

    @Test
    public void getLatitudeReturnsCorrectNumber(){
        double latitude = 0;
        double longitude = 1;
        LocationRepresentation lr = new LocationRepresentation(latitude, longitude);
        assertEquals(lr.getLatitude(), latitude, 1e-4);
    }

    @Test
    public void getLongitudeReturnsCorrectNumber(){
        double latitude = 0;
        double longitude = 1;
        LocationRepresentation lr = new LocationRepresentation(latitude, longitude);
        assertEquals(lr.getLongitude(), longitude, 1e-4);
    }
}
