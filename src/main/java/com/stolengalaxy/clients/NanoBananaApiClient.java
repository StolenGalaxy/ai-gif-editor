package com.stolengalaxy.clients;

import com.stolengalaxy.config.AppConfig;
import com.stolengalaxy.utils.Requests;
import okhttp3.Request;


public class NanoBananaApiClient {
    private static final String endpoint = "https://api.nanobananaapi.ai/api/v1/nanobanana/generate";

    private static Request generateRequestFromJson(String bodyJson){
        Request request = Requests.generatePostRequestFromJson(endpoint, bodyJson);

        return Requests.addHeader(request, "Content-Type", "application/json");
    }
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
                    "imageURLs": ["%s"]
                }
                """, promptText, imageURL);

        Request requestBody = generateRequestFromJson(json);
        Request authenticatedRequest = authenticateRequest(requestBody);
        authenticatedRequest = Requests.addHeader(authenticatedRequest, "Content-Type", "application/json");
        return authenticatedRequest;
    }
}
