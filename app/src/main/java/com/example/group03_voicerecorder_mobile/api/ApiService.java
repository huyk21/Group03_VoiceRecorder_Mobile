package com.example.group03_voicerecorder_mobile.api;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {

    @Multipart
    @POST("process_file/convert/")
    Call<JsonObject> uploadAudioFile(
            @Part MultipartBody.Part file,
            @Part("format") RequestBody outputFormat
    );

    @Multipart
    @POST("process_file/speech_to_text/")
    Call<JsonObject> speechToText(
            @Part MultipartBody.Part file);

    @Multipart
    @POST("process_file/remove_silence/")
    Call<JsonObject> removeSilence(
            @Part MultipartBody.Part file);

    @Multipart
    @POST("process_file/reduce_noise/")
    Call<JsonObject> reduceNoise(
            @Part MultipartBody.Part file);

    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);
}

