package ru.arseny;

import com.shinyhut.vernacular.client.VernacularClient;
import ru.arseny.Clients.Client;
import ru.arseny.Clients.ClientConfig;
import ru.arseny.Clients.ClientConnection;
import ru.arseny.Inferface.Dialogs;
import ru.arseny.Inferface.InterfaceParam;
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

public class Main {
    private static final int COLUMN_LIMIT = 6;
    private static final Object[] OBJECTS = new Object[COLUMN_LIMIT];

    static JTable table;
    static DefaultTableModel model;
    private static JFrame frame;
    private static Font font;

    private static int row = 0;
    private static int column = 0;

    private static ClientConnection clientConnection = null;
    private static UserInterface userInterface;
    private Dialogs dialogs;
    private static ClientConfig config;
    private static VNCConnect connect;

    Main() {
        frame = new JFrame();
        font = InterfaceParam.getFont();
        clientConnection = new ClientConnection();
        dialogs = new Dialogs(frame);
        userInterface = new UserInterface();
        config = new ClientConfig();
        connect = new VNCConnect(frame);

        createUI();

        connectClientXML();
    }

    public static void main(String[] args) {
        new Main();
    }

    //создание UI
    public void createUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        frame.setTitle("VNC Client");
        frame.setSize(1280, 720);
        frame.setLocationRelativeTo(null);
        frame.setFont(font);
        frame.setJMenuBar(userInterface.createMenu());
        try {
            BufferedImage image = ImageIO.read(new FileInputStream("src/icons/main_icon.png"));
            frame.setIconImage(image);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        createTable();

        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    //создание таблицы
    public void createTable() {
        table = userInterface.createTable();
        table.addMouseListener(new TableClickListener(table));
        clearTable();
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);
    }

    public static void clearTable() {
        row = 0;
        column = 0;
        table.setModel(userInterface.createModel());
        ClientConnection.clearMap();
    }

    //подключение к клиентам
    public static void connectClientXML() {
        ArrayList<Client> clients = config.getListClient();
        for (Client client : clients) {
            connect(client, true);
        }
    }

    //подключение к vnc
    public static void connect(Client c, boolean xml) {
        String clientIp = c.getIp();
        int clientPort = c.getPort();
        String name = c.getName();

        String key = clientIp + ":" + clientPort;
        ClientConnection.setPassword(c.getPass());
        try {
            if (!clientConnection.hasClient(key)) {
                if (column >= COLUMN_LIMIT) {
                    row += 1;
                    column = 0;
                    model.addRow(OBJECTS);
                }

                VernacularClient vnc = VNCConnect.connectVNC(row, column, clientIp, clientPort, xml);
                if (vnc.isRunning() || xml) {
                    Client client = new Client(row, column, clientIp, clientPort,
                            ClientConnection.getPassword(), name, vnc);
                    clientConnection.addClient(key, client);
                    column++;

                    if (!xml) {
                        config.addVncToXml(client);
                    }
                }
            } else {
                showMessageDialog(frame, "Данная машина уже подключена");
            }
            ClientConnection.setPassword("");
        } catch (Exception e) {
            showMessageDialog(frame, "Ошибка при подключении!");
        }
    }

    //изображение vnc
    public static void setView(Image image, int rowIndex, int colIndex) {
        table.setValueAt(image, rowIndex, colIndex);
    }
}