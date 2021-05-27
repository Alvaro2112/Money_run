package sdp.moneyrun.weather;


import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import sdp.moneyrun.location.LocationRepresentation;


public class OpenWeatherMap {
    public static final String open_weather_key = "052c4316c52f5d7f619a05a0a09a7636";
    private static final String API_ENDPOINT = "https://api.openweathermap.org/data/2.5/onecall";
    private static final String TEMP_UNIT = "metric";
    private static final WeatherReport NO_DATA = new WeatherReport(0, 0, 0, "N/A", "N/A");
    private final String apiKey;

    OpenWeatherMap(String apiKey) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());

        this.apiKey = apiKey;
    }

    @NonNull
    public static OpenWeatherMap build() {
        return new OpenWeatherMap(open_weather_key);
    }

    /**
     * This function will get a report as a JSONObject and will parse it to convert it into a usable WeatherReport Object
     * @param report The report to be converted
     * @return The converted report
     */
    @NonNull
    private WeatherReport parseReport(@Nullable JSONObject report) throws JSONException {
        if (report == null) {
            throw new NullPointerException();
        }

        JSONObject weather = report.getJSONArray("weather").getJSONObject(0);

        return new WeatherReport(
                report.getJSONObject("temp").getDouble("day"),
                report.getJSONObject("temp").getDouble("min"),
                report.getJSONObject("temp").getDouble("max"),

                weather.getString("main"),
                weather.getString("icon")
        );
    }

    /**
     * This function will get a forecast as a JSONObject and will parse it to convert it into a usable WeatherForecast Object
     * @param forecast The weatherForecast to be converted
     * @return The converted weatherForecast
     */
    @NonNull
    private WeatherForecast parseForecast(@Nullable JSONObject forecast) throws JSONException {
        if (forecast == null)
            throw new NullPointerException();

        JSONArray daily = forecast.getJSONArray("daily");
        WeatherReport report;

        if (0 == daily.length())
            report = NO_DATA;
        else
            report = tryToParseReport(daily.getJSONObject(0));

        return new WeatherForecast(report);
    }

    /**
     * Basically the parseForecast function surrounded by a try catch in case an Exception occurs
     * @param jsonObject The Report to be converted
     * @return
     */
    private WeatherReport tryToParseReport(JSONObject jsonObject) {

        WeatherReport report;

        try {
            report = parseReport(jsonObject);
        } catch (JSONException ex) {
            Log.e("OpenWeatherMapWeather", "Error when parsing day 0", ex);
            report = NO_DATA;
        }

        return report;
    }

    /**
     * Gets the forecast as a string for a given location from the OpenWeather website
     * @param location The location for which we want the weather forecast
     * @return The weather forecast as a String
     */
    @Nullable
    private String getRawForecast(@NonNull LocationRepresentation location) throws IOException {
        String queryUrl = API_ENDPOINT +
                "?lat=" + location.getLatitude() +
                "&lon=" + location.getLongitude() +
                "&units=" + TEMP_UNIT +
                "&exclude=current,minutely,hourly" +
                "&appid=" + apiKey;

        URL url = new URL(queryUrl);

        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");

            connection.setDoInput(true);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            stream = connection.getInputStream();
            if (stream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                result = reader.lines().collect(Collectors.joining("\n"));
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;

    }

    /**
     * Returns the weatherForecast for a given location
     * @param location the location for which we want the forecast
     * @return the forecast
     */
    @NonNull
    public WeatherForecast getForecast(@NonNull LocationRepresentation location) throws IOException {
        String forecast = getRawForecast(location);
        try {
            JSONObject json = (JSONObject) new JSONTokener(forecast).nextValue();
            return parseForecast(json);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }
}