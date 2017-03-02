package com.palarran.cycloops;

/**
 * {@link CycloneData} represents a single Cyclone object.
 * Each object has 3 properties: Magnitude, Place, Time.
 */

public class CycloneData {

    // Magnitude of the Earthquake (e.g. 1.1, 4.3, 5.6...)
    private double mCategory;

    // City name
    private String mName;

    // Time and Date the earthquake happened
    private long mTimeInMilliseconds;

    // Url to website with Cyclone data
    //Unused at this time.
    //private String mUrl;

    /*
    * CONSTRUCTOR
    * Create a new CycloneData object.
    *
    * @param uCat is the category of the cyclone (e.g. 1, 2, 3, 4, or 5)
    * @param uname is the name of the Cyclone
    * @param uTime is the date the Cyclone was reported
    * @param uUrl is the url of the website that is providing the data
    * */
    public CycloneData(double uCat, String uName, long uTime) {

        mCategory = uCat;
        mName = uName;
        mTimeInMilliseconds = uTime;
        //mUrl = uUrl;
    }

    /**
     * Setting public getters for the private variables above for use of other classes
     */
    // Get the storm category
    public double getCategory() {
        return mCategory;
    }

    // Get the cyclones name
    public String getmName() {
        return mName;
    }

    // Get the time/date
    public long getmTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    // Get the Url
//    public String getmUrl() {
//        return mUrl;
//    }
}
