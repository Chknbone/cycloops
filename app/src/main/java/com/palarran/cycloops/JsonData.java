package com.palarran.cycloops;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving Cyclone data from USGS.
 */
public final class JsonData {

    private static final String LOG_TAG = JsonData.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link JsonData} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name JsonData (and an object instance of JsonData is not needed).
     */
    private JsonData() {
    }

    /**
     * Return a list of {@link Cyclone} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Cyclone> extractFeatureFromJson(String cycloneJSON) {

        // Create an empty ArrayList to start adding Cyclones to
        ArrayList<Cyclone> cyclones = new ArrayList<>();

        // Try to parse the cycloneJSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception , and print the error message to the logs.

        try {

            JSONObject rootJsonObject = new JSONObject(cycloneJSON);

            // Create JSONArray associated with the key called "currenthurricane", which represents
            // a list of cyclones from JSON response.
            JSONArray currentHurricaneArray = rootJsonObject.getJSONArray("currenthurricane");

            //Loop through each section in the currentHurricaneArray array & create an
            //{@link Cyclone} object for each one
            for (int i = 0; i < currentHurricaneArray.length(); i++) {
                //Get cyclone JSONObject at position i in the array
                JSONObject cycloneProperties = currentHurricaneArray.getJSONObject(i);

                // Extract "stormInfo" object
                JSONObject stormInfo = cycloneProperties.optJSONObject("stormInfo");
                //Extract “stormName_Nice” & "requesturl" for Cyclone's name and url
                String name = stormInfo.optString("stormName_Nice");
                String url = stormInfo.optString("wuiurl");

                // Extract "Current" object
                JSONObject current = cycloneProperties.optJSONObject("Current");
                // Extract "SaffirSimpsonCategory" key
                int category = current.optInt("SaffirSimpsonCategory");

                //Extract "WindSpeed" object
                JSONObject windSpeed = current.optJSONObject("WindSpeed");
                //Extract wind speed in "Knots" Key
                int Kts = windSpeed.optInt("Kts");
                String knots = Kts + " Knots";

                //Extract "Movement" object
                JSONObject movement = current.optJSONObject("Movement");
                //Extract movement direction in "Text" key
                String direction = movement.optString("Text");

                Cyclone cyclone = new Cyclone(category, name, direction, knots, url);
                //Add new cyclone to list
                cyclones.add(cyclone);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("JsonData", "Problem parsing the cyclone JSON results", e);
        }

        // Return the list of cyclones
        return cyclones;
    }
    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the cyclone JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Query the USGS data set and return a list of {@link Cyclone} objects.
     */
    public static List<Cyclone> fetchCycloneData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Cyclone}s
        List<Cyclone> cyclones = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Cyclone}s
        return cyclones;
    }
}