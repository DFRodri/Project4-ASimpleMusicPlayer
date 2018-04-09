package com.example.android.dasmusicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Splash Screen done right!
        //Call from one "activity" (not really but sort of gives you that idea) to the one that
        //matters - MainActivity
        //Makes use of the AndroidManifest and Styles to make all this magic happen
        //More info available here - https://www.bignerdranch.com/blog/splash-screens-the-right-way/
        Intent splash = new Intent(this, MainActivity.class);
        startActivity(splash);
        finish();
    }
}
