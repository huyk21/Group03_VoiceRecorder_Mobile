package com.example.group03_voicerecorder_mobile.app.search_audio;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.group03_voicerecorder_mobile.R;

public class SearchAudioActivity extends AppCompatActivity {

    private EditText editTextSearchQuery;
    private TextView textViewResults;
    private Button buttonSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_search);

        editTextSearchQuery = findViewById(R.id.editTextSearch);
        textViewResults = findViewById(R.id.textViewResult);
        buttonSearch = findViewById(R.id.buttonSearch);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch(editTextSearchQuery.getText().toString());
            }
        });
    }

    private void performSearch(String query) {
        // This should interact with your audio processing/searching logic
        String result = "Searching for: " + query;
        // You would replace this with the actual logic to find the word in the audio file.
        textViewResults.setText(result);
    }

}