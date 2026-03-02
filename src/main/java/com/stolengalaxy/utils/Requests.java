package com.stolengalaxy.utils;
import java.io.File;
import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;


public class Requests {
    private static OkHttpClient client = new OkHttpClient();

    public static JsonObject sendRequest(Request request){
        try(Response response = client.newCall(request).execute()){
            if(response.isSuccessful()){
                JsonElement responseElement = JsonParser.parseString(response.body().string());
                return responseElement.getAsJsonObject();
            } else{
                throw new RuntimeException("Http request failed");
            }
        } catch (IOException e){
            throw new RuntimeException("Http request failed", e);
        }
    }

    public static Request fileUploadRequest(String url, String filePath){
        File file = new File(filePath);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("image/jpeg")))
                .build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }


    public static Request generateRequestFromJson(String url, String json, boolean postRequest){
        MediaType JSON = MediaType.parse("application/json");

        RequestBody requestBody = RequestBody.create(json, JSON);
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody);
        if (postRequest){
            builder.post(requestBody);
        } else{
            builder.get();
        }
        return builder.build();
    }

    public static Request addHeader(Request request, String name, String value){
        return request.newBuilder()
                .addHeader(name, value)
                .build();
    }

    public static JsonObject postFile (String url, String filePath){
        Request request = fileUploadRequest(url, filePath);
        return sendRequest(request);
    }
}
