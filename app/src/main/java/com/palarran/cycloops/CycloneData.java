package com.palarran.cycloops;

/**
 * {@link CycloneData} represents a single Earthquake object.
 * Each object has 4 properties: Magnitude, Place, Time, and Url.
 */

public class CycloneData {

    // Magnitude of the Earthquake (e.g. 1.1, 4.3, 5.6...)
    private double mMagnitude;

    // City name
    private String mPlace;

    // Time and Date the earthquake happened
    private long mTimeInMilliseconds;

    // Url to USGS website with Earthquake data
    private String mUrl;

    /*
    * CONSTRUCTOR
    * Create a new EarthquakeData object.
    *
    * @param qMag is magnitude of the earthquake (e.g. 5.5)
    * @param qPlace is the name of the nearest city the earthquake happened
    * @param qTime is the date the earthquake happened
    * @param qUrl is the url of the USGS site that is providing the data
    * */
    public CycloneData(double qMag, String qPlace, long qTime, String qUrl) {

        mMagnitude = qMag;
        mPlace = qPlace;
        mTimeInMilliseconds = qTime;
        mUrl = qUrl;
    }

    /**
     * Setting public getters for the private variables above for use of other classes
     */
    // Get the Magnitude
    public double getmMagnitude() {
        return mMagnitude;
    }

    // Get the City name
    public String getmPlace() {
        return mPlace;
    }

    // Get the date
    public long getmTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    // Get the Url
    public String getmUrl() {
        return mUrl;
    }
}
