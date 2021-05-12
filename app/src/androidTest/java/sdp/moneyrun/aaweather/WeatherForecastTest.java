package sdp.moneyrun.aaweather;

import org.junit.Test;


import sdp.moneyrun.weather.WeatherForecast;
import sdp.moneyrun.weather.WeatherReport;

import static org.junit.Assert.assertEquals;

public class WeatherForecastTest {

    @Test
    public void getWeatherReportWorks() {
        WeatherReport weatherReport1 = new WeatherReport(1, 1, 1, "sunny", "aa");
        WeatherReport weatherReport2 = new WeatherReport(2, 1, 1, "sunny", "aa");
        WeatherReport weatherReport3 = new WeatherReport(3, 1, 1, "sunny", "aa");

        WeatherReport[] reports = {weatherReport1, weatherReport2, weatherReport3};

        WeatherForecast weatherForecast = new WeatherForecast(reports);
        assertEquals(weatherForecast.getWeatherReport(WeatherForecast.Day.TODAY), weatherReport1);
    }

    @Test
    public void setWeatherReportWorks() {
        WeatherReport weatherReport1 = new WeatherReport(1, 1, 1, "sunny", "aa");
        WeatherReport weatherReport2 = new WeatherReport(2, 1, 1, "sunny", "aa");
        WeatherReport weatherReport3 = new WeatherReport(3, 1, 1, "sunny", "aa");
        WeatherReport weatherReport4 = new WeatherReport(4, 1, 1, "sunny", "aa");

        WeatherReport[] reports1 = {weatherReport1, weatherReport2, weatherReport3};
        WeatherReport[] reports2 = {weatherReport1, weatherReport2, weatherReport4};

        WeatherForecast weatherForecast = new WeatherForecast(reports1);
        weatherForecast.setReports(reports2);

        assertEquals(weatherForecast.getWeatherReport(WeatherForecast.Day.AFTER_TOMORROW), weatherReport4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void WeatherReportWorksFailsCorrectly() {
        WeatherReport weatherReport1 = new WeatherReport(1, 1, 1, "sunny", "aa");
        WeatherReport weatherReport2 = new WeatherReport(2, 1, 1, "sunny", "aa");

        WeatherReport[] reports1 = {weatherReport1, weatherReport2};

        WeatherForecast weatherForecast = new WeatherForecast(reports1);

    }

}
