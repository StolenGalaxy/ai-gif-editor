package com.stolengalaxy.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class FileDownloader {
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
}
