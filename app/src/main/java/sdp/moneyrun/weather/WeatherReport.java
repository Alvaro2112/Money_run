package sdp.moneyrun.weather;

import androidx.annotation.Nullable;

public final class WeatherReport {
    private double averageTemperature;
    private double minimumTemperature;
    private double maximalTemperature;

    @Nullable
    private String weatherType;
    @Nullable
    private String weatherIcon;

    public WeatherReport(double averageTemperature, double minimumTemperature, double maximalTemperature, @Nullable String weatherType, @Nullable String weatherIcon) {
        if (weatherIcon == null || weatherType == null) {
            throw new NullPointerException();
        }
        this.averageTemperature = averageTemperature;
        this.minimumTemperature = minimumTemperature;
        this.maximalTemperature = maximalTemperature;
        this.weatherType = weatherType;
        this.weatherIcon = weatherIcon;
    }

    public double getAverageTemperature() {
        return averageTemperature;
    }

    public void setAverageTemperature(double averageTemperature) {
        this.averageTemperature = averageTemperature;
    }

    public double getMinimumTemperature() {
        return minimumTemperature;
    }

    public void setMinimumTemperature(double minimumTemperature) {
        this.minimumTemperature = minimumTemperature;
    }

    public double getMaximalTemperature() {
        return maximalTemperature;
    }

    public void setMaximalTemperature(double maximalTemperature) {
        this.maximalTemperature = maximalTemperature;
    }

    @Nullable
    public String getWeatherType() {
        return weatherType;
    }

    public void setWeatherType(@Nullable String weatherType) {
        if (weatherType == null) {
            throw new NullPointerException();
        }

        this.weatherType = weatherType;
    }

    @Nullable
    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(@Nullable String weatherIcon) {
        if (weatherIcon == null) {
            throw new NullPointerException();
        }
        this.weatherIcon = weatherIcon;
    }
}