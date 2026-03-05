package com.stolengalaxy.utils;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.AnimatedGif;
import com.sksamuel.scrimage.nio.AnimatedGifReader;
import com.sksamuel.scrimage.nio.ImageSource;
import com.sksamuel.scrimage.nio.StreamingGifWriter;
import com.sksamuel.scrimage.nio.StreamingGifWriter.GifStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;

public class GifHandling {
    public static ArrayList<String> splitGif(File gifFile){
        ArrayList<String> resultantFramesData = new ArrayList<>();
        try {
            AnimatedGif gif = AnimatedGifReader.read(ImageSource.of(gifFile));
            int frameCount = gif.getFrameCount();
            for (int i = 0; i < frameCount; i++) {
                ImmutableImage frame = gif.getFrame(i);

                String frameDelayMS = String.valueOf(gif.getDelay(i).toMillis());

                String imagePath = frameDelayMS + "_frame_" + String.format("%03d", i) + ".png";
                File outputFile = new File(imagePath);

                ImageIO.write(frame.awt(), "png", outputFile);
                resultantFramesData.add(imagePath);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to split gif", e);
        }
        return resultantFramesData;
    }

    public static void mergeIntoGif(ArrayList<String> framePaths, String gifDestinationPath){
        try{
            StreamingGifWriter writer = new StreamingGifWriter(Duration.ofMillis(20), true, false);
            GifStream gif = writer.prepareStream(gifDestinationPath, BufferedImage.TYPE_INT_ARGB);

            for (String framePath : framePaths) {
                File imageFile = new File(framePath);

                BufferedImage image = ImageIO.read(imageFile);

                long frameDelay = Long.parseLong(framePath.split("_")[0]);
                gif.writeFrame(ImmutableImage.fromAwt(image), Duration.ofMillis(frameDelay));

                imageFile.delete();
            }
            gif.close();
        } catch(IOException e){
            throw new RuntimeException("An error occurred while merging frames", e);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void resizeImage(File imageFile, int targetWidth, int targetHeight) {
        String imagePath = imageFile.getAbsolutePath();

        try{
            ImmutableImage image = ImmutableImage.fromAwt(ImageIO.read(imageFile));
            image = image.scaleTo(targetWidth, targetHeight);

            BufferedImage bufferedImage = image.awt();
            imageFile.delete();
            ImageIO.write(bufferedImage, "PNG", new File(imagePath));
        } catch (IOException e){
            throw new RuntimeException("Failed to resize image.", e);
        }

    }
}
