package com.stolengalaxy;

import com.stolengalaxy.clients.NanoBananaApiClient;
import com.stolengalaxy.clients.TmpFilesClient;
import com.stolengalaxy.utils.FileHandling;
import com.stolengalaxy.utils.GifHandling;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static void main(){
        File selectedFile = FileHandling.selectLocalFile();

        String gifPath = selectedFile.getAbsolutePath();
        ArrayList<String> framePaths = GifHandling.splitGif(selectedFile);

        Scanner scanner = new Scanner(System.in);
        System.out.println("The selected GIF has " + framePaths.size() + " frames.");
        double estimatedPrice = framePaths.size() * 0.04;
        System.out.println("This will likely cost at least $" + estimatedPrice + ".\nEnter Y to confirm:");

        if (!scanner.nextLine().equalsIgnoreCase("Y")){
            System.out.println("Deleting frames and exiting.");

            for(String framePath : framePaths){
                FileHandling.deleteFileByPath(framePath);
            }
        } else {
            System.out.println("Enter prompt:");
            String prompt = scanner.nextLine();
            prompt = prompt + " Do NOT change the aspect ratio at all!";

            for(int i=0; i<framePaths.size(); i++){
                String framePath = framePaths.get(i);
                System.out.println("Starting " + framePath);

                String imageURL = TmpFilesClient.uploadFile(framePath);

                FileHandling.deleteFileByPath(framePath);

                String newImageURL = NanoBananaApiClient.editImage(imageURL, prompt, 1);
                FileHandling.downloadFile(newImageURL, framePath);

                System.out.println("Completed " + framePath);
            }

            FileHandling.deleteFileByPath(gifPath);
            GifHandling.mergeIntoGif(framePaths, gifPath);
            System.out.println("Completed " + gifPath);
        }
    }
}
