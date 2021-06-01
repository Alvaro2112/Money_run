package sdp.moneyrun.weather;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WeatherForecastTest {

    @Test
    public void getWeatherReportWorks() {
        WeatherReport weatherReport1 = new WeatherReport(1, 1, 1, "sunny", "aa");

        WeatherForecast weatherForecast = new WeatherForecast(weatherReport1);
        assertEquals(weatherForecast.getWeatherReport(), weatherReport1);
    }

    @Test
    public void setWeatherReportWorks() {
        WeatherReport weatherReport1 = new WeatherReport(1, 1, 1, "sunny", "aa");
        WeatherReport weatherReport2 = new WeatherReport(2, 1, 1, "sunny", "aa");

        WeatherForecast weatherForecast = new WeatherForecast(weatherReport1);
        weatherForecast.setReports(weatherReport2);

        assertEquals(weatherForecast.getWeatherReport(), weatherReport2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void WeatherReportWorksFailsCorrectly() {
        WeatherForecast weatherForecast = new WeatherForecast(null);

    }

}
