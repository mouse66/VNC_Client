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

public class MainView {
    static JTable table;
    private static JFrame frame;

    private static int row = 0;
    private static int column = 0;

    private static ClientList clientList;
    private static UserInterface userInterface;
    private Dialogs dialogs;
    private static ClientConfig config;
    private static VNCConnect connect;

    public MainView() {
        frame = new JFrame();
        clientList = new ClientList();
        dialogs = new Dialogs(frame);
        userInterface = new UserInterface();
        config = new ClientConfig();
        connect = new VNCConnect(frame);

        createUI();

        connectClientXML();
    }

    public void createUI() {
        userInterface.updateUIManager();
        frame.setTitle("VNC Client");
        frame.setSize(1280, 720);
        frame.setLocationRelativeTo(null);
        frame.setFont(FONT);
        frame.setJMenuBar(userInterface.createMenu());
        try {
            BufferedImage image = ImageIO.read(new FileInputStream("icons/main_icon.png"));
            frame.setIconImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        createTable();

        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

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
        ClientList.clearMap();
    }

    public static void connectClientXML() {
        ArrayList<Client> clients = config.getListClient();
        for (Client client : clients) {
            connect(client, true);
        }
    }

    public static void connect(Client client, boolean xml) {
        String clientIp = client.getIp();
        int clientPort = client.getPort();
        String name = client.getName();

        String key = clientIp + ":" + clientPort;
        ClientList.setPassword(client.getPass());
        try {
            if (!clientList.hasClient(key)) {
                if (column >= COLUMN_LIMIT) {
                    row += 1;
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    model.insertRow(row, new Object[COLUMN_LIMIT]);
                    column = 0;
                }

                VernacularClient vnc = VNCConnect.connectVNC(row, column, clientIp, clientPort, xml);
                if (vnc.isRunning() || xml) {
                    Client conClient = new Client(row, column, clientIp, clientPort,
                            ClientList.getPassword(), name, vnc);
                    clientList.addClient(key, conClient);
                    column++;

                    if (!xml) {
                        config.addVncToXml(conClient);
                    }
                }
            } else {
                showMessageDialog(frame, "Данная машина уже подключена");
            }
            ClientList.setPassword("");
        } catch (Exception e) {
            showMessageDialog(frame, "Ошибка при подключении!");
        }
    }

    public static void setView(Image image, int rowIndex, int colIndex) {
        table.setValueAt(image, rowIndex, colIndex);
    }
}