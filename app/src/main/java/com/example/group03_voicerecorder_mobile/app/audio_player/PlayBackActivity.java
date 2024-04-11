package com.example.group03_voicerecorder_mobile.app.audio_player;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.group03_voicerecorder_mobile.R;

public class PlayBackActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ImageButton playPauseButton;
    private ImageButton rewindButton;
    private ImageButton fastForwardButton;
    private Chronometer chronometer;
    private TextView fileNameTextView;
    private ImageButton btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
        // Initialize UI components
        playPauseButton = findViewById(R.id.button_play_pause);
        rewindButton = findViewById(R.id.button_rewind);
        fastForwardButton = findViewById(R.id.button_fast_forward);
        chronometer = findViewById(R.id.chronometer_playback);
        fileNameTextView = findViewById(R.id.textView_fileName);

        // Retrieve the filename from the intent
        String fileName = getIntent().getStringExtra("record_name");
        fileNameTextView.setText(fileName); // Set the filename in the TextView

        // Initialize MediaPlayer
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileName); // Set the data source to the file path
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Rewind button functionality
        rewindButton.setOnClickListener(view -> {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000); // Rewind 5 seconds
        });

        // Fast forward button functionality
        fastForwardButton.setOnClickListener(view -> {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000); // Fast forward 5 seconds
        });

        // Play/Pause button functionality
        playPauseButton.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playPauseButton.setImageResource(android.R.drawable.ic_media_play); // Update button icon to 'play'
            } else {
                mediaPlayer.start();
                playPauseButton.setImageResource(android.R.drawable.ic_media_pause); // Update button icon to 'pause'
                chronometer.start(); // Start the chronometer
            }
        });

        // Back button functionality
        ImageButton backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(view -> finish()); // Close the activity

        // Properties button functionality
        ImageButton propertiesButton = findViewById(R.id.button_properties);
        propertiesButton.setOnClickListener(view -> {
            
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Release the media player resources
        }
    }
}
