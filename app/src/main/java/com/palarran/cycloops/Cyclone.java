package com.palarran.cycloops;

/**
 * {@link Cyclone} class represents a single Cyclone object.
 * Each object has 4 properties: Category, Name, Heading, WindSpeed, and Url.
 */

public class Cyclone {

    // Category of the cyclone (e.g. -2, -1, 0, 1, 2, 3, 4, or 5)
    private int category;

    // Latitude & Longitude of Cyclone
    private float latitude, longitude;

    // Cyclone name
    private String name;

    // Cyclone heading (N, S, E, or W)
    private String heading;

    // Wind speed in Cyclone
    private String windSpeedKnots;

    // Url to website with Cyclone data
    private String url;

    /**
    * CONSTRUCTOR
    * Create a new Cyclone object.
    *
    * @param constCategory is the category of the cyclone (e.g. -2, -1, 0, 1, 2, 3, 4, or 5)
    * @param constLatitude is the Latitude of the cyclone
    * @param constLongitude is the Longitude of the cyclone
    * @param constName is the name of the Cyclone
    * @param constHeading is the direction the cyclone is moving
    * @param constWindSpeedKnots is the speed of the wind the Cyclone is producing
    * @param constUrl is the url of the website that is providing the data
    */
    public Cyclone(int constCategory, float constLatitude, float constLongitude,String constName,
                   String constHeading, String constWindSpeedKnots, String constUrl) {

        category = constCategory;
        latitude = constLatitude;
        longitude = constLongitude;
        name = constName;
        heading = constHeading;
        windSpeedKnots = constWindSpeedKnots;
        url = constUrl;
    }

    /**
     * Setting public getters for the private variables above for use of other classes
     */
    // Get the storm category
    public int getCategory() {
        return category;
    }

    // Get the cyclones Latitude
    public float getLatitude() {
        return latitude;
    }

    //Get the cyclones Longitude
    public float getLongitude() {
        return longitude;
    }

    // Get the cyclones name
    public String getName() {
        return name;
    }

    // Get the cyclones heading
    public String getHeading() {
        return heading;
    }

    // Get the cyclones windspeed
    public String getWindSpeedKnots() {
        return windSpeedKnots;
    }

    // Get the Url
    public String getUrl() {
        return url;
    }
}
