package ru.arseny;

import com.shinyhut.vernacular.client.VernacularClient;
import ru.arseny.Clients.Client;
import ru.arseny.Clients.ClientConfig;
import ru.arseny.Clients.ClientList;
import ru.arseny.Inferface.Dialogs;
import ru.arseny.Inferface.Listners.TableClickListener;
import ru.arseny.Inferface.UserInterface;
import ru.arseny.VNC.VNCConnect;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static javax.swing.JOptionPane.showMessageDialog;
import static ru.arseny.Inferface.InterfaceParam.COLUMN_LIMIT;
import static ru.arseny.Inferface.InterfaceParam.FONT;

public class Main {
    public static void main(String[] args) {
        new MainView();
    }
}