package com.stolengalaxy;

import com.stolengalaxy.clients.NanoBananaApiClient;
import com.stolengalaxy.services.TaskControllerService;
import com.stolengalaxy.util.FileHandling;
import com.stolengalaxy.util.GifHandling;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static void main(){
        Scanner scanner = new Scanner(System.in);

        File selectedFile = FileHandling.selectLocalFile();

        String gifPath = selectedFile.getAbsolutePath();
        ArrayList<String> framesData = GifHandling.splitGif(selectedFile);

        int gifWidth;
        int gifHeight;

        double pricePerCredit = 0.00451;
        int creditsPerFrame = 8;

        try{
            File firstFrameFile = new File(framesData.getFirst());
            BufferedImage firstFrame = ImageIO.read(firstFrameFile);

            gifWidth = firstFrame.getWidth();
            gifHeight = firstFrame.getHeight();

        } catch (IOException e){
            throw new RuntimeException("Failed to get dimensions of first frame.", e);
        }


        int firstFrameToEdit = -1;
        int lastFrameToEdit = framesData.size();
        System.out.println("To only edit certain frames, enter Y, otherwise press enter:");
        if (scanner.nextLine().equalsIgnoreCase("Y")){

            while (firstFrameToEdit < 0 || firstFrameToEdit >= framesData.size()){
                System.out.println("Enter index of the first frame to be edited (0-" + (framesData.size() - 1) + "):");
                firstFrameToEdit = scanner.nextInt();
                scanner.nextLine();
            }

            while(lastFrameToEdit >= framesData.size() || lastFrameToEdit < firstFrameToEdit){
                System.out.println("Enter index of the last frame to be edited (" + (firstFrameToEdit + 1) + "-" + (framesData.size() - 1) + "):");
                lastFrameToEdit = scanner.nextInt();
                scanner.nextLine();
            }
        } else{
            firstFrameToEdit = 0;
            lastFrameToEdit = framesData.size() - 1;
        }

        int numberOfFramesToEdit = lastFrameToEdit - firstFrameToEdit + 1;

        double estimatedPrice = numberOfFramesToEdit * creditsPerFrame * pricePerCredit;
        estimatedPrice = (double) Math.round(estimatedPrice * 100) / 100;

        System.out.println("You have " + NanoBananaApiClient.getRemainingCredits() + " credits remaining.");
        System.out.println(numberOfFramesToEdit + " frames will be edited.");
        System.out.println("This will cost at least " + numberOfFramesToEdit * creditsPerFrame + " credits (£" + estimatedPrice + ").\nEnter Y to continue:");

        if (!scanner.nextLine().equalsIgnoreCase("Y")) {
            System.err.println("Deleting frames and exiting.");
            for(String framePath : framesData){
                FileHandling.deleteFileByPath(framePath);
            }

        } else{
            System.out.println("Enter prompt:");
            String prompt = scanner.nextLine() + " Do NOT change the aspect ratio at all!";

            ArrayList<String> editableFrames = new ArrayList<>();
            for(int i=0; i<framesData.size(); i++){
                String framePath = framesData.get(i);
                if(i <= lastFrameToEdit && i >= firstFrameToEdit){
                    editableFrames.add(framePath);
                } else{
                    System.out.println("Skipping " + framePath);
                }
            }

            TaskControllerService.generateAndDownload(editableFrames, prompt, 3, gifWidth, gifHeight);
            FileHandling.deleteFileByPath(gifPath);
            GifHandling.mergeIntoGif(framesData, gifPath);
            System.out.println("Completed " + gifPath);
        }
    }
}
