package com.example.group03_voicerecorder_mobile.app.audio_player;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.main.WaveformView;

import java.io.IOException;

public class PlayBackActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ImageButton playPauseButton;
    private ImageButton rewindButton;
    private ImageButton fastForwardButton;
    private Chronometer chronometer;
    private TextView textViewFileName;
    private long pauseOffset;
    private final int SKIP_TIME_MS = 5000; // Amount of milliseconds to skip
    private String filePath;
    private WaveformView waveformView;
    private Handler waveformHandler = new Handler();
    private final int UPDATE_FREQUENCY_MS = 100; // Update waveform every 100 ms
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);
        textViewFileName = findViewById(R.id.textView_fileName);
        waveformView = findViewById(R.id.waveform_view); // Assuming you have a WaveformView in your layout
        playPauseButton = findViewById(R.id.button_play_pause);
        rewindButton = findViewById(R.id.button_rewind);
        fastForwardButton = findViewById(R.id.button_fast_forward);
        chronometer = findViewById(R.id.chronometer_playback);

        setupMediaPlayer();
        Bundle bd = getIntent().getExtras();

        if(bd!=null){
            textViewFileName.setText(bd.getString("recordName"));
        }
        else{
            textViewFileName.setText("Record Name");
        }



        playPauseButton.setOnClickListener(view -> togglePlayPause());
        rewindButton.setOnClickListener(view -> rewind());
        fastForwardButton.setOnClickListener(view -> fastForward());
    }

    private void setupMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        String filePath = getIntent().getStringExtra("recordPath");
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();

            mediaPlayer.setOnCompletionListener(mp -> {
                // Handle completion of playback here
                playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                chronometer.stop();
                // Reset the chronometer
                chronometer.setBase(SystemClock.elapsedRealtime());
                pauseOffset = 0;
            });
        } catch (IOException e) {
            e.printStackTrace();
            // Handle errors here
            Toast.makeText(this, "Unable to play this audio file.", Toast.LENGTH_LONG).show();
        }
    }

    private void togglePlayPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playPauseButton.setImageResource(android.R.drawable.ic_media_play);
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            chronometer.stop();
            waveformHandler.removeCallbacksAndMessages(null); // Stop updating the waveform
        } else {
            mediaPlayer.start();
            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            updateWaveform(); // Start updating the waveform
        }
    }
    private void updateWaveform() {
        // Simulate waveform data update. Here you might need real data from the MediaPlayer.
        // Since MediaPlayer doesn't provide amplitude data directly, this is a placeholder.
        float randomAmplitude = (float) (Math.random() * 500); // Example amplitude value
        waveformView.addAmplitude(randomAmplitude); // Add random amplitude to waveform view
        waveformHandler.postDelayed(this::updateWaveform, UPDATE_FREQUENCY_MS);
    }

    private void rewind() {
        if (mediaPlayer.isPlaying() || pauseOffset != 0) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            currentPosition = Math.max(currentPosition - SKIP_TIME_MS, 0);
            mediaPlayer.seekTo(currentPosition);

            // Adjust chronometer
            pauseOffset = Math.max(pauseOffset - SKIP_TIME_MS, 0);
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        }
    }

    private void fastForward() {
        if (mediaPlayer.isPlaying() || pauseOffset != 0) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            currentPosition = Math.min(currentPosition + SKIP_TIME_MS, mediaPlayer.getDuration());
            mediaPlayer.seekTo(currentPosition);

            // Adjust chronometer
            pauseOffset += SKIP_TIME_MS;
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        }
    }

    private void resetPlayback() {
        playPauseButton.setImageResource(android.R.drawable.ic_media_play);
        mediaPlayer.seekTo(0);
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        waveformHandler.removeCallbacksAndMessages(null);
    }
}
