package sdp.moneyrun.weather;

import androidx.annotation.NonNull;

public final class WeatherForecast {
    private WeatherReport[] reports;

    public WeatherForecast(@NonNull WeatherReport[] reports) {
        if (reports.length < 3) {
            throw new IllegalArgumentException("reports array must contain at least 3 elements.");
        }

        this.reports = reports;
    }

    public void setReports(WeatherReport[] reports) {
        this.reports = reports;
    }

    public WeatherReport getWeatherReport(@NonNull Day offset) {
        return this.reports[offset.ordinal()];
    }


    public enum Day {
        TODAY, TOMORROW, AFTER_TOMORROW
    }
}