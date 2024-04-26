package com.example.group03_voicerecorder_mobile.app.settings;

import static com.example.group03_voicerecorder_mobile.utils.PreferenceHelper.loadSettingsState;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.GlobalConstants;
import com.example.group03_voicerecorder_mobile.app.statistics.StatisticsActivity;
import com.example.group03_voicerecorder_mobile.utils.PreferenceHelper;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageButton btnToStatistics;
    private SwitchCompat swAutoRecord;
    private SwitchCompat swNoiseReduction;
    private SwitchCompat swSilenceRemoval;
    private SwitchCompat swTranscript;
    private Spinner fileFormat;
    private ImageButton btnToScheduledRecording;
    private boolean settingsChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnBack = findViewById(R.id.btnBack);
        btnToStatistics = findViewById(R.id.btnToStatistics);
        swAutoRecord = findViewById(R.id.swAutoRecord);
        swNoiseReduction = findViewById(R.id.swNoiseReduction);
        swSilenceRemoval = findViewById(R.id.swSilenceRemoval);
        swTranscript = findViewById(R.id.swTranscript);
        fileFormat = findViewById(R.id.dropdown_menu);
        btnToScheduledRecording = findViewById(R.id.scheduledRecording);

        loadSettings();
        setUpListeners();
    }

    private void loadSettings() {
        boolean isAutoRecord = PreferenceHelper.loadSettingsState(this, "isAutoRecord");
        boolean isNoiseReduction = PreferenceHelper.loadSettingsState(this, "isNoiseReduction");
        boolean isSilenceRemoval = PreferenceHelper.loadSettingsState(this, "isSilenceRemoval");
        boolean isTranscript = PreferenceHelper.loadSettingsState(this, "isTranscript");

        swAutoRecord.setChecked(isAutoRecord);
        swNoiseReduction.setChecked(isNoiseReduction);
        swSilenceRemoval.setChecked(isSilenceRemoval);
        swTranscript.setChecked(isTranscript);
    }

    private void toStatisticsActivity() {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    private void setUpListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnToStatistics.setOnClickListener(v -> toStatisticsActivity());

        swAutoRecord.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferenceHelper.saveSettingsState(this, "isAutoRecord", isChecked);
            settingsChanged = true;
        });

        swNoiseReduction.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferenceHelper.saveSettingsState(this, "isNoiseReduction", isChecked);
            settingsChanged = true;
        });

        swSilenceRemoval.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferenceHelper.saveSettingsState(this, "isSilenceRemoval", isChecked);
            settingsChanged = true;
        });

        swTranscript.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferenceHelper.saveSettingsState(this, "isTranscript", isChecked);
            settingsChanged = true;
        });

        btnToScheduledRecording.setOnClickListener(v -> {

        });
    }

    private void restartApp() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        if (i != null) {
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        if (settingsChanged) {
            new AlertDialog.Builder(this)
                    .setTitle("Restart Required")
                    .setMessage("Changes detected! Please restart the app for the changes to take effect")
                    .setPositiveButton("Restart Now", (dialog, which) -> restartApp())
                    .setNegativeButton("Later", (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            super.onBackPressed();
        }
    }
}
