package com.example.group03_voicerecorder_mobile.app.audio_player;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class PlayBackActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ImageButton playPauseButton;
    private ImageButton rewindButton;
    private ImageButton fastForwardButton;
    private ImageButton btnBack;
    private Chronometer chronometer;
    private TextView textViewFileName;
    private long pauseOffset;
    private final int SKIP_TIME_MS = 3000; // Amount of milliseconds to skip
    private String filePath;
    private WaveformView waveformView;
    private Handler waveformHandler = new Handler();
    private final int UPDATE_FREQUENCY_MS = 100; // Update waveform every 100 ms
    private ArrayList<Integer> amplitudeList = new ArrayList<>();


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
        btnBack = findViewById(R.id.btnBack);
        System.out.println("oncreate playback");

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
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void setupMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        String filePath = getIntent().getStringExtra("recordPath");
        int recordId = getIntent().getIntExtra("recordId", -1); // Default to -1 if not found
        loadAmplitudeData(recordId);  // Load amplitude data for the current recording

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
                waveformView.resetWaveform();
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
            // Check if playback was finished
            if(mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration()) {
                mediaPlayer.seekTo(0); // Reset to start
                chronometer.setBase(SystemClock.elapsedRealtime()); // Reset chronometer
                pauseOffset = 0; // Reset pause offset
                waveformView.resetWaveform();
            }
            mediaPlayer.start();
            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            updateWaveform(); // Start updating the waveform
        }
    }
    private void loadAmplitudeData(int recordId) {
        String fileName = "amplitudes_" + recordId + ".json";
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);
        if (file.exists()) {
            try {
                // Read the file to a String
                String json = new String(Files.readAllBytes(file.toPath()));
                // Parse the JSON data
                JSONObject jsonObject = new JSONObject(json);
                JSONArray amplitudeArray = jsonObject.getJSONArray("amplitudes");
                amplitudeList = new ArrayList<>();
                for (int i = 0; i < amplitudeArray.length(); i++) {
                    amplitudeList.add(amplitudeArray.getInt(i));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                // Handle error
                Toast.makeText(this, "Error loading amplitude data.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error loading amplitude data.", Toast.LENGTH_SHORT).show();

        }
    }

    private void updateWaveform() {
        if (mediaPlayer.isPlaying() && amplitudeList != null && !amplitudeList.isEmpty()) {
            // Calculate the current position in the amplitudes list
            int position = (int) ((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration() * amplitudeList.size());
            // Make sure the position does not exceed the list size
            position = Math.min(position, amplitudeList.size() - 1);
            // Get the amplitude value for the current position
            int amplitude = amplitudeList.get(position);
            waveformView.addAmplitude((float) amplitude); // Update the waveform view
            waveformHandler.postDelayed(this::updateWaveform, UPDATE_FREQUENCY_MS);
        }
    }


    private void rewind() {
        if (mediaPlayer.isPlaying() || pauseOffset != 0) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            currentPosition = Math.max(currentPosition - SKIP_TIME_MS, 0);

            mediaPlayer.seekTo(currentPosition);
            waveformView.setPlaybackPosition(amplitudeList, mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());


            // Adjust chronometer
            pauseOffset = Math.max(pauseOffset - SKIP_TIME_MS, 0);
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            mediaPlayer.start();
        }
    }

    private void fastForward() {
        if (mediaPlayer.isPlaying() || pauseOffset != 0) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            currentPosition = Math.min(currentPosition + SKIP_TIME_MS, mediaPlayer.getDuration());
            mediaPlayer.seekTo(currentPosition);
            waveformView.setPlaybackPosition(amplitudeList, mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());
            // Adjust chronometer
            pauseOffset += SKIP_TIME_MS;
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            mediaPlayer.start();
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
