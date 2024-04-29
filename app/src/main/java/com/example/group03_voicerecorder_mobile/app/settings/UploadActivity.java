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
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;
import com.example.group03_voicerecorder_mobile.utils.PreferenceHelper;

import com.example.group03_voicerecorder_mobile.utils.Utilities;
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
    private DatabaseHelper databaseHelper;
    private TextView tvFilePath;
    private Spinner spinnerOutputFormat;
    private Button btnUpload;
    private String[] formats = {"MP3", "WAV", "OGG"};

    private String fileName;
    private String filePath;
    private Integer recordId;
    private String selectedFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utilities.setCustomTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        tvFilePath = findViewById(R.id.tvFilePath);
        btnUpload = findViewById(R.id.btnUploadFile);
        spinnerOutputFormat = findViewById(R.id.spinnerOutputFormat);

        databaseHelper = new DatabaseHelper(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, formats);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOutputFormat.setAdapter(adapter);
        spinnerOutputFormat.setOnItemSelectedListener(this);

        filePath = getIntent().getStringExtra("recordPath");
        fileName = getIntent().getStringExtra("recordName");
        recordId = getIntent().getIntExtra("recordId", -1);
        System.out.println(fileName + " " + filePath);
        if (filePath != null) {
            tvFilePath.setText(filePath);
        }

        btnUpload.setOnClickListener(v -> {
            if (filePath != null && !filePath.isEmpty()) {
                btnUpload.setEnabled(false);
                uploadFile(filePath);
            } else {
                Toast.makeText(UploadActivity.this, "File path is not valid.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadFile(String path) {
        File file = new File(path);

        // MediaType for the file part
        MediaType mediaType = MediaType.parse("audio/*");
        RequestBody requestFile = RequestBody.Companion.create(file, mediaType);
        MultipartBody.Part body = MultipartBody.Part.createFormData("audio", file.getName(), requestFile);

        selectedFormat = spinnerOutputFormat.getSelectedItem().toString();

        MediaType textType = MediaType.parse("text/plain");

        RequestBody formatBody = RequestBody.Companion.create(selectedFormat, textType);


        ApiService service = RetrofitClient.getClient().create(ApiService.class);

        Call<JsonObject> call = service.uploadAudioFile(body, formatBody);
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
        String url = "http://10.0.2.2:8000/process_file/download" + (fileUrl.startsWith("/") ? fileUrl : "/" + fileUrl);
        Call<ResponseBody> call = service.downloadFile(url);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {

                        try (InputStream inputStream = response.body().byteStream()) {
                            fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
                            // overwrite file
                            Utilities.overwriteAudioFile(getApplicationContext(), fileName, inputStream);
                            // delete old file
                            Utilities.deleteFile(filePath, getApplicationContext());
                            // rename filepath in database
                            String newFilePath = getExternalFilesDir(null).getPath() + "/" + fileName;
                            System.out.println("newFilePath " + newFilePath);
                            databaseHelper.updateFilePath(recordId, newFilePath);
                            Log.i(TAG, "Downloaded converted successfully");
                        } catch (IOException e) {
                            Log.e(TAG, "Error saving downloaded file: " + e.getMessage());
                        } finally {
                            if (databaseHelper != null) {
                                databaseHelper.close();
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
