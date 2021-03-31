package sdp.moneyrun;

import androidx.test.core.app.ActivityScenario;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MapInstrumentedTest {

        @Test
        public void AddMarkerAddsMarker() {
            try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
                float lat = 12f;
                float lon = 12f;
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                scenario.onActivity(a ->{
                    a.addMarker(lat,lon);
                    a.addMarker(lat+2f,lon);
                        });

                Thread.sleep(2500);
                scenario.onActivity(a ->{
                   assertEquals( a.getSymbolManager().getAnnotations().size(),2);
                });

            }
            catch (Exception e){
                assertEquals(-1,2);
                e.printStackTrace();
            }
        }

    @Test
    public void moveCameraToWorks() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            float lat = 12f;
            float lon = 12f;
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a ->{
                a.moveCameraTo(lat,lon);
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            scenario.onActivity(a ->{
                LatLng latLng = a.getMapboxMap().getCameraPosition().target;
                assertEquals(latLng.getLatitude(), 12.0,0.1);
                assertEquals(latLng.getLongitude(), 12.0,0.1);
                System.out.println("LONGITUDE IS "+String.valueOf(latLng.getLatitude()));
            });
        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }

    @Test
    public void testSymbolManager() {
        try (ActivityScenario<MapActivity> scenario = ActivityScenario.launch(MapActivity.class)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            scenario.onActivity(a->{
               assertEquals( a.getSymbolManager().getIconAllowOverlap(),true);
                assertEquals( a.getSymbolManager().getTextAllowOverlap(),true);

            });
        }
        catch (Exception e){
            assertEquals(-1,2);
            e.printStackTrace();
        }
    }



}
