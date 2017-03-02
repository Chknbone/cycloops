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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * {@link CycloneAdapter} is an {@link ArrayAdapter} that provides the layout for each list
 * based on a data source, which is a list of {@link CycloneData} objects.
 */

public class CycloneAdapter extends ArrayAdapter<CycloneData> {

    //If JSON place data contains the word "of", this will be used to help separate into two Strings.
    private static final String LOCATION_SEPARATOR = " of ";

    private static final String LOG_TAG = CycloneAdapter.class.getSimpleName();

    /**
     * This is a custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context        The current context. Used to inflate the layout file.
     * @param cyclones A List of Cyclone Data objects to display in a list
     */
    public CycloneAdapter(Activity context, ArrayList<CycloneData> cyclones) {
        // Initialize the ArrayAdapter's internal storage for the context and the list.
        // The second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for four TextViews, the adapter is not going to use
        // this second argument, so it can be any value. Setting it at 0.
        super(context, 0, cyclones);
    }

    //Helper method to return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    //Helper Method to return the formatted date string (i.e. "4:20 PM") from a Date object.
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    //Helper method to get the correct color depending on the severity of the earthquake
    private int getCategoryColor(double category) {
        int categoryColorResourceId;
        int categoryFloor = (int) Math.floor(category);
        switch (categoryFloor) {
            case 0:
            case 1:
                categoryColorResourceId = R.color.catagory1;
                break;
            case 2:
                categoryColorResourceId = R.color.catagory2;
                break;
            case 3:
                categoryColorResourceId = R.color.catagory3;
                break;
            case 4:
                categoryColorResourceId = R.color.catagory4;
                break;
            default:
                categoryColorResourceId = R.color.catagory5;
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

        //Get the {@link CycloneData} object located at this position in the list
        CycloneData currentCycloneData = getItem(position);

        //Find the TextView in the activity_main.xml layout with ID magnitude
        TextView categoryTextView = (TextView) listItemView.findViewById(R.id.storm_category);

        //Define how doubles are formatted (e.g. 2.3) Show only one decimal place
        DecimalFormat formattedCategory = new DecimalFormat("0.0");
        String output = formattedCategory.format(currentCycloneData.getCategory());

        // Get the category from the formatted data & set that text on the categoryTextView
        categoryTextView.setText(output);

        //Get the initial location data and split it into two separate strings
        String originalPlace = currentCycloneData.getmPlace();

        //Variables for the primary and offset TextViews that splitting the String will produce
        String primaryLocation;
        String locationOffset;

        //Split the JSON String. One for City and one for Offset
        if (originalPlace.contains(LOCATION_SEPARATOR)) {
            String[] parts = originalPlace.split(LOCATION_SEPARATOR);
            locationOffset = parts[0] + LOCATION_SEPARATOR;
            primaryLocation = parts[1];
        } else {
            locationOffset = getContext().getString(R.string.near_the);
            primaryLocation = originalPlace;
        }

        //Get the primary and offset locations from String split above & set text on the relevant Textviews
        TextView primaryLocationTextView = (TextView) listItemView.findViewById(R.id.location);
        primaryLocationTextView.setText(primaryLocation);
        TextView locationOffsetTextView = (TextView) listItemView.findViewById(R.id.distance);
        locationOffsetTextView.setText(locationOffset);

        // Create a new Date object from the time in milliseconds of the earthquake
        Date dateObject = new Date(currentCycloneData.getmTimeInMilliseconds());

        //Find the TextView of the from the earthquake_activity.xml layout w/ ID date
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date);

        // Format the date string (i.e. "Mar 3, 1984")
        String formattedDate = formatDate(dateObject);

        // Display the date of the current earthquake in that TextView
        dateTextView.setText(formattedDate);
        // Find the TextView with view ID time
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);

        // Format the time string (i.e. "4:30PM")
        String formattedTime = formatTime(dateObject);

        // Display the time of the current earthquake in that TextView
        timeView.setText(formattedTime);

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable cycloneShape = (GradientDrawable) categoryTextView.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int categoryColor = getCategoryColor(currentCycloneData.getCategory());

        // Set the color on the magnitude circle
        cycloneShape.setColor(categoryColor);

        //Return the whole list item layout (containing 4 TextViews) so that it can be shown in the Listview
        return listItemView;
    }
}
