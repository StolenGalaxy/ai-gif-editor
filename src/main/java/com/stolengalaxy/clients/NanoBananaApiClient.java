package com.stolengalaxy.clients;

import com.google.gson.JsonObject;
import com.stolengalaxy.config.AppConfig;
import com.stolengalaxy.util.Requests;
import okhttp3.Request;

import java.util.Scanner;


public class NanoBananaApiClient {
    private static final String endpoint = "https://api.nanobananaapi.ai/api/v1/";

    private static Request authenticateRequest(Request request){
        return Requests.addHeader(request, "Authorization", AppConfig.NanoBananaAPIKey);
    }

    private static Request imageToImageRequest(String imageURL, String promptText){
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

        Request requestBody = Requests.generateRequestFromJson(endpoint + "nanobanana/generate", json, true);
        requestBody = Requests.addHeader(requestBody, "Content-Type", "application/json");

        return authenticateRequest(requestBody);
    }

    public static String imageToImageTask(String imageURL, String promptText){
        Request request = imageToImageRequest(imageURL, promptText);
        JsonObject response = Requests.sendRequestWithRetries(request);

        return response.get("data").getAsJsonObject().get("taskId").getAsString();
    }

    public static String checkGeneration(String taskID){
        Request request = Requests.generateRequestFromJson(endpoint + String.format("nanobanana/record-info?taskId=%s", taskID), "", false);
        request = authenticateRequest(request);
        JsonObject response = Requests.sendRequestWithRetries(request);

        if(!response.get("data").isJsonNull()){
            int successFlag = response.get("data").getAsJsonObject().get("successFlag").getAsInt();

            return switch(successFlag){
                case 1 -> response.get("data").getAsJsonObject().get("response").getAsJsonObject().get("resultImageUrl").getAsString();
                case 2 -> "Error: Task creation failed";
                case 3 -> "Error: Image generation failed";
                default -> "GENERATING";
            };
        } else if (response.get("code").getAsInt() == 429){
            System.err.println("Polling appears to have been rate limited. Pausing for 5 seconds.");
            try{
                Thread.sleep(5000);
            } catch (InterruptedException e){
                throw new RuntimeException("There was an error while waiting.", e);
            }

            return "GENERATING";
        } else{
            System.err.println(response);
            throw new RuntimeException("There was an error while polling generation.");
        }
    }

    public static int getRemainingCredits(){
        Request request = Requests.generateRequestFromJson(endpoint + "common/credit", "", false);
        request = authenticateRequest(request);
        JsonObject response = Requests.sendRequestWithRetries(request);

        return response.get("data").getAsInt();
    }
}
