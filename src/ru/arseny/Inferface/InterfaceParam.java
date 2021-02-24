package ru.arseny.Inferface;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class InterfaceParam {
    private static final Font FONT = new Font("Segoe UI", Font.TRUETYPE_FONT, 12);
    private static final File file = new File("not_available.jpg");
    private static Image notAvailable;

    static {
        try {
            notAvailable = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Font getFont() {
        return FONT;
    }

    public static Image getNotAvailable() {
        return notAvailable;
    }
}