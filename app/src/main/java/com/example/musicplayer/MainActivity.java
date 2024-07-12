package com.example.musicplayer;
import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView textViewTrackTitle;
    private SeekBar seekBar;
    private Button buttonPlayPause;
    private MediaPlayer mediaPlayer;
    private Handler handler;

    private String[] trackTitles = {"Track 1", "Track 2"};
    private int[] trackResources = {R.raw.track1, R.raw.track2};
    private int currentTrackIndex = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewTrackTitle = findViewById(R.id.textViewtracktitle);
        seekBar = findViewById(R.id.seekBar);
        Button buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonPlayPause = findViewById(R.id.buttonplaypause);
        Button buttonNext = findViewById(R.id.buttonNext);

        handler = new Handler();


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(mp -> playNextTrack());


        buttonPrevious.setOnClickListener(v -> playPreviousTrack());
        buttonPlayPause.setOnClickListener(v -> togglePlayPause());
        buttonNext.setOnClickListener(v -> playNextTrack());


        prepareMediaPlayer(currentTrackIndex);
    }

    private void prepareMediaPlayer(int trackIndex) {
        try {
            mediaPlayer.reset();
            AssetFileDescriptor afd = getResources().openRawResourceFd(trackResources[trackIndex]);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.prepare();
            textViewTrackTitle.setText(trackTitles[trackIndex]);
            seekBar.setMax(mediaPlayer.getDuration());
            seekBar.setProgress(0);

            mediaPlayer.setOnPreparedListener(mp -> {
                mediaPlayer.start();
                buttonPlayPause.setText("Pause");
                updateSeekBar();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void togglePlayPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            buttonPlayPause.setText("Play");
        } else {
            mediaPlayer.start();
            buttonPlayPause.setText("Pause");
            updateSeekBar();
        }
    }

    private void updateSeekBar() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        if (mediaPlayer.isPlaying()) {
            handler.postDelayed(this::updateSeekBar, 1000);
        }
    }

    private void playNextTrack() {
        currentTrackIndex = (currentTrackIndex + 1) % trackResources.length;
        prepareMediaPlayer(currentTrackIndex);
    }

    private void playPreviousTrack() {
        currentTrackIndex = (currentTrackIndex - 1 + trackResources.length) % trackResources.length;
        prepareMediaPlayer(currentTrackIndex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        handler.removeCallbacksAndMessages(null);
 }
}