package sdp.moneyrun.aaweather;

import org.junit.Test;

import java.util.ArrayList;

import sdp.moneyrun.weather.Address;

import static org.junit.Assert.assertEquals;

public class AddressTest {

    @Test
    public void toStringWorksWithSeparator() {
        ArrayList<String> a = new ArrayList<>();
        a.add("Rue du prince");
        a.add("34");
        a.add("Ecublens");
        Address address = new Address(a);
        assertEquals(address.toString("-"), "Rue du prince-34-Ecublens-");
    }

    @Test
    public void toStringWorks() {
        ArrayList<String> a = new ArrayList<>();
        a.add("Rue du prince");
        a.add("34");
        a.add("Ecublens");
        Address address = new Address(a);
        assertEquals(address.toString(), "Rue du prince\n34\nEcublens\n");
    }
}
