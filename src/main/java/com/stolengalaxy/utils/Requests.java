package com.stolengalaxy.utils;
import java.io.File;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;


public class Requests {
    private static OkHttpClient client = new OkHttpClient();

    public static JsonObject post(String url, RequestBody requestBody){
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try(Response response = client.newCall(request).execute()){
            if(response.isSuccessful()){
                JsonElement responseElement= JsonParser.parseString(response.body().string());
                return responseElement.getAsJsonObject();
            } else{
                throw new RuntimeException("Http request failed");
            }
        } catch (IOException e){
            throw new RuntimeException("Http request failed", e);
        }
    }

    public static RequestBody fileRequestBody(String filePath){
        File file = new File(filePath);

        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("image/jpeg")))
                .build();
    }

    public static JsonObject postFile (String url, String filePath){
        RequestBody requestBody = fileRequestBody(filePath);
        return post(url, requestBody);
    }


}
