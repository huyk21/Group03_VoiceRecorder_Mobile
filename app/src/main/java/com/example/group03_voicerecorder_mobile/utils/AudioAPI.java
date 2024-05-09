package com.example.group03_voicerecorder_mobile.utils;

import com.example.group03_voicerecorder_mobile.api.ApiService;
import com.example.group03_voicerecorder_mobile.api.RetrofitClient;
import com.example.group03_voicerecorder_mobile.audio.TrimAudioCallback;
import com.example.group03_voicerecorder_mobile.data.database.DatabaseHelper;
import com.google.gson.JsonObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AudioAPI {

    public static void removeSilence(Context context, String filePath) {
        File file = new File(filePath);

        // Create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/*"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("audio", file.getName(), requestFile);

        // Retrofit instance
        ApiService service = RetrofitClient.getClient().create(ApiService.class);
        Call<JsonObject> call = service.removeSilence(body);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    String message = jsonObject.get("message").getAsString();
                    String downloadUrl = jsonObject.get("download_url").getAsString();
                    System.out.println("success: " + message);
                    downloadFile(context, downloadUrl);
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to remove silence: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Handle complete failure
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void reduceNoise(Context context, String filePath) {
        File file = new File(filePath);

        // Create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/*"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("audio", file.getName(), requestFile);

        // Retrofit instance
        ApiService service = RetrofitClient.getClient().create(ApiService.class);
        Call<JsonObject> call = service.reduceNoise(body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonObject = response.body();
                    String message = jsonObject.get("message").getAsString();
                    String downloadUrl = jsonObject.get("download_url").getAsString();
                    downloadFile(context, downloadUrl);
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                } else {
                    // Handle failure
                    Toast.makeText(context, "Failed to remove silence: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // Handle complete failure
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void trimAudio(Context context, String filePath, String startTime, String endTime, TrimAudioCallback callback) {
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("audio", file.getName(), requestFile);
        RequestBody start = RequestBody.create(MediaType.parse("text/plain"), startTime);
        RequestBody end = RequestBody.create(MediaType.parse("text/plain"), endTime);

        ApiService service = RetrofitClient.getClient().create(ApiService.class);
        Call<JsonObject> call = service.trimAudio(body, start, end);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String durationStr = response.body().get("trimmed_duration_ms").getAsString();
                    String downloadUrl = response.body().get("download_url").getAsString();
                    int resultDuration = Integer.parseInt(durationStr);
                    callback.onSuccess(resultDuration, downloadUrl);
                } else {
                    callback.onFailure("Failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onFailure("Error: " + t.getMessage());
            }
        });
    }


    public static void downloadFile(Context context, String fileUrl) {
        ApiService service = RetrofitClient.getClient().create(ApiService.class);
        String url = "http://10.0.2.2:8000/process_file/download" + (fileUrl.startsWith("/") ? fileUrl : "/" + fileUrl);
        Call<ResponseBody> call = service.downloadFile(url);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        try (InputStream inputStream = response.body().byteStream()) {
                            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
                            Utilities.overwriteAudioFile(context, fileName, inputStream);
                            Log.i("Download", "Downloaded and saved successfully at " + fileName);
                        } catch (IOException e) {
                            Log.e("Download", "Error saving downloaded file: " + e.getMessage());
                        }
                    }).start();
                } else {
                    Log.e("Download", "File download failed: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Download", "Error downloading file: " + t.getMessage());
            }
        });
    }
    public interface NoiseReductionCallback {
        void onSuccess(int duration);
        void onFailure(String errorMessage);
    }
}
