package sdp.moneyrun.weather;


import android.os.StrictMode;
import android.util.Log;

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

import sdp.moneyrun.map.LocationRepresentation;


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

    public static OpenWeatherMap build() {
        String key = open_weather_key;
        return new OpenWeatherMap(key);
    }


    private WeatherReport parseReport(JSONObject report) throws JSONException {
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

    private WeatherForecast parseForecast(JSONObject forecast) throws JSONException {
        if (forecast == null) {
            throw new NullPointerException();
        }
        JSONArray daily = forecast.getJSONArray("daily");
        WeatherReport[] reports = new WeatherReport[Math.max(3, daily.length())];

        for (int i = 0; i < Math.max(3, daily.length()); ++i) {
            if (i >= daily.length())
                reports[i] = NO_DATA;
            else
                try {
                    reports[i] = parseReport(daily.getJSONObject(i));
                } catch (JSONException ex) {
                    Log.e("OpenWeatherMapWeather", "Error when parsing day " + i, ex);
                    reports[i] = NO_DATA;
                }
        }

        return new WeatherForecast(reports);
    }

    private String getRawForecast(LocationRepresentation location) throws IOException {
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

    public WeatherForecast getForecast(LocationRepresentation location) throws IOException {
        String forecast = getRawForecast(location);
        try {
            JSONObject json = (JSONObject) new JSONTokener(forecast).nextValue();
            return parseForecast(json);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }
}