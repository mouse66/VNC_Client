package ru.vncclient.Inferface;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ImageLoader {
    private static ClassLoader classLoader;

    public ImageLoader() {
        classLoader = getClass().getClassLoader();
    }

    public static Image getImage(String path) {
        URL url = classLoader.getResource(path);
        return new ImageIcon("/" + url).getImage();
    }
}