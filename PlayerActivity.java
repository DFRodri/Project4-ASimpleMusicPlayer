package com.example.android.dasmusicplayer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends AppCompatActivity {

    @BindView(R.id.playerList)
    ImageView goBack;
    @BindView(R.id.songPlaying)
    TextView songPlaying;
    @BindView(R.id.playAndPause)
    ImageView playAndPauseButton;

    @BindDrawable(R.drawable.play_arrow)
    Drawable playButton;
    @BindDrawable(R.drawable.pause_two_lines)
    Drawable pauseButton;

    boolean playPauseButton;
    private MediaPlayer mp;

    String currentSongName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        ButterKnife.bind(this);

        currentSongName = getIntent().getStringExtra("getSongName");
        songPlaying.setText(currentSongName);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goBack = new Intent(PlayerActivity.this, MainActivity.class);
                startActivity(goBack);
            }
        });

        playAndPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playPauseButton) {
                    playAndPauseButton.setBackground(pauseButton);

                    //falta aqui um if que determina se a música já começou ou não e algo que a faz criar no caso de tal ser o caso tipo
//                  //mp = MediaPlayer.create(this, R.raw.music);
                    //mp.start();
                    playPauseButton = false;
                } else {
                    playAndPauseButton.setBackground(playButton);
                    //mp.pause();
                    playPauseButton = true;
                }
            }
        });


    }
}
