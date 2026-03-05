package com.stolengalaxy.utils;
import java.io.File;
import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;


public class Requests {
    private static OkHttpClient client = new OkHttpClient();

    private static JsonObject sendRequest(Request request){
        try{
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                JsonElement responseElement = JsonParser.parseString(response.body().string());
                return responseElement.getAsJsonObject();
            } else{
                System.err.println(response.body().string());
                throw new RuntimeException("HTTP request was not successful.");
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static JsonObject sendRequestWithRetries(Request request){
        int attempts = 0;
        boolean successful = false;

        JsonObject response = new JsonObject();

        while(attempts < 2 && !successful){
            attempts++;
            try{
                response = sendRequest(request);
                successful = true;
            } catch (RuntimeException e){
                System.err.println("Sending HTTP request failed.");
                if(attempts >= 2){
                    throw new RuntimeException("Reached request retry limit. Cancelling.", e);
                }

                try{
                    Thread.sleep(5000);
                } catch (InterruptedException interruptedError){
                    throw new RuntimeException(interruptedError);
                }

            }
        }
        return response;
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
        return sendRequestWithRetries(request);
    }
}
