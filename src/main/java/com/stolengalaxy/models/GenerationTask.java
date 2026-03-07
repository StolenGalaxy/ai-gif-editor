package com.stolengalaxy.models;

import com.stolengalaxy.clients.NanoBananaApiClient;

public class GenerationTask {
    public int index;

    public boolean failed;
    public boolean completed;
    public int attempts;

    public String filePath;
    public String ID;
    public String finalURL;

    public GenerationTask(int index, String ID, String filePath){
        this.index = index;
        this.ID = ID;
        this.filePath = filePath;

        completed = false;
        failed = false;
        attempts = 0;
    }

    public void updateTask(){
        String currentStatus = NanoBananaApiClient.checkGeneration(ID);

        if(currentStatus.contains("https://")){
            finalURL = currentStatus;
            completed = true;
        } else if(currentStatus.contains("Error")){
            failed = true;
        }
    }
}
