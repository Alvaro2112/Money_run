package sdp.moneyrun.weather;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WeatherReportTest {

    @Test
    public void getAverageTemperatureWorks() {
        WeatherReport weatherReport = new WeatherReport(1, 1, 1, "sunny", "aa");
        assertEquals(weatherReport.getAverageTemperature(), 1, 0);
    }

    @Test
    public void getMaximalTemperatureWorks() {
        WeatherReport weatherReport = new WeatherReport(1, 1, 1, "sunny", "aa");
        assertEquals(weatherReport.getMaximalTemperature(), 1, 0);
    }

    @Test
    public void getMinimumTemperatureWorks() {
        WeatherReport weatherReport = new WeatherReport(1, 1, 1, "sunny", "aa");
        assertEquals(weatherReport.getMinimumTemperature(), 1, 0);
    }

    @Test
    public void getWeatherTypeWorks() {
        WeatherReport weatherReport = new WeatherReport(1, 1, 1, "sunny", "aa");
        assertEquals(weatherReport.getWeatherType(), "sunny");
    }

    @Test
    public void getWeatherIconWorks() {
        WeatherReport weatherReport = new WeatherReport(1, 1, 1, "sunny", "aa");
        assertEquals(weatherReport.getWeatherIcon(), "aa");
    }

    @Test
    public void setAverageTemperatureWorks() {
        WeatherReport weatherReport = new WeatherReport(1, 1, 1, "sunny", "aa");
        weatherReport.setAverageTemperature(2);
        assertEquals(weatherReport.getAverageTemperature(), 2, 0);
    }

    @Test
    public void setMaximalTemperatureWorks() {
        WeatherReport weatherReport = new WeatherReport(1, 1, 1, "sunny", "aa");
        weatherReport.setMaximalTemperature(2);
        assertEquals(weatherReport.getMaximalTemperature(), 2, 0);
    }

    @Test
    public void setMinimumTemperatureWorks() {
        WeatherReport weatherReport = new WeatherReport(1, 1, 1, "sunny", "aa");
        weatherReport.setMinimumTemperature(2);
        assertEquals(weatherReport.getMinimumTemperature(), 2, 0);
    }

    @Test
    public void setWeatherTypeWorks() {
        WeatherReport weatherReport = new WeatherReport(1, 1, 1, "sunny", "aa");
        weatherReport.setWeatherType("cloudy");
        assertEquals(weatherReport.getWeatherType(), "cloudy");
    }

    @Test
    public void setWeatherIconWorks() {
        WeatherReport weatherReport = new WeatherReport(1, 1, 1, "sunny", "aa");
        weatherReport.setWeatherIcon("bb");
        assertEquals(weatherReport.getWeatherIcon(), "bb");
    }

    @Test(expected = NullPointerException.class)
    public void setWeatherTypeFailsCorrectly() {
        WeatherReport weatherReport = new WeatherReport(1, 1, 1, "sunny", "aa");
        weatherReport.setWeatherType(null);
    }

    @Test(expected = NullPointerException.class)
    public void setWeatherIconFailsCorrectly() {
        WeatherReport weatherReport = new WeatherReport(1, 1, 1, "sunny", "aa");
        weatherReport.setWeatherIcon(null);
    }

    @Test(expected = NullPointerException.class)
    public void constructorFailsCorrectly1() {
        WeatherReport weatherReport = new WeatherReport(1, 1, 1, null, "aa");
    }

    @Test(expected = NullPointerException.class)
    public void constructorFailsCorrectly2() {
        WeatherReport weatherReport = new WeatherReport(1, 1, 1, "sunny", null);
    }
}
