package com.stolengalaxy.utils;

import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class FileHandling {
    public static void downloadFile(String fileURL, String fileDestination){
        File file = new File(fileDestination);

        URL url;
        try{
            URI uri = new URI(fileURL);
            url = uri.toURL();
        } catch (MalformedURLException e){
            throw new RuntimeException("The file URL is not valid", e);
        } catch (URISyntaxException e){
            throw new RuntimeException("There was an issue with the URI syntax when download the file", e);
        }

        try{
            FileUtils.copyURLToFile(url, file);
        } catch (IOException e){
            throw new RuntimeException("There was an issue while downloading the file from the URL", e);
        }

    }

    public static File selectLocalFile(){
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("GIF only", "gif");
        chooser.setFileFilter(filter);

        int returnVal = chooser.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION){
            System.out.printf("File '%s' selected%n", chooser.getSelectedFile().getName());
            return chooser.getSelectedFile();
        } else{
            throw new RuntimeException("No file selected");
        }
    }
}
