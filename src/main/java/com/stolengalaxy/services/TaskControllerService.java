package com.stolengalaxy.services;
import com.stolengalaxy.clients.NanoBananaApiClient;
import com.stolengalaxy.clients.TmpFilesClient;
import com.stolengalaxy.models.GenerationTask;
import com.stolengalaxy.util.FileHandling;
import com.stolengalaxy.util.GifHandling;

import java.io.File;
import java.util.ArrayList;

public class TaskControllerService {
    private static ArrayList<GenerationTask> currentTasks = new ArrayList<>();
    private static ArrayList<GenerationTask> completedTasks = new ArrayList<>();

    public static void logGenerationTask(int index, String ID, String framePath){
        System.out.println("Starting task " + framePath);
        GenerationTask generationTask = new GenerationTask(index, ID, framePath);

        currentTasks.add(generationTask);
    }

    public static boolean gatherGenerations(){
        boolean allTasksCompleted = true;
        for(GenerationTask task:currentTasks){
            task.updateTask();
            if(task.completed && !task.failed){
                FileHandling.downloadFile(task.finalURL, task.filePath);
                completedTasks.add(task);
                System.out.println("Completed task " + task.filePath);
            } else if (!task.failed) {
                allTasksCompleted = false;
            } else if(task.attempts < 2){
                System.out.println("Task " + task.filePath + " failed. Retrying.");
                // TODO: Retry task on failure

                //temp
                throw new RuntimeException("Task failed");
            } else{
                throw new RuntimeException("Task " + task.filePath + " failed repeatedly.");
            }
        }
        return allTasksCompleted;
    }

    public static ArrayList<GenerationTask> generate(ArrayList<String> framesData, String prompt, int simultaneousTasks){
        int highestStartedTask = -1;
        while (highestStartedTask < framesData.size() - 1){
            for(int i = 0; i < simultaneousTasks; i++){
                if(highestStartedTask < framesData.size() - 1){
                    highestStartedTask++;
                    String framePath = framesData.get(highestStartedTask);

                    String tempURL = TmpFilesClient.uploadFile(framePath);
                    FileHandling.deleteFileByPath(framePath);

                    String ID = NanoBananaApiClient.imageToImageTask(tempURL, prompt);
                    logGenerationTask(i, ID, framePath);
                }

            }
            while(!gatherGenerations()){
                try{
                    Thread.sleep(5);
                } catch(InterruptedException e){
                    throw new RuntimeException("An error occurred while sleeping", e);
                }
            }
            currentTasks.clear();
        }
        return completedTasks;
    }

    public static void generateAndDownload(ArrayList<String> editableFrames, String prompt, int simultaneousTasks, int gifWidth, int gifHeight){
        ArrayList<GenerationTask> completedTasks = TaskControllerService.generate(editableFrames, prompt, simultaneousTasks);

        for(GenerationTask task:completedTasks){
            FileHandling.downloadFile(task.finalURL, task.filePath);
            GifHandling.resizeImage(new File(task.filePath), gifWidth, gifHeight);
        }

    }

}
