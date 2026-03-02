package com.stolengalaxy.clients;

import com.google.gson.JsonObject;
import com.stolengalaxy.config.AppConfig;
import com.stolengalaxy.utils.Requests;
import okhttp3.Request;


public class NanoBananaApiClient {
    private static final String endpoint = "https://api.nanobananaapi.ai/api/v1/nanobanana/";

    private static Request authenticateRequest(Request request){
        return Requests.addHeader(request, "Authorization", AppConfig.NanoBananaAPIKey);
    }

    public static Request imageToImageRequest(String imageURL, String promptText){
        // "IMAGETOIAMGE" [sic]
        String json = String.format("""
                {
                    "prompt": "%s",
                    "type": "IMAGETOIAMGE",
                    "numImages": 1,
                    "imageURLs": ["%s"],
                    "resolution": "1K"
                }
                """, promptText, imageURL);

        Request requestBody = Requests.generateRequestFromJson(endpoint + "generate", json, true);
        requestBody = Requests.addHeader(requestBody, "Content-Type", "application/json");

        return authenticateRequest(requestBody);
    }

    public static String checkGenerationStatus(String taskID){
        Request request = Requests.generateRequestFromJson(endpoint + String.format("record-info?taskId=%s", taskID), "", false);
        request = authenticateRequest(request);
        JsonObject response = Requests.sendRequest(request);
        return response.get("msg").getAsString();
    }
}
