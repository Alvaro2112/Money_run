package sdp.moneyrun.weather;

public final class WeatherReport {
    private double averageTemperature;
    private double minimumTemperature;
    private double maximalTemperature;

    private String weatherType;
    private String weatherIcon;

    public WeatherReport(double averageTemperature, double minimumTemperature, double maximalTemperature, String weatherType, String weatherIcon) {
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

    public String getWeatherType() {
        return weatherType;
    }

    public void setWeatherType(String weatherType) {
        if (weatherType == null) {
            throw new NullPointerException();
        }

        this.weatherType = weatherType;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        if (weatherIcon == null) {
            throw new NullPointerException();
        }
        this.weatherIcon = weatherIcon;
    }
}