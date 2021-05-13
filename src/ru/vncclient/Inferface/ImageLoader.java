package ru.vncclient.Inferface;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class ImageLoader {
    private static ClassLoader classLoader;

    public ImageLoader() {
        classLoader = getClass().getClassLoader();
    }

    public static Image getImage(String path) {
        try {
            return ImageIO.read(classLoader.getResource(path));
        } catch (IOException e) {
            return null;
        }
    }
}