package com.example.android.dasmusicplayer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    @BindView(R.id.songList)
    ListView listOfSongs;

    @BindView(R.id.credits)
    ImageView credits;

    @BindView(R.id.creditsBG)
    ImageView creditsBG;

    @BindView(R.id.creatorName)
    TextView creatorName;

    @BindView(R.id.resourcesUsed)
    TextView resourcesUsed;

    @BindView(R.id.creditsList)
    ListView listResourcesUsed;

    @BindView(R.id.closeCredits)
    TextView buttonCreditsClose;

    int order;

    //To do later com a cena do load files da pasta music do cartão em que se nada for detectado, ele faz load disto
    //Song Empty = new Song("No Music Found", "--", "-:--", false);

    final ArrayList<Song> songList = new ArrayList<>();
    final ArrayList<Song> currentSongList = new ArrayList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        /**
         ON HOLD FOR NOW, CENAS PARA FAZER O FETCH DE DATA DE CADA MUSICA

         String musicDirectory = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
         String[] list = {
         MediaStore.Audio.Media.TITLE,
         MediaStore.Audio.Media.ARTIST,
         MediaStore.Audio.Media.DURATION,
         };
         songList.add(new Song(getString(list[0].indexOf(1)),getString(list[0].indexOf(2)),getString(list[0].indexOf(3)),false));
         ^pode vir a ser uma maneira de o fazer no arrayList
         **/

        /**if (currentSongList.isEmpty()) {
         currentSongList.add(Empty);
         } else {
         currentSongList.remove(Empty);
         }**/

        credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creditsBG.setVisibility(View.VISIBLE);
                creatorName.setVisibility(View.VISIBLE);
                resourcesUsed.setVisibility(View.VISIBLE);
                listResourcesUsed.setVisibility(View.VISIBLE);
                buttonCreditsClose.setVisibility(View.VISIBLE);
                listOfSongs.setEnabled(false);
            }
        });

        buttonCreditsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creditsBG.setVisibility(View.GONE);
                creatorName.setVisibility(View.GONE);
                resourcesUsed.setVisibility(View.GONE);
                listResourcesUsed.setVisibility(View.GONE);
                buttonCreditsClose.setVisibility(View.GONE);
                listOfSongs.setEnabled(true);
            }
        });

        songList.add(new Song("I'm Amazing", "Your Momma!", "1:23", false));
        songList.add(new Song("Cadeirudo", "Zé do Gado", "2:54", false));
        songList.add(new Song("Matumbina & Tibúrcio", "Matumba", "0:12", false));
        songList.add(new Song("Zé Ghetto", "Bacalhau à Brás", "5:15", false));
        songList.add(new Song("Here is Portugal", "And his name is John Cena!", "0:25", false));
        songList.add(new Song("I'm Amazing", "Your Momma!", "3:24", false));
        songList.add(new Song("BANANANANANANANNANANA PORRA!", "Zé do Gado", "3:21", false));
        songList.add(new Song("Matumbina & Tibúrcio", "Matumba", "2:54", false));
        songList.add(new Song("Zé Ghetto", "Bacalhau à Brás", "5:23", false));
        songList.add(new Song("Here is Portugal", "And his name is John Cena!", "0:57", false));
        songList.add(new Song("I'm Amazing", "Your Momma!", "0:12", false));
        songList.add(new Song("Cadeirudo", "Zé do Gado4", "0:54", false));
        songList.add(new Song("Matumbina & Tibúrcio", "Matumba", "2:12", false));
        songList.add(new Song("Zé Ghetto", "Bacalhau à Brás", "1:23", false));
        songList.add(new Song("Here is Portugal", "And his name is John Cena2!", "69:69", false));

        SongsAdapter customPlayList = new SongsAdapter(this, songList) {
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

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.listSong:
                //To fetch the real position of our clickable element
                View parentRow = (View) view.getParent();
                ListView listView = (ListView) parentRow.getParent();
                final int position = listView.getPositionForView(parentRow);

                if (!currentSongList.contains(songList.get(position))) {
                    songList.get(position).setNewCheck(true);
                    currentSongList.add(songList.get(position));
                }

                String songName = songList.get(position).getNewSongName();
                selectSong(songName);
                break;
            default:
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        view.getTag();
        View songCheck = view.findViewById(R.id.activeSong);

        boolean checkSelection = songList.get(position).getNewCheck();
        Song selectedSong = songList.get(position);

        if (!checkSelection) {
            songCheck.setBackgroundColor(songCheck.getResources().getColor(R.color.pressed_color));
            selectedSong.setNewCheck(true);
            currentPlayList(selectedSong, checkSelection);
        } else {
            songCheck.setBackgroundColor(songCheck.getResources().getColor(R.color.colorPrimaryDark));
            selectedSong.setNewCheck(false);
            currentPlayList(selectedSong, checkSelection);
        }

        order = position;
        Toast.makeText(this, "Item Click " + position + "\n" + order + "\n" + currentSongList.size(), Toast.LENGTH_SHORT).show();
    }


    public void currentPlayList(Song selectedSong, boolean checkSelection) {
        if (!checkSelection) {
            currentSongList.add(selectedSong);
        } else {
            currentSongList.remove(selectedSong);
        }
    }

    public void selectSong(String songName) {

        Intent selectSong = new Intent(MainActivity.this, PlayerActivity.class);
        selectSong.putExtra("getSongName", songName);
        //selectSong.putExtra("songsSelected", currentSongList);
        startActivity(selectSong);

    }
}
