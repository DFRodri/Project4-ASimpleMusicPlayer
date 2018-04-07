package com.example.android.dasmusicplayer;

import android.os.Parcel;
import android.os.Parcelable;

//Custom Class to create the custom Object Song that holds a few elements
//of any song we load into our app:
//ID, Song Name, Group Name, Duration, Check song to add to our Playlist, Play Now!
//I'm not sure on this description but I believe that this is it?
//It implements Parcelable because we need it to pass our ArrayList of custom Objects between activities
public class Song implements Parcelable{

    private long newID;
    private String newSongName;
    private String newGroupName;
    private long newSongDuration;
    private boolean newCheck;
    private boolean newPlaylistCheck;

    //method to create the custom Object
    public Song(long id, String songName, String groupName, long songDuration, boolean songCheck, boolean playlistCheck) {
        newID = id;
        newSongName = songName;
        newGroupName = groupName;
        newSongDuration = songDuration;
        newCheck = songCheck;
        newPlaylistCheck = playlistCheck;
    }

    //methods related to Parcelable
    //Do we really need to know how to create this without alt+enter->Add Parcelable Implementation?
    //Holy coconuts!
    protected Song(Parcel in) {
        newID = in.readLong();
        newSongName = in.readString();
        newGroupName = in.readString();
        newSongDuration = in.readLong();
        newCheck = in.readByte() != 0;
        newPlaylistCheck = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(newID);
        dest.writeString(newSongName);
        dest.writeString(newGroupName);
        dest.writeLong(newSongDuration);
        dest.writeByte((byte) (newCheck ? 1 : 0));
        dest.writeByte((byte) (newPlaylistCheck ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    //get and set methods to retrieve and set their values
    public long getNewID() {
        return newID;
    }

    public String getNewSongName() {
        return newSongName;
    }

    public String getNewGroupName() {
        return newGroupName;
    }

    public long getNewSongDuration() {
        return newSongDuration;
    }

    public boolean getNewCheck() {
        return newCheck;
    }

    public boolean setNewCheck(boolean songCheck) {
        return this.newCheck = songCheck;
    }

    public boolean getNewPlaylistCheck() {
        return newPlaylistCheck;
    }

    public boolean setNewPlaylistCheck(boolean playlistCheck) {
        return this.newPlaylistCheck = playlistCheck;
    }
}
