package com.example.android.dasmusicplayer;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class SongsAdapter extends ArrayAdapter<Song> {

    //Needs to be public to be accessed by MainActivity.java
    public SongsAdapter(Activity context, ArrayList<Song> songList) {
        super(context, 0, songList);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View songsList = convertView;
        final Song currentSong = getItem(position);

        final String newSong = currentSong.getNewSongName();
        final String newGroup = currentSong.getNewGroupName();
        final String newDuration = currentSong.getNewSongDuration();
        final boolean newCheck = currentSong.getNewCheck();


        if (songsList == null) {
            LayoutInflater songsList_ = LayoutInflater.from(getContext());
            songsList = songsList_.inflate(R.layout.song_list, parent, false);
        }

        TextView song = songsList.findViewById(R.id.songName);
        song.setText(newSong);

        TextView group = songsList.findViewById(R.id.groupName);
        group.setText(newGroup);

        TextView duration = songsList.findViewById(R.id.songDuration);
        duration.setText(newDuration);

        View checked = songsList.findViewById(R.id.activeSong);
        if (!newCheck) {
            checked.setBackgroundColor(checked.getResources().getColor(R.color.default_color));
        } else {
            checked.setBackgroundColor(checked.getResources().getColor(R.color.pressed_color));
        }

        return songsList;
    }
}
