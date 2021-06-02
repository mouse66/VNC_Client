package ru.vncclient.ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

/**
 * Класс для загрузки изображений из ресурсов
 */
public class ImageLoader {
    private static final ClassLoader classLoader = ImageLoader.class.getClassLoader();

    /**
     * Получить изображение из ресурсов
     * @param imageName название картинки
     * @return Image
     */
    public static Image getImage(String imageName) {
        try {
            return ImageIO.read(classLoader.getResource(imageName));
        } catch (IOException e) {
            return null;
        }
    }
}