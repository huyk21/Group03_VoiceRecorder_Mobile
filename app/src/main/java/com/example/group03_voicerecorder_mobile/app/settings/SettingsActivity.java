package com.example.group03_voicerecorder_mobile.app.settings;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.app.GlobalConstants;
import com.example.group03_voicerecorder_mobile.app.iocheck.SoundTestActivity;
import com.example.group03_voicerecorder_mobile.app.statistics.StatisticsActivity;
import com.example.group03_voicerecorder_mobile.utils.PreferenceHelper;
import com.example.group03_voicerecorder_mobile.utils.Utilities;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private ImageButton btnToStatistics;
    private SwitchCompat swAutoRecord;
    private SwitchCompat swNoiseReduction;
    private SwitchCompat swSilenceRemoval;
    private SwitchCompat swPhoneActive;
    private SwitchCompat swTranscript;
    private Spinner fileFormat;
    private Spinner themeList;
    private TextView selectedFormat;
    private TextView selectedTheme;
    private EditText editTextDateTime;
    private ImageButton btnToSoundTest;
    private static boolean settingsChanged = false;
    private static boolean isUserTriggeredTheme = true;
    private static boolean isUserTriggeredFormat = true;
    private static boolean isDeleteSchedule = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utilities.setCustomTheme(this);
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
        swPhoneActive = findViewById(R.id.swPhoneActive);
        btnToSoundTest = findViewById(R.id.btnToSoundTest);
        editTextDateTime = findViewById(R.id.editTextDateTime);

        setUpDateTimePicker();
        createExtensionList();
        createThemeList();
        loadSettings();
        setUpListeners();
    }

    private void deleteScheduledTime() {


        // Optionally, update the EditText to reflect that the time has been cleared
        EditText editTextDateTime = findViewById(R.id.editTextDateTime);
        editTextDateTime.setText("");  // Clear the text field


    }
    private void setUpDateTimePicker() {
        editTextDateTime.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year1);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    String formattedDateTime = dateFormat.format(calendar.getTime());

                    editTextDateTime.setText(formattedDateTime);
                    PreferenceHelper.saveSelectedFormat(this, "selectedDateTime", formattedDateTime);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }, year, month, day);
            datePickerDialog.show();

        });

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
            ImageButton btnDeleteSchedule = findViewById(R.id.btnDeleteSchedule);
            btnDeleteSchedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteScheduledTime();

                    isDeleteSchedule = true;


                }


            });
            boolean isAutoRecord = PreferenceHelper.loadSettingsState(this, "isAutoRecord");
            boolean isNoiseReduction = PreferenceHelper.loadSettingsState(this, "isNoiseReduction");
            boolean isSilenceRemoval = PreferenceHelper.loadSettingsState(this, "isSilenceRemoval");
            boolean isTranscript = PreferenceHelper.loadSettingsState(this, "isTranscript");
            boolean isPhoneActive = PreferenceHelper.loadSettingsState(this, "isPhoneActive");
            String dateTime = PreferenceHelper.getSelectedDate(this, "selectedDateTime");
            if(isDeleteSchedule){
                PreferenceHelper.removeSetting(this, "selectedDateTime");
                dateTime="";
                isDeleteSchedule = false;
            }
            String theme = PreferenceHelper.getSelectedTheme(this, "selectedTheme");
            String format = PreferenceHelper.getSelectedFormat(this, "selectedFormat");

            int themePosition = Arrays.asList(GlobalConstants.THEMES).indexOf(theme);
            if (themePosition != -1) {
                isUserTriggeredTheme = false;
                themeList.setSelection(themePosition);
            }

            int formatPosition = Arrays.asList(GlobalConstants.FORMATS_SUPPORTED).indexOf(format);
            if (formatPosition != -1) {
                isUserTriggeredFormat = false;
                fileFormat.setSelection(formatPosition);
            }

            swAutoRecord.setChecked(isAutoRecord);
            swNoiseReduction.setChecked(isNoiseReduction);
            swSilenceRemoval.setChecked(isSilenceRemoval);
            swTranscript.setChecked(isTranscript);
            swPhoneActive.setChecked(isPhoneActive);
            selectedTheme.setText(theme);
            selectedFormat.setText(format);
            if (!dateTime.isEmpty()) {
                editTextDateTime.setText(dateTime);
            } else {
                editTextDateTime.setText("");  // Clear or set to a default hint if necessary
            }
        }

    private void toStatisticsActivity() {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    private void toSoundTestActivity() {
        Intent intent = new Intent(this, SoundTestActivity.class);
        startActivity(intent);
    }

    private void setUpListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnToStatistics.setOnClickListener(v -> toStatisticsActivity());

        btnToSoundTest.setOnClickListener(v -> toSoundTestActivity());

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

        swPhoneActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferenceHelper.saveSettingsState(this, "isPhoneActive", isChecked);
            settingsChanged = true;
        });

        fileFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUserTriggeredFormat) {
                    String selectedExtension = GlobalConstants.FORMATS_SUPPORTED[position];
                    selectedFormat.setAllCaps(true);
                    selectedFormat.setText(selectedExtension);
                    PreferenceHelper.saveSelectedFormat(SettingsActivity.this, "selectedFormat", selectedExtension);
                    settingsChanged = true;
                }
                isUserTriggeredFormat = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        themeList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUserTriggeredTheme)
                {
                    String selected = GlobalConstants.THEMES[position];
                    selectedTheme.setText(selected);
                    PreferenceHelper.saveSelectedTheme(SettingsActivity.this, "selectedTheme", selected);
                    settingsChanged = true;
                }
                isUserTriggeredTheme = true;
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
