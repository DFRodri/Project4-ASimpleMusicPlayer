package com.example.android.dasmusicplayer;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

//Class of the adapter to be used in our MainActivity
//Makes use of ViewHolder to make things faster since the main point of this project was to recycle data
public class CreditsAdapter extends ArrayAdapter<Credits> {

    //Needs to be public to be accessed by MainActivity.java
    public CreditsAdapter(Activity context, ArrayList<Credits> creditsList) {
        super(context, 0, creditsList);
    }
    //The class that holds the views that will be recycled
    private static class ViewHolder {
        protected TextView creatorName, creatorURL;
    }

    //The getView method to display (return) each row of what we want as we want with a certain layout as a base
    //Makes use of a ViewHolder class created previously to recycle data even further
    //R.id.<layout name> -> the layout
    //getMethods -> what we want to be displayed in each recycled view
    //the views loaded with the holder -> what we want to populate with data
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.credits_list, parent, false);

            holder = new ViewHolder();

            holder.creatorName = row.findViewById(R.id.resourceName);
            holder.creatorURL = row.findViewById(R.id.resourceURL);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        //each element (Item) of our list has data stored in different positions
        //this makes sure that we're accessing the right one
        final Credits currentCredits = getItem(position);

        //grabs the info from the Credits Object stored
        String newName = currentCredits.getNewCreditName();
        String newURL = currentCredits.getNewCreditURL();

        //sets the info fetched from the get methods so we can have something displayed
        holder.creatorName.setText(newName);
        holder.creatorURL.setText(newURL);

        return row;

    }
}
