package com.palarran.cycloops;

/**
 * {@link CycloneData} class represents a single Cyclone object.
 * Each object has 4 properties: Category, Name, Time, and Url.
 */

public class CycloneData {

    // Category of the cyclone (e.g. -2, -1, 0, 1, 2, 3, 4, or 5)
    private int mCategory;

    // Cyclone name
    private String mName;

//    // Time and Date the cyclone happened
//    private long mTimeInMilliseconds;

    // Url to website with Cyclone data
    private String mUrl;

    /*
    * CONSTRUCTOR
    * Create a new CycloneData object.
    *
    * @param uCategory is the category of the cyclone (e.g. -2, -1, 0, 1, 2, 3, 4, or 5)
    * @param uName is the name of the Cyclone
    *
    * @param uUrl is the url of the website that is providing the data
    * */
    public CycloneData(int uCategory, String uName, String uUrl) {

        mCategory = uCategory;
        mName = uName;
//        mTimeInMilliseconds = uTime;
        mUrl = uUrl;
    }

    /**
     * Setting public getters for the private variables above for use of other classes
     */
    // Get the storm category
    public int getCategory() {
        return mCategory;
    }

    // Get the cyclones name
    public String getmName() {
        return mName;
    }

    // Get the time/date
//    public long getmTimeInMilliseconds() {
//        return mTimeInMilliseconds;
//    }

    // Get the Url
    public String getmUrl() {
        return mUrl;
    }
}
