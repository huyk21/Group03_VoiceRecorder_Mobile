package com.example.group03_voicerecorder_mobile.app.settings;

import static com.example.group03_voicerecorder_mobile.utils.PreferenceHelper.loadSettingsState;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.GlobalConstants;
import com.example.group03_voicerecorder_mobile.app.statistics.StatisticsActivity;
import com.example.group03_voicerecorder_mobile.utils.PreferenceHelper;

import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private ImageButton btnToStatistics;
    private SwitchCompat swAutoRecord;
    private SwitchCompat swNoiseReduction;
    private SwitchCompat swSilenceRemoval;
    private SwitchCompat swTranscript;
    private Spinner fileFormat;
    private Spinner themeList;
    private TextView selectedFormat;
    private TextView selectedTheme;
    private ImageButton btnToScheduledRecording;
    private boolean settingsChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String theme = PreferenceHelper.getSelectedTheme(this, "selectedTheme");
        switch (theme) {
            case GlobalConstants.THEME_BLUE:
            {
                setTheme(R.style.AppTheme_Blue);
                break;
            }
            case GlobalConstants.THEME_TEAL:
            {
                setTheme(R.style.AppTheme_Teal);
                break;
            }
            case GlobalConstants.THEME_RED:
            {
                setTheme(R.style.AppTheme_Red);
                break;
            }
            case GlobalConstants.THEME_PINK:
            {
                setTheme(R.style.AppTheme_Pink);
                break;
            }
            case GlobalConstants.THEME_PURPLE:
            {
                setTheme(R.style.AppTheme_Purple);
                break;
            }
            case GlobalConstants.THEME_ORANGE:
            {
                setTheme(R.style.AppTheme_DeepOrange);
                break;
            }
            default: {
                setTheme(R.style.AppTheme_Default);
                break;
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnBack = findViewById(R.id.btnBack);
        btnToStatistics = findViewById(R.id.btnToStatistics);
        swAutoRecord = findViewById(R.id.swAutoRecord);
        swNoiseReduction = findViewById(R.id.swNoiseReduction);
        swSilenceRemoval = findViewById(R.id.swSilenceRemoval);
        swTranscript = findViewById(R.id.swTranscript);
        fileFormat = findViewById(R.id.dropdown_menu);
        themeList = findViewById(R.id.themes_dropdown);
        selectedFormat = findViewById(R.id.formatType);
        selectedTheme = findViewById(R.id.themeType);
        btnToScheduledRecording = findViewById(R.id.scheduledRecording);

        createExtensionList();
        createThemeList();
        loadSettings();
        setUpListeners();
    }

    private void createExtensionList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, GlobalConstants.FORMATS_SUPPORTED);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        fileFormat.setAdapter(adapter);
    }

    private void createThemeList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, GlobalConstants.THEMES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        themeList.setAdapter(adapter);
    }

    private void loadSettings() {
        boolean isAutoRecord = PreferenceHelper.loadSettingsState(this, "isAutoRecord");
        boolean isNoiseReduction = PreferenceHelper.loadSettingsState(this, "isNoiseReduction");
        boolean isSilenceRemoval = PreferenceHelper.loadSettingsState(this, "isSilenceRemoval");
        boolean isTranscript = PreferenceHelper.loadSettingsState(this, "isTranscript");
        String theme = PreferenceHelper.getSelectedTheme(this, "selectedTheme");
        String format = PreferenceHelper.getSelectedFormat(this, "selectedFormat");

        int themePosition = Arrays.asList(GlobalConstants.THEMES).indexOf(theme);
        if (themePosition != -1) {
            themeList.setSelection(themePosition);
        }

        // Set the spinner selection for the format
        int formatPosition = Arrays.asList(GlobalConstants.FORMATS_SUPPORTED).indexOf(format);
        if (formatPosition != -1) {
            fileFormat.setSelection(formatPosition);
        }

        System.out.println(theme);
        System.out.println(format);

        swAutoRecord.setChecked(isAutoRecord);
        swNoiseReduction.setChecked(isNoiseReduction);
        swSilenceRemoval.setChecked(isSilenceRemoval);
        swTranscript.setChecked(isTranscript);
        selectedTheme.setText(theme);
        selectedFormat.setText(format);
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

        fileFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedExtension = GlobalConstants.FORMATS_SUPPORTED[position];
                selectedFormat.setAllCaps(true);
                selectedFormat.setText(selectedExtension);
                PreferenceHelper.saveSelectedFormat(SettingsActivity.this, "selectedFormat", selectedExtension);
                System.out.println(selectedExtension);
                settingsChanged = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        themeList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = GlobalConstants.THEMES[position];
                selectedTheme.setText(selected);
                PreferenceHelper.saveSelectedTheme(SettingsActivity.this, "selectedTheme", selected);
                System.out.println(selected);
                settingsChanged = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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
