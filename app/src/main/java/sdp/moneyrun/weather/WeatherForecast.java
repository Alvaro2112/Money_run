package sdp.moneyrun.weather;

import androidx.annotation.NonNull;

public final class WeatherForecast {
    private WeatherReport report;

    public WeatherForecast(@NonNull WeatherReport report) {
        if (report == null) {
            throw new IllegalArgumentException("Report must not be null");
        }

        this.report = report;
    }

    public void setReports(WeatherReport report) {
        this.report = report;
    }

    public WeatherReport getWeatherReport() {
        return this.report;
    }

}