package com.example.android.dasmusicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    //ALL HAIL SAINT BUTTERKNIFE!
    //Later findViewById!
    //ButterKnife - http://jakewharton.github.io/butterknife/
    @BindView(R.id.refresh)
    ImageView refresh;

    @BindView(R.id.credits)
    ImageView credits;

    @BindView(R.id.creditsBG)
    ImageView creditsBG;

    @BindView(R.id.creatorName)
    TextView creatorName;

    @BindView(R.id.resourcesUsed)
    TextView resourcesUsed;

    @BindView(R.id.creditsMenuSeparator)
    View creditsMenuSeparator;

    @BindView(R.id.creditsList)
    ListView listResourcesUsed;

    @BindView(R.id.creatorIndicator)
    View creatorID;

    @BindView(R.id.resourcesIndicator)
    View resourcesID;

    @BindView(R.id.songList)
    ListView listOfSongs;

    @BindView(R.id.separatorNoticeMenu)
    View separatorNoticeMenu;

    @BindView(R.id.noticeMenu)
    RelativeLayout noticeMenu;

    @BindView(R.id.refreshWarning)
    TextView refreshWarning;

    //Global Variables INCOMING!!

    //No authorization to scan you SD or no song in it? Then nothing to be displayed!
    //Sucks to be you! (this is the object loaded when that happens)
    Song Empty = new Song(0, "No Music Found", "--", 0, false, false);

    //ArrayLists populated with the audio data and resources used
    ArrayList<Song> songList = new ArrayList<>();
    ArrayList<Credits> creditsList = new ArrayList<>();

    //booleans that help all this sea of code work properly
    boolean creditsButton = false;
    boolean isEmpty = false;

    //Having two hands is nice, but having one handler to make certain events happen in the future
    //even better
    private Handler noticeGone = new Handler();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        //Check to know if we come or not with a playlist already created
        //We're recycling views so why not go further with it?
        isEmpty = getIntent().getBooleanExtra("checkingList", isEmpty);

        //Because I'm a guy that always like to do more, I decided to learn how to work with
        //real songs from a SD card *inserts happy face with the results achieved*

        //If you give permission, it calls a method to scan your SD card or displays that no music was found
        if (isStoragePermissionGranted()) {
            isEmpty();
        } else {
            songList.add(Empty);
        }

        //Method to load the info of the resources used as to give credit to the creator - me!
        creditsListFound();

        //Sets and populate the adapter that displays the songs data and continues the work previously started
        //And because this guy always wants more of what he does, he wasted some days to find
        //a way to make a "button" (ImageView of course!) inside each item of the ListView clickable
        final SongsAdapter customPlayList = new SongsAdapter(this, songList) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View musicRow = super.getView(position, convertView, parent);

                View selectSongToPlay = musicRow.findViewById(R.id.listSong);
                selectSongToPlay.setTag(position);
                selectSongToPlay.setOnClickListener(MainActivity.this);

                return musicRow;
            }
        };
        listOfSongs.setAdapter(customPlayList);
        listOfSongs.setOnItemClickListener(this);

        //set and populate the adapter that displays the credits data
        CreditsAdapter customCredits = new CreditsAdapter(this, creditsList);
        listResourcesUsed.setAdapter(customCredits);

        //When you click a link in the credits menu, you open a link in a browser
        //this guarantees that it actually happens
        listResourcesUsed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openURL(position);
            }
        });

        //when you want to rescan your SD card, this makes sure that it happens properly
        //it also includes a timed warning to show that it happened (makes use of an handler!)
        //it can be used as a way to clean (reset) your current playlist instead of scrolling
        //for every single song that you've added
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEmpty = false;
                songList.clear();
                getSongFiles();
                customPlayList.notifyDataSetChanged();
                refreshWarning.setVisibility(View.VISIBLE);
                refreshWarning.setText(getString(R.string.listUpdatedNotice));
                separatorNoticeMenu.setVisibility(View.VISIBLE);
                noticeMenu.setVisibility(View.VISIBLE);

                noticeGone.postDelayed(new Runnable() {
                    public void run() {
                        separatorNoticeMenu.setVisibility(View.GONE);
                        noticeMenu.setVisibility(View.GONE);
                        refreshWarning.setVisibility(View.GONE);
                    }
                }, 2000);
            }
        });

        //to make sure that when you press the logo of the app you see/hide the credits
        credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!creditsButton) {
                    creditsBG.setVisibility(View.VISIBLE);
                    creatorName.setVisibility(View.VISIBLE);
                    resourcesUsed.setVisibility(View.VISIBLE);
                    listResourcesUsed.setVisibility(View.VISIBLE);
                    creatorID.setVisibility(View.VISIBLE);
                    resourcesID.setVisibility(View.VISIBLE);
                    creditsMenuSeparator.setVisibility(View.VISIBLE);
                    creditsButton = true;
                } else {
                    creditsBG.setVisibility(View.GONE);
                    creatorName.setVisibility(View.GONE);
                    resourcesUsed.setVisibility(View.GONE);
                    listResourcesUsed.setVisibility(View.GONE);
                    creatorID.setVisibility(View.GONE);
                    resourcesID.setVisibility(View.GONE);
                    creditsMenuSeparator.setVisibility(View.GONE);
                    creditsButton = false;
                }
            }
        });

    }

    //more of that special work made before to access elements inside of each row of our ListView
    //In this particular case, we're passing to the next activity that
    //this is the song that we want to play now
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.listSong:
                //To fetch the real position of our clickable element
                View parentRow = (View) view.getParent();
                ListView listView = (ListView) parentRow.getParent();
                final int position = listView.getPositionForView(parentRow);

                if (!songList.get(position).getNewCheck()) {
                    songList.get(position).setNewCheck(true);
                }

                songList.get(position).setNewPlaylistCheck(true);
                selectSong();
                break;
            default:
                break;

        }
    }

    //Final part of that special code
    //Here we do everything else like a normal onItemClickListener
    //In this case we only need to take care if we want to add/remove a song from our custom playlist
    //to be seen in the next activity and display a visual warning when that happens
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        View songCheck = view.findViewById(R.id.activeSong);

        //this is the check
        boolean checkSelection = songList.get(position).getNewCheck();

        //this is the visual warning
        //it's smart enough to figure if you've, or not selected it before when you arrive here with
        //a previous created playlist - selectedSong.getNewPlaylistCheck()
        Song selectedSong = songList.get(position);
        if (!checkSelection) {
            songCheck.setBackgroundColor(songCheck.getResources().getColor(R.color.pressed_color));
            if (!selectedSong.getNewPlaylistCheck()) {
                selectedSong.setNewCheck(true);
            }
        } else {
            songCheck.setBackgroundColor(songCheck.getResources().getColor(R.color.colorPrimaryDark));
            selectedSong.setNewCheck(false);
        }
    }

    //Override method to force a refresh (update) on the activity if you granted permission to access the SD Card
    //After a bit of checks (ALL HAIL LORD LOG and Android Dev!), found out that it's
    //requestCode == 1 and grantResults[0] == 0 that take care of that
    //Since we only need that, why not simply check for that without any animation?
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1 && grantResults[0] == 0) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    }

    //After reading a bit about permissions, I checked that we need to do it like this to grant it
    //There was there also a big warning about OS versions previous to 23
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            //permission is automatically granted on sdk<23 upon installation because of what we add in the Manifest
            return true;
        }
    }

    //Method that checks if you just started the APP or you're coming back from the PlayerActivity
    //Editing a playlist is possible because of this!
    //In case you don't have any song in your SD card, this also takes care of it with a warning
    public void isEmpty() {
        if (!isEmpty) {
            getSongFiles();
        } else {
            songList = getIntent().getParcelableArrayListExtra("songsList");
        }
        if (songList.size() == 0) {
            songList.add(Empty);
        }
    }

    //Method that takes care of checking the info of every audio file available in your SD card
    //and create an object for each one
    //It adds its unique ID, song name, group name, and duration
    //Both boolean = false exist because we use them for:
    //songCheck -> song to be added to our playlist
    //PlaylistCheck -> song selected to play now in the PlayerActivity
    //Don't ask me about cursors, all I know is that we use it as it was a way to navigate
    //inside a db (I see query, I see a database!)
    private void getSongFiles() {
        if (isStoragePermissionGranted() && !isEmpty) {
            ContentResolver musicResolver = getContentResolver();
            Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

            if (musicCursor != null && musicCursor.moveToFirst()) {
                //get info of each audio file and adds them to a column
                int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
                int songColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
                int groupColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
                int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                //add songs to the Array List
                while (musicCursor.moveToNext()) {
                    long id = musicCursor.getLong(idColumn);
                    String song = musicCursor.getString(songColumn);
                    String group = musicCursor.getString(groupColumn);
                    long duration = musicCursor.getInt(durationColumn);
                    songList.add(new Song(id, song, group, duration, false, false));
                }
            }
            musicCursor.close();
            //Nothing wrong with an alphabetic sort of the audio files loaded, right?
            Collections.sort(songList, new Comparator<Song>() {
                public int compare(Song a, Song b) {
                    return a.getNewSongName().compareTo(b.getNewSongName());
                }
            });
            //This is a bit of a bad way of checking if our arrayList is empty or not but
            //I already had this added before doing tons of changes in the code and I don't want to check
            //everything here and there again and replace it with songList.isEmpty() :(
            //Both things work fine, right?
            //(I actually only noticed this now...)
            isEmpty = true;
        }
    }

    //Nothing wrong with this
    //We've a method that simply takes care of all our data added to an arrayList to easier to find
    private void creditsListFound() {
        creditsList.add(new Credits("Freepik", "flaticon.com"));
        creditsList.add(new Credits("ButterKnife", "jakewharton.github.io/butterknife"));
        creditsList.add(new Credits("Antrromet's Blog", "blog.antrromet.com/2013/07/handling-clicks-within-list-items-in.html?m=1"));
        creditsList.add(new Credits("Wires Are Obselete", "wiresareobsolete.com/2011/08/clickable-zones-in-listview-items"));
        creditsList.add(new Credits("StackOverFlow Link #1", "stackoverflow.com/questions/20541821/get-listview-item-position-on-button-click/205420341"));
        creditsList.add(new Credits("StackOverFlow Link #2", "stackoverflow.com/questions/37963820/check-permissions-and-reload-the-activity"));
        creditsList.add(new Credits("StackOverFlow Link #3", "stackoverflow.com/questions/625433/how-to-convert-milliseconds-to-x-mins-x-seconds-in-java"));
        creditsList.add(new Credits("StackOverFlow Link #4", "stackoverflow.com/questions/13164054/viewholder-good-practice"));
        creditsList.add(new Credits("StackOverFlow Link #5", "stackoverflow.com/questions/3072173/how-to-call-a-method-after-a-delay-in-android"));
        creditsList.add(new Credits("Big Nerd Ranch", "bignerdranch.com/blog/splash-screens-the-right-way"));
        creditsList.add(new Credits("Envato Tuts+", "code.tutsplus.com/tutorials/create-a-music-player-on-android-project-setup--mobile-22764"));
        creditsList.add(new Credits("Envato Tuts+", "code.tutsplus.com/tutorials/create-a-music-player-on-android-song-playback--mobile-22778"));
    }

    //And the same can be said of this
    //We've a method that opens the url of each Item selected in our ListView
    //It's a way to make our credits list a bit more fancy
    //(not really but it's a bit more interactive)
    public void openURL(int position) {
        Uri homepage;
        Intent openLink;
        switch (position) {
            case 1:
                homepage = Uri.parse("http://www.flaticon.com");
                openLink = new Intent(Intent.ACTION_VIEW, homepage);
                startActivity(openLink);
                break;
            case 2:
                homepage = Uri.parse("http://jakewharton.github.io/butterknife/");
                openLink = new Intent(Intent.ACTION_VIEW, homepage);
                startActivity(openLink);
                break;
            case 3:
                homepage = Uri.parse("http://blog.antrromet.com/2013/07/handling-clicks-within-list-items-in.html?m=1");
                openLink = new Intent(Intent.ACTION_VIEW, homepage);
                startActivity(openLink);
                break;
            case 4:
                homepage = Uri.parse("http://wiresareobsolete.com/2011/08/clickable-zones-in-listview-items/");
                openLink = new Intent(Intent.ACTION_VIEW, homepage);
                startActivity(openLink);
                break;
            case 5:
                homepage = Uri.parse("https://stackoverflow.com/questions/20541821/get-listview-item-position-on-button-click/205420341");
                openLink = new Intent(Intent.ACTION_VIEW, homepage);
                startActivity(openLink);
                break;
            case 6:
                homepage = Uri.parse("https://stackoverflow.com/questions/37963820/check-permissions-and-reload-the-activity");
                openLink = new Intent(Intent.ACTION_VIEW, homepage);
                startActivity(openLink);
                break;
            case 7:
                homepage = Uri.parse("https://stackoverflow.com/questions/625433/how-to-convert-milliseconds-to-x-mins-x-seconds-in-java");
                openLink = new Intent(Intent.ACTION_VIEW, homepage);
                startActivity(openLink);
                break;
            case 8:
                homepage = Uri.parse("https://stackoverflow.com/questions/13164054/viewholder-good-practice");
                openLink = new Intent(Intent.ACTION_VIEW, homepage);
                startActivity(openLink);
                break;
            case 9:
                homepage = Uri.parse("https://stackoverflow.com/questions/3072173/how-to-call-a-method-after-a-delay-in-android");
                openLink = new Intent(Intent.ACTION_VIEW, homepage);
                startActivity(openLink);
                break;
            case 10:
                homepage = Uri.parse("https://www.bignerdranch.com/blog/splash-screens-the-right-way/");
                openLink = new Intent(Intent.ACTION_VIEW, homepage);
                startActivity(openLink);
                break;
            case 11:
                homepage = Uri.parse("https://code.tutsplus.com/tutorials/create-a-music-player-on-android-project-setup--mobile-22764");
                openLink = new Intent(Intent.ACTION_VIEW, homepage);
                startActivity(openLink);
                break;
            case 12:
                homepage = Uri.parse("https://code.tutsplus.com/tutorials/create-a-music-player-on-android-song-playback--mobile-22778");
                openLink = new Intent(Intent.ACTION_VIEW, homepage);
                startActivity(openLink);
                break;
            default:
                break;
        }
    }

    //Method that takes care of the data needed to make the Player work
    //It passes it with an Intent
    public void selectSong() {

        Intent selectSong = new Intent(MainActivity.this, PlayerActivity.class);
        selectSong.putExtra("checkingList", isEmpty);
        selectSong.putParcelableArrayListExtra("songsList", songList);
        startActivity(selectSong);

    }
}
