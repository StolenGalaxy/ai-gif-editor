package com.stolengalaxy;

import com.stolengalaxy.clients.NanoBananaApiClient;
import com.stolengalaxy.clients.TmpFilesClient;
import com.stolengalaxy.utils.FileHandling;
import com.stolengalaxy.utils.GifHandling;

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

        double pricePerFrame = 0.04;

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

        double estimatedPriceDollars = (lastFrameToEdit - firstFrameToEdit + 1) * pricePerFrame;
        double estimatedPrice = estimatedPriceDollars * 0.75;

        System.out.println((lastFrameToEdit - firstFrameToEdit + 1) + " frames will be edited.");
        System.out.println("This will likely cost at least £" + estimatedPrice + "\nEnter Y to continue:");
        String confirmation = scanner.nextLine();

        if (!confirmation.equalsIgnoreCase("Y")) {
            System.err.println("Deleting frames and exiting.");
            System.err.println(confirmation);
            for(String framePath : framesData){
                FileHandling.deleteFileByPath(framePath);
            }

        } else{
            System.out.println("Enter prompt:");
            String prompt = scanner.nextLine() + " Do NOT change the aspect ratio at all!";

            for(int i=0; i<framesData.size(); i++){
                String framePath = framesData.get(i);
                if(i <= lastFrameToEdit && i >= firstFrameToEdit){
                    System.out.println("Starting " + framePath);

                    String imageURL = TmpFilesClient.uploadFile(framePath);

                    FileHandling.deleteFileByPath(framePath);

                    String newImageURL = NanoBananaApiClient.editImage(imageURL, prompt, 1);
                    FileHandling.downloadFile(newImageURL, framePath);

                    GifHandling.resizeImage(new File(framePath), gifWidth, gifHeight);

                    System.out.println("Completed " + framePath);
                } else{
                    System.out.println("Skipping " + framePath);
                }
            }

            FileHandling.deleteFileByPath(gifPath);
            GifHandling.mergeIntoGif(framesData, gifPath);
            System.out.println("Completed " + gifPath);
        }
    }
}
