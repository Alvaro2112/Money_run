package sdp.moneyrun.weather;

import androidx.annotation.NonNull;

public final class WeatherForecast {
    public void setReports(WeatherReport[] reports) {
        this.reports = reports;
    }

    private  WeatherReport[] reports;


    WeatherForecast(WeatherReport[] reports) {
        if (reports.length < 3) {
            throw new IllegalArgumentException("reports array must contain at least 3 elements.");
        }

        this.reports = reports;
    }

    public enum Day {
        TODAY, TOMORROW, AFTER_TOMORROW;
    }


    public WeatherReport getWeatherReport(@NonNull Day offset) {
        return this.reports[offset.ordinal()];
    }
}