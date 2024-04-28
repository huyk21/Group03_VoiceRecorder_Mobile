package com.example.group03_voicerecorder_mobile.app.settings;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.api.ApiService;
import com.example.group03_voicerecorder_mobile.api.RetrofitClient;
import com.example.group03_voicerecorder_mobile.app.GlobalConstants;
import com.example.group03_voicerecorder_mobile.utils.PreferenceHelper;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "UploadActivity";
    private TextView tvFilePath;
    private Spinner spinnerOutputFormat;
    private Button btnUpload;
    private String[] formats = {"MP3", "WAV", "OGG"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String selectedTheme = PreferenceHelper.getSelectedTheme(this, "selectedTheme");
        switch (selectedTheme) {
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
        setContentView(R.layout.activity_upload);

        tvFilePath = findViewById(R.id.tvFilePath);
        btnUpload = findViewById(R.id.btnUploadFile);
        spinnerOutputFormat = findViewById(R.id.spinnerOutputFormat);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, formats);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOutputFormat.setAdapter(adapter);
        spinnerOutputFormat.setOnItemSelectedListener(this);

        String filePath = getIntent().getStringExtra("recordPath");
        if (filePath != null) {
            tvFilePath.setText(filePath);
        }

        btnUpload.setOnClickListener(v -> {
            if (filePath != null && !filePath.isEmpty()) {
                uploadFile(filePath);
            } else {
                Toast.makeText(UploadActivity.this, "File path is not valid.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadFile(String path) {
        File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("audio", file.getName(), requestFile);
        String selectedFormat = spinnerOutputFormat.getSelectedItem().toString();
        RequestBody format = RequestBody.create(MediaType.parse("text/plain"), selectedFormat);

        ApiService service = RetrofitClient.getClient().create(ApiService.class);
        Call<JsonObject> call = service.uploadAudioFile(body, selectedFormat);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonObject = response.body();
                    String fileUrl = jsonObject.get("download_url").getAsString();
                    downloadFile(fileUrl);
                    Log.d(TAG, "Upload succeeded and downloading file.");
                } else {
                    Log.e(TAG, "Upload failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    private void downloadFile(String fileUrl) {
        ApiService service = RetrofitClient.getClient().create(ApiService.class);
        // Ensure the URL path is correctly concatenated
        Call<ResponseBody> call = service.downloadFile("http://10.0.2.2:8000/process_file/download" + fileUrl);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // The rest of your file saving code here
                                File convertedDirectory = new File(getExternalFilesDir(null), "converted");
                                if (!convertedDirectory.exists()) {
                                    convertedDirectory.mkdirs();
                                }
                                String filename = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
                                File downloadedFile = new File(convertedDirectory, filename);

                                try (InputStream inputStream = response.body().byteStream();
                                     FileOutputStream outputStream = new FileOutputStream(downloadedFile)) {
                                    byte[] buffer = new byte[4096];
                                    int bytesRead;
                                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                                        outputStream.write(buffer, 0, bytesRead);
                                    }
                                    runOnUiThread(() -> Toast.makeText(UploadActivity.this,
                                            "File downloaded successfully to: " + downloadedFile.getPath(),
                                            Toast.LENGTH_LONG).show());
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "Error saving downloaded file: " + e.getMessage());
                            }
                        }
                    }).start();
                } else {
                    Log.e(TAG, "File download failed: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Error downloading file: " + t.getMessage());
            }
        });

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), formats[position], Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
