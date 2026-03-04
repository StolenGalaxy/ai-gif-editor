package com.stolengalaxy.utils;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.AnimatedGif;
import com.sksamuel.scrimage.nio.AnimatedGifReader;
import com.sksamuel.scrimage.nio.ImageSource;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class GifHandling {
    public static void splitGif(File gifFile){
        try {
            AnimatedGif gif = AnimatedGifReader.read(ImageSource.of(gifFile));

            int frameCount = gif.getFrameCount();
            for (int i = 0; i < frameCount; i++) {
                ImmutableImage frame = gif.getFrame(i);

                File outputFile = new File("frame_" + String.format("%03d", i) + ".png");
                ImageIO.write(frame.awt(), "png", outputFile);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to split gif", e);
        }
    }
}
