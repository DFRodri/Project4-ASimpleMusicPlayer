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
public class PlaylistAdapter extends ArrayAdapter<Song> {

    //Needs to be public to be accessed by MainActivity.java
    public PlaylistAdapter(Activity context, ArrayList<Song> songList) {
        super(context, 0, songList);
    }
    //The class that holds the views that will be recycled
    private static class ViewHolder {
        protected TextView song, group, duration;
        protected View checked;
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
            row = inflater.inflate(R.layout.song_list, parent, false);

            holder = new ViewHolder();

            holder.song = row.findViewById(R.id.songName);
            holder.group = row.findViewById(R.id.groupName);
            holder.duration = row.findViewById(R.id.songDuration);
            holder.checked = row.findViewById(R.id.activeSong);

            row.setTag(holder);
        } else{
            holder = (ViewHolder) row.getTag();
        }

        //each element (Item) of our list has data stored in different positions
        //this makes sure that we're accessing the right one
        Song currentSong = getItem(position);

        //grabs the info from the Song Object stored
        String newSong = currentSong.getNewSongName();
        String newGroup = currentSong.getNewGroupName();
        long newDuration = currentSong.getNewSongDuration();
        boolean newCheck = currentSong.getNewPlaylistCheck();

        //The following wall of code takes care of converting the ms fetched from the getNewSongDuration to hh:mm:ss
        //We want to see something that we can understand without much effort, right?
        int seconds = (((int) newDuration) / 1000) % 60;
        String realSeconds = Integer.toString(seconds);
        if (seconds < 10) {
            realSeconds = "0" + seconds;
        }
        int minutes = (((int) newDuration) / (1000 * 60)) % 60;
        String realMinutes = Integer.toString(minutes);
        if (minutes < 10) {
            realMinutes = "0" + minutes;
        }
        int hours = (((int) newDuration) / (1000 * 60 * 60)) % 24;
        String realHours = Integer.toString(hours);

        String newFinalDuration;
        if (hours < 1) {
            newFinalDuration = realMinutes + ":" + realSeconds;
        } else {
            newFinalDuration = realHours + ":" + realMinutes + ":" + realSeconds;
        }

        //sets the info fetched from the get methods so we can have something displayed
        holder.song.setText(newSong);
        holder.group.setText(newGroup);
        holder.duration.setText(newFinalDuration);

        //You may wonder why is this here inside an if but there is a reason for that and isn't simply
        //re-use of code from the SongsAdapter.java
        //When we select a song to play (do not confuse with selecting one to add to the playlist),
        //it's displayed in the PlayerActivity, in that "fancy playlist".
        //Because the user needs to know which song was selected, this makes the song selected "active"
        if (!newCheck) {
            holder.checked.setBackgroundColor(holder.checked.getResources().getColor(R.color.default_color));
        } else {
            holder.checked.setBackgroundColor(holder.checked.getResources().getColor(R.color.pressed_color));
        }

        return row;
    }
}
