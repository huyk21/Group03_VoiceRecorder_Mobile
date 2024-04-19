package com.example.group03_voicerecorder_mobile.api;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
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
            @Part("output_format") String outputFormat
    );

    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);
}

