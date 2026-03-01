package com.stolengalaxy.clients;

import com.google.gson.JsonObject;
import com.stolengalaxy.utils.Requests;

public class TmpFilesClient {
    private static final String endpoint = "https://tmpfiles.org/api/v1/upload";

    public static String uploadFile(String filePath){
        JsonObject responseObject =  Requests.postFile(endpoint, filePath);
        String hostedFileURL = responseObject.get("data").getAsJsonObject().get("url").getAsString();

        return getDownloadURL(hostedFileURL);
    }

    private static String getDownloadURL(String hostedFileURL){
        String[] split = hostedFileURL.split("tmpfiles.org/");
        return "https://tmpfiles.org/dl/" + split[1];
    }
}
