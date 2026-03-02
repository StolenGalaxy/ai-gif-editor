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

        Request requestBody = Requests.generateRequestFromJson(endpoint + "generate", json, true);
        requestBody = Requests.addHeader(requestBody, "Content-Type", "application/json");

        return authenticateRequest(requestBody);
    }

    public static String imageToImage(String imageURL, String promptText){
        Request request = imageToImageRequest(imageURL, promptText);
        JsonObject response = Requests.sendRequest(request);

        return response.get("data").getAsJsonObject().get("taskId").getAsString();
    }

    private static String checkGeneration(String taskID){
        Request request = Requests.generateRequestFromJson(endpoint + String.format("record-info?taskId=%s", taskID), "", false);
        request = authenticateRequest(request);
        JsonObject response = Requests.sendRequest(request);

        int successFlag = response.get("data").getAsJsonObject().get("successFlag").getAsInt();

        return switch(successFlag){
            case 1 -> response.get("data").getAsJsonObject().get("response").getAsJsonObject().get("resultImageUrl").getAsString();
            case 2 -> "Error: Task creation failed";
            case 3 -> "Error: Image generation failed";
            default -> "GENERATING";
        };
    }

    public static String checkUntilOutcome(String taskID, int delay){
        String status = checkGeneration(taskID);
        while(status.contains("GENERATING")){
            status = checkGeneration(taskID);
            try{
                Thread.sleep(delay * 1000);
            } catch (InterruptedException e){
                throw new RuntimeException("There was an error while paused.", e);
            }
        }
        if(status.contains("https://")){
            return status;
        } else{
            throw new RuntimeException(status);
        }
    }
}
