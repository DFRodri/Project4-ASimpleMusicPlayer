package com.example.android.dasmusicplayer;

import android.content.ContentUris;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends AppCompatActivity {

    //ALL HAIL SAINT BUTTERKNIFE!
    //Later findViewById!
    //ButterKnife - http://jakewharton.github.io/butterknife/
    @BindView(R.id.playerList)
    ImageView goBack;

    @BindView(R.id.songPlaying)
    TextView songPlaying;

    @BindView(R.id.currentSongDuration)
    TextView currentSongDuration;

    @BindView(R.id.playAndPause)
    ImageView playAndPauseButton;

    @BindView(R.id.currentPlayList)
    ListView customPlayList;

    @BindDrawable(R.drawable.play_arrow)
    Drawable playButton;

    @BindDrawable(R.drawable.pause_two_lines)
    Drawable pauseButton;

    @BindView(R.id.previous)
    ImageView previous;

    @BindView(R.id.next)
    ImageView next;

    @BindView(R.id.shuffle)
    ImageView shuffle;

    @BindView(R.id.repeat)
    ImageView repeat;

    //Global Variables INCOMING!!

    //ArrayLists populated with the audio data
    //Why two? Because we work with references from one to another and it's easier to work with a smaller list
    ArrayList<Song> currentSongList = new ArrayList<>();
    ArrayList<Song> songList = new ArrayList<>();

    //Holds the name of the song to be displayed at the top
    String currentSongName;

    //just a lazy boolean that is passed from one activity to another
    //lazy way to check if it's empty or not
    //(yes, I know about <arrayList>.isEmpty()... but come, don't make me do that after implementing this :( )
    boolean isEmpty;

    //We need this to know when to swap the icon of our app from play to pause and vice-versa
    boolean playPauseButton = true;

    //We need it to play music
    MediaPlayer mp = new MediaPlayer();

    //A global way to know which music is selected
    int currentSongSelected;

    //If someone wants to play and audio non-stop, we need to know that
    //I know about setLooping() but it doesn't work properly in some older versions of Android
    //so this together with seekTo(0) is a good workaround
    boolean loop = false;

    //Noticed an issue with my loop (I think it was when size()-1 = 1 and position = 1.
    //The way I found, after using Log.i, to fix it was to use another boolean to force it to work as I wanted)
    boolean repeaterOn = false;

    //It's pretty much used like mp.isPlaying() but works where it doesn't work
    boolean hasStarted = false;

    //Variable used to store/update the time left of an audio
    String newFinalDuration;

    //Handles the time left of the song in another thread so we can process the rest without any issues
    private Handler currentDurationSong = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        ButterKnife.bind(this);

        //Grabs the data of our audio files (songs) and the check to see if the arrayList
        //so we can do small quick edits on the playlist in a few seconds
        songList = getIntent().getParcelableArrayListExtra("songsList");
        isEmpty = getIntent().getBooleanExtra("checkingList", isEmpty);

        //Populates an arrayList (currentSongList) with the info that matters from our songList ArrayList
        for (int i = 0; i < songList.size(); i++) {
            if (songList.get(i).getNewCheck()) {
                currentSongList.add(songList.get(i));
            }
        }

        //Takes care of the important data that the new arrayList has
        //- position of the song that is playing now
        //- name of the song to display
        for (int i = 0; i < currentSongList.size(); i++) {
            if (currentSongList.get(i).getNewPlaylistCheck()) {
                currentSongSelected = i;
                currentSongName = currentSongList.get(currentSongSelected).getNewSongName();
                songPlaying.setText(currentSongName);
            }
        }

        //sets and populates the adapter that displays the "refined" songs data
        final PlaylistAdapter currentPlayList = new PlaylistAdapter(this, currentSongList);
        customPlayList.setAdapter(currentPlayList);

        //Methods that play the song and take care of the button to display (play or pause) when the song starts
        playSongButton();
        playSong();

        //To update the time left of the song playing
        if (mp.isPlaying()){
            UpdateTimeDisplayed();
        }

        //When we go back to the MainActivity to do quick changes on our playlist, we need this little clickListener
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Stops everything to prevent issues
                stopSong();

                //Forces no song to be considered "Playing Now"
                //It's pretty much a way to prevent display errors
                for (int i = 0; i < songList.size(); i++) {
                    songList.get(i).setNewPlaylistCheck(false);
                }

                //The Intent to make the magic happen
                //It also passes the info needed to make the other activity continue the work made before
                Intent goBack = new Intent(PlayerActivity.this, MainActivity.class);
                goBack.putParcelableArrayListExtra("songsList", songList);
                goBack.putExtra("checkingList", isEmpty);
                startActivity(goBack);
            }
        });

        //We've a "Start Previous Song" button so we need this here to make it work
        //Changes to the previous song, if available. If not, it simply repeats the first song on the list
        //Notifies our adapter of the changes
        //Updates the name of the song
        //The play/pause button
        //Starts the audio file
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousSong();
                currentPlayList.notifyDataSetChanged();
                displaySongName();
                playSongButton();
                playSong();
            }
        });

        //Play/Pause button
        //Changes the icon, pauses and continues the song
        //No, there is no stop here. It doesn't seem... needed in this case.
        playAndPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSongButton();
                playSong();
            }
        });

        //We've a "Start Next Song" button so we need this here to make it work
        //Changes to the next song, if available. If not, it simply repeats the last song on the list
        //Notifies our adapter of the changes
        //Updates the name of the song
        //The play/pause button
        //Starts the audio file
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextSong();
                currentPlayList.notifyDataSetChanged();
                displaySongName();
                playSongButton();
                playSong();
            }
        });

        //ClickListener that shuffles the ListView to make the order random
        //Makes use of Collections to achieve that and a for to know where the current song playing is now
        //After this is notifies the adapter of the changes
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String songSelected = currentSongList.get(currentSongSelected).getNewSongName();
                Collections.shuffle(currentSongList);
                for (int i = 0; i < currentSongList.size(); i++) {
                    if (songSelected.equals(currentSongList.get(i).getNewSongName())) {
                        currentSongSelected = i;
                        break;
                    }
                }
                currentPlayList.notifyDataSetChanged();
            }
        });

        //If the user wants a song to repeat, this visually notifies them of that
        //It also changes the boolean that takes care of that since setLooping() bugs with
        //older versions of Android
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loop) {
                    repeat.setBackgroundResource(R.drawable.returning_curved_right_arrow_red);
                    loop = true;
                } else {
                    repeat.setBackgroundResource(R.drawable.returning_curved_right_arrow);
                    loop = false;
                }
            }
        });

        //When a song ends we need to tell our APP that we need to start the next song right away
        //Takes in consideration if the user have the loop song button on
        //and updates and notify everything needed to make this work
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!loop) {
                    if (currentSongList.size() - 1 != currentSongSelected) {
                        nextSong();
                    } else {
                        //when the last song of the list ends, we start from index zero one more time!
                        //this also prevents an issue that I encountered when index and size where equal
                        repeaterOn = true;
                        nextSong();
                    }
                    displaySongName();
                    playPauseButton = true;
                    playSongButton();
                    currentPlayList.notifyDataSetChanged();
                    playSong();
                } else {
                    playPauseButton = false;
                    hasStarted = false;
                    //setLooping(<state>) bugs in some devices and seekTo(0) works just fine
                    //yes, I'm aware that huge files may have a small delay
                    //NOTE: I tested with a 8 minutes song, no delay with it in my Wiko Sunny2 Plus
                    mp.seekTo(0);
                    playSong();
                }
            }
        });

        //Manual play is also a thing. Click an item from the ListView and see here all this magic happen
        //It works pretty much like everything made until now
        customPlayList.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View songCheck;
                songCheck = view.findViewById(R.id.activeSong);

                //Changes the visual indicator of the song previously playing and the current one
                if (songCheck.getSolidColor() != getResources().getColor(R.color.pressed_color) && currentSongList.size() != 1) {
                    songCheck.setBackgroundColor(songCheck.getResources().getColor(R.color.pressed_color));
                    currentSongList.get(position).setNewPlaylistCheck(true);

                    songCheck = view.findViewById(R.id.activeSong);
                    songCheck.setBackgroundColor(songCheck.getResources().getColor(R.color.colorPrimaryDark));
                    currentSongList.get(currentSongSelected).setNewPlaylistCheck(false);
                }

                //updates our global indicator of which song is now playing
                currentSongSelected = position;

                hasStarted = false;
                displaySongName();
                playPauseButton = true;
                playSongButton();
                playPauseButton = false;
                playSong();

                currentPlayList.notifyDataSetChanged();
            }
        });
    }

    //This is a must
    //When our APP "dies", we need to kill the MediaPlayer to release resources
    //so our stopSong() method takes care of that
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSong();
    }

    //Method that takes care of changing from the current song to the previous song in the ListView
    //Takes care of everything needed that isn't covered by any other method
    //It's aware that we can't have a song below the index 0
    public void previousSong() {
        currentSongList.get(currentSongSelected).setNewPlaylistCheck(false);
        hasStarted = false;
        currentSongSelected--;
        if (currentSongSelected < 0) {
            currentSongSelected = 0;
        }
        currentSongList.get(currentSongSelected).setNewPlaylistCheck(true);
        playPauseButton = true;
    }

    //Method that takes care of changing from the current song to the next song in the ListView
    //Takes care of everything needed that isn't covered by any other method
    //It's aware that we can't have a song at a position above the index size()-1
    public void nextSong() {
        currentSongList.get(currentSongSelected).setNewPlaylistCheck(false);
        hasStarted = false;
        currentSongSelected++;
        if (currentSongSelected > currentSongList.size() - 1) {
            currentSongSelected = currentSongList.size() - 1;
        }
        if (repeaterOn){
            currentSongSelected = 0;
        }
        currentSongList.get(currentSongSelected).setNewPlaylistCheck(true);
        playPauseButton = true;
        repeaterOn = false;
    }

    //Method that takes care of the display play/pause button
    //Also has something to say on the playSong() method since they work like a couple (depend of each other)
    public void playSongButton() {
        if (!playPauseButton) {
            playAndPauseButton.setBackground(pauseButton);
            playPauseButton = true;
        } else {
            playAndPauseButton.setBackground(playButton);
            playPauseButton = false;
        }
    }

    //Method that plays/pause/continue a song from where it paused
    public void playSong() {
        if (!playPauseButton) {
            if (!hasStarted) {
                try {
                    Uri uri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSongList.get(currentSongSelected).getNewID());
                    mp.reset();
                    mp.setDataSource(getApplicationContext(), uri);
                    mp.prepare();
                    mp.start();
                    hasStarted = true;
                } catch (Exception e) {
                    Log.e("Error: ", "Exception " + e);
                }
            } else {
                try {
                    mp.start();
                } catch (Exception e) {
                    Log.e("Error: ", "Exception " + e);
                }
            }
        } else {
            if (mp.isPlaying()) {
                try {
                    mp.pause();
                } catch (Exception e) {
                    Log.e("Error: ", "Exception " + e);
                }
            }
        }
    }

    //Method that handles everything needed when we need to kill the MediaPlayer to release resources
    public void stopSong() {
        try {
            mp.reset();
            mp.prepare();
            mp.stop();
            mp.release();
            mp = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Our magical loop based on a Runnable
    //(Let us run a method in another thread to let us do whatever we want in the main one, or something like that)
    //Fetches the total time of our audio file and then just updates it every second with its current position
    //by doing a quick calculation of TotalTime - CurrentTime. E.g.: 10000 - 2000 = 8000 miliseconds left
    //Ofc we are working in ms so it also converts it to an understandable value based on the format hh:mm:ss
    //NOTE: It hides the hh when isn't needed
    private void UpdateTimeDisplayed() {
        if(mp.isPlaying()) {
            Runnable run = new Runnable() {
                public void run() {
                    long duration = mp.getDuration();
                    long currentDuration = mp.getCurrentPosition();
                    int currentDisplayTimer = (int) (duration - currentDuration);

                    //converts the ms to hh:mm:ss and set it
                    timeConverter(currentDisplayTimer);
                    currentSongDuration.setText(newFinalDuration);
                    UpdateTimeDisplayed();
                }
            };
            currentDurationSong.postDelayed(run, 1000);
        }
    }

    //Method that converts ms to the hh:mm:ss format
    private void timeConverter(int currentDisplayTimer) {
        int seconds = ((currentDisplayTimer) / 1000) % 60;
        String realSeconds = Integer.toString(seconds);
        if (seconds < 10) {
            realSeconds = "0" + seconds;
        }
        int minutes = ((currentDisplayTimer) / (1000 * 60)) % 60;
        String realMinutes = Integer.toString(minutes);
        if (minutes < 10) {
            realMinutes = "0" + minutes;
        }
        int hours = ((currentDisplayTimer) / (1000 * 60 * 60)) % 24;
        String realHours = Integer.toString(hours);

        if (hours < 1) {
            newFinalDuration = realMinutes + ":" + realSeconds;
        } else {
            newFinalDuration = realHours + ":" + realMinutes + ":" + realSeconds;
        }
    }

    //Method to display the song name at the top of the APP
    public void displaySongName() {
        currentSongName = currentSongList.get(currentSongSelected).getNewSongName();
        songPlaying.setText(currentSongName);
    }
}
