package ru.arseny.Inferface;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class InterfaceParam {
    public static final Font FONT = new Font("Segoe UI", Font.TRUETYPE_FONT, 12);
    public static final File file = new File("icons/not_available.jpg");
    public static Image NOT_AVAILABLE;

    public static final int COLUMN_LIMIT = 6;

    static {
        try {
            NOT_AVAILABLE = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}