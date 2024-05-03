    package com.example.group03_voicerecorder_mobile.audio;

    import androidx.appcompat.app.AppCompatActivity;

    import android.content.Context;
    import android.content.Intent;
    import android.os.Bundle;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.example.group03_voicerecorder_mobile.R;
    import com.example.group03_voicerecorder_mobile.app.record.Record;
    import com.example.group03_voicerecorder_mobile.app.settings.UploadActivity;
    import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;
    import com.example.group03_voicerecorder_mobile.utils.AudioAPI;
    import com.example.group03_voicerecorder_mobile.utils.Utilities;
    import com.google.android.material.slider.RangeSlider;

    import java.util.ArrayList;
    import java.util.List;

    public class TrimAudioActivity extends AppCompatActivity {
        Context context;
        private ImageButton btnBack;
        private ImageButton buttonProperties;
        private TextView textViewFileName;
        private EditText editTextStartTime;
        private EditText editTextEndTime;
        private Button btnTrimAudio;

        private DatabaseHelper databaseHelper;
        private Record record;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            Utilities.setCustomTheme(this);
            setContentView(R.layout.activity_trim_audio);
            super.onCreate(savedInstanceState);
            initializeViews();
            loadRecordingData();
            databaseHelper = new DatabaseHelper(getApplicationContext());


            record = (Record) getIntent().getExtras().getSerializable("record");
            textViewFileName.setText(record.getFilename());
            btnTrimAudio.setOnClickListener(v -> {
                AudioAPI.trimAudio(getApplicationContext(), record.getFilePath(),
                        editTextStartTime.getText().toString(), editTextEndTime.getText().toString(),
                        new TrimAudioCallback() {
                            @Override
                            public void onSuccess(int resultDuration, String downloadUrl) {
                                System.out.println("Result Duration: " + resultDuration);
                                if (resultDuration > 0) {
                                    databaseHelper.updateDurationState(record.getId(), resultDuration);
                                }
                                AudioAPI.downloadFile(context, downloadUrl);
                                Toast.makeText(getApplicationContext(), "Trimmed successfully!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(TrimAudioActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
            });
            btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        }

        private void loadRecordingData() {
            Intent intent = getIntent();
            Bundle bd = intent.getExtras();

            if (bd != null) {
                Record record = (Record) bd.getSerializable("record");
                System.out.println(record);
            }
            else {
                Toast.makeText(this, "Could not find record", Toast.LENGTH_LONG).show();
            }
        }

        private void initializeViews() {
            context = getApplicationContext();
            btnBack = findViewById(R.id.btnBack);
            buttonProperties = findViewById(R.id.button_properties);
            textViewFileName = findViewById(R.id.textView_fileName);
            editTextStartTime = findViewById(R.id.editText_startTime);
            editTextEndTime = findViewById(R.id.editText_endTime);
            btnTrimAudio = findViewById(R.id.btnTrimAudio);

            // Set listeners or further initialization here if needed
        }
    }