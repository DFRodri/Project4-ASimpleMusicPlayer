package com.example.android.dasmusicplayer;

//Everything in this class needs to be public to be accessed by MainActivity.java
public class Song {

    private String newSongName;
    private String newGroupName;
    private String newSongDuration;
    private boolean newCheck;

    public Song(String songName, String groupName, String songDuration, boolean songCheck) {
        newSongName = songName;
        newGroupName = groupName;
        newSongDuration = songDuration;
        newCheck = songCheck;
    }

    public String getNewSongName() {
        return newSongName;
    }

    public String getNewGroupName() {
        return newGroupName;
    }

    public String getNewSongDuration() {
        return newSongDuration;
    }

    public boolean getNewCheck() {
        return newCheck;
    }

    public boolean setNewCheck(boolean songCheck) {
        return this.newCheck = songCheck;
    }
}
