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

import static com.palarran.cycloops.MainActivity.LOG_TAG;

/**
 * Helper methods related to requesting and receiving Cyclone data from USGS.
 */
public final class Utils {

    /**
     * Create a private constructor because no one should ever create a {@link Utils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name Utils (and an object instance of Utils is not needed).
     */
    private Utils() {
    }

    /**
     * Return a list of {@link CycloneData} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<CycloneData> extractFeatureFromJson(String cycloneJSON) {

        // Create an empty ArrayList that we can start adding Cyclones to
        ArrayList<CycloneData> cyclones = new ArrayList<>();

        // Try to parse the cycloneJSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject jsonObjRoot = new JSONObject(cycloneJSON);

            // Extract the JSONArray associated with the key called "currenthurricane",
            // which represents a list of cyclones.
            JSONArray currentHurricaneArray = jsonObjRoot.getJSONArray("currenthurricane");

            //Loop through each feature in the currentHurricaneArray array & create an
            //{@link CycloneData} object for each one
            for (int i = 0; i < currentHurricaneArray.length(); i++) {
                //Get cyclone JSONObject at position i
                JSONObject currentCyclone = currentHurricaneArray.getJSONObject(i);
                //Get “properties” JSONObject
                JSONObject cycloneProperties = currentCyclone.getJSONObject("currenthurricane");
                //Extract “Category” for cyclone category
                double category = cycloneProperties.getDouble("Category");
                //Extract “stormName” for Hurricane's name
                String name = cycloneProperties.getString("stormName");
                //Extract “epoch” for time
                long time = cycloneProperties.getLong("epoch");
                // Extract the value for the key called "url"
                //String url = cycloneProperties.getString("url");
                //Create CycloneData java object from magnitude, location, time, and url
                CycloneData cyclone = new CycloneData(category, name, time);
                //Add new cyclone to list
                cyclones.add(cyclone);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("Utils", "Problem parsing the cyclone JSON results", e);
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
     * Query the USGS data set and return a list of {@link CycloneData} objects.
     */
    public static List<CycloneData> fetchCycloneData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link CycloneData}s
        List<CycloneData> cyclones = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link CycloneData}s
        return cyclones;
    }
}