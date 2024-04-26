package com.example.group03_voicerecorder_mobile.app.search_audio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.group03_voicerecorder_mobile.R;
import com.example.group03_voicerecorder_mobile.api.ApiService;
import com.example.group03_voicerecorder_mobile.api.RetrofitClient;
import com.example.group03_voicerecorder_mobile.utils.StringAlgorithms;
import com.google.gson.JsonObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchAudioActivity extends AppCompatActivity {

    private EditText editTextSearchQuery;
    private TextView textViewResults;
    private Button buttonSearch;

    private String convertedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_search);

        editTextSearchQuery = findViewById(R.id.editTextSearch);
        textViewResults = findViewById(R.id.textViewResult);
        buttonSearch = findViewById(R.id.buttonSearch);

        Intent intent = getIntent();
        String recordPath = intent.getStringExtra("recordPath");

        if (recordPath != null) {
            uploadFileAndRetrieveText(recordPath);
        }

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch(editTextSearchQuery.getText().toString(), convertedText);
            }
        });
    }

    private void uploadFileAndRetrieveText(String filePath) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("audio", file.getName(), requestFile);

        ApiService service = RetrofitClient.getClient().create(ApiService.class);
        Call<JsonObject> call = service.speechToText(body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonResponse = response.body();
                    System.out.println(jsonResponse);
                    if (jsonResponse.get("status").toString().equals("error")) {
                        Log.e("errorResponse", jsonResponse.get("message").toString());
                    } else {
                        convertedText = jsonResponse.get("text").toString();
                        textViewResults.setText(jsonResponse.get("text").toString());
                    }
                } else {
                    textViewResults.setText("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                textViewResults.setText("Failure: " + t.getMessage());
            }
        });
    }

    private void performSearch(String pattern, String text) {
        StringAlgorithms.search(text, pattern, 101);
    }
}