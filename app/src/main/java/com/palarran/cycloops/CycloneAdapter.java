package com.palarran.cycloops;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * {@link CycloneAdapter} is an {@link ArrayAdapter} that provides the layout for each list
 * based on a data source, which is a list of {@link Cyclone} objects.
 */

public class CycloneAdapter extends ArrayAdapter<Cyclone> {

    private static final String LOG_TAG = CycloneAdapter.class.getSimpleName();

    /**
     * This is a custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context  The current context. Used to inflate the layout file.
     * @param cyclones A List of Cyclone Data objects to display in a list
     */
    public CycloneAdapter(Activity context, ArrayList<Cyclone> cyclones) {
        // Initialize the ArrayAdapter's internal storage for the context and the list.
        // The second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for four TextViews, the adapter is not going to use
        // this second argument, so it can be any value. Setting it at 0.
        super(context, 0, cyclones);
    }

    //Helper method to display the full text of the cardinal direction cyclone is moving. Data comes
    //from the JSON response as just N, S, E, or W.
    private String getCardinalDirection(String compassHeading) {
        String direction;
        switch (compassHeading) {
            case "N":
               direction = " Moving North";
                break;
            case "S":
                direction = "Moving South";
                break;
            case "E":
                direction = "Moving East";
                break;
            default:
                direction = "Moving West";
                break;
        }
        return direction;
    }

    //Helper method to get the correct color depending on the category of the cyclone
    private int getCategoryColor(int category) {
        int categoryColorResourceId;
        //The Math.floor() function returns the largest integer less than or equal to a given number
        int categoryFloor = (int) Math.floor(category);
        switch (categoryFloor) {
            case -2:
                categoryColorResourceId = R.color.category_neg2;
                break;
            case -1:
                categoryColorResourceId = R.color.category_neg1;
                break;
            case 0:
                categoryColorResourceId = R.color.category0;
                break;
            case 1:
                categoryColorResourceId = R.color.category1;
                break;
            case 2:
                categoryColorResourceId = R.color.category2;
                break;
            case 3:
                categoryColorResourceId = R.color.category3;
                break;
            case 4:
                categoryColorResourceId = R.color.category4;
                break;
            case 5:
                categoryColorResourceId = R.color.category5;
                break;
            default:
                categoryColorResourceId = R.color.category_weak_storm;
                break;
        }
        return ContextCompat.getColor(getContext(), categoryColorResourceId);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the list of data that should be displayed in the list item view.
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main, parent, false);
        }

        //Get the {@link Cyclone} object located at this position in the list
        Cyclone currentCyclone = getItem(position);

        //Find the TextView in the activity_main.xml layout with ID storm_category
        TextView categoryTextView = (TextView) listItemView.findViewById(R.id.storm_category);
        // Get the category & set that text on the categoryTextView
        int categoryOutput = currentCyclone.getCategory();
        //int needs to be converted to a String before setText can happen properly
        categoryTextView.setText(Integer.toString(categoryOutput));

        //Get the Cyclone name data
        String cycloneName = currentCyclone.getName();
        //Get the Cyclone name & set text on the relevant Textview
        TextView cycloneNameTextView = (TextView) listItemView.findViewById(R.id.storm_name);
        cycloneNameTextView.setText(cycloneName);

        //Get the Cyclone Heading and direction data from JSON
        String JSONHeading = currentCyclone.getHeading();
        //Run it through the Switch statement above
        String cardinalDirection = getCardinalDirection(JSONHeading);
        String cycloneHeading = cardinalDirection;
        //Get the Cyclone heading & set text on the relevant Textview
        TextView cycloneHeadingTextView = (TextView) listItemView.findViewById(R.id.direction);
        cycloneHeadingTextView.setText(cycloneHeading);

        //Get the wind speed the Cyclone is producing & set that text on the windSpeedTextView
        String windSpeedKnots = currentCyclone.getWindSpeedKnots();
        //Find the TextView in the activity_main.xml layout with ID storm_category
        TextView windSpeedTextView = (TextView) listItemView.findViewById(R.id.wind_speed);
        windSpeedTextView.setText(windSpeedKnots);

        //Get the current latitude & longitude of the active cyclone
        float cycloneLatitude = currentCyclone.getLatitude();
        float cycloneLongitude = currentCyclone.getLongitude();
        //Convert float values to Stings and concatenate to one value
        String strLat = String.valueOf(cycloneLatitude);
        String strLon = String.valueOf(cycloneLongitude);
        String cycloneLatLon = strLat + " " + strLon;
        //strLat.concat(strLon);

        //Find the TextView in the activity_main.xml layout with ID latlon
        TextView LatLonTextView = (TextView) listItemView.findViewById(R.id.lat_lon);
        LatLonTextView.setText(cycloneLatLon);

        // Set the proper background color on the cyclone shaped icon.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable cycloneShape = (GradientDrawable) categoryTextView.getBackground();

        // Get the appropriate background color based on the current cyclone category
        int categoryColor = getCategoryColor(currentCyclone.getCategory());

        // Set the color on the cyclone shape icon
        cycloneShape.setColor(categoryColor);

        //Return the whole list item layout (containing 5 TextViews) so that it can be shown in the Listview
        return listItemView;
    }
}
