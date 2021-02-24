package ru.arseny;

import ru.arseny.Clients.Client;
import ru.arseny.Clients.ClientConfig;
import ru.arseny.Clients.ClientConnect;
import ru.arseny.Inferface.Dialogs;
import ru.arseny.Inferface.InterfaceParam;
import ru.arseny.Inferface.Listners.TableClickListener;
import ru.arseny.Inferface.UserInterface;
import ru.arseny.VNC.VNCConnect;
import com.shinyhut.vernacular.client.VernacularClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

    private static ClientConnect clientConnect = null;
    private static UserInterface userInterface;
    private static Dialogs dialogs;
    private static ClientConfig config;
    private static VNCConnect connect;

    Main() {
        frame = new JFrame();
        font = InterfaceParam.getFont();
        clientConnect = new ClientConnect();
        dialogs = new Dialogs(frame);
        userInterface = new UserInterface(dialogs);
        config = new ClientConfig();
        connect = new VNCConnect(frame);

        createUI();

        connectClientXML();
    }

    public static void main(String[] args) {
        new Main();
    }

    public static void clearTable() {
        row = 0;
        column = 0;
        table.setModel(userInterface.createModel());
        clientConnect.clearMap();
    }

    //подключение к клиентам
    public static void connectClientXML() {
        ArrayList<Client> clients = config.getListClient();
        for (Client c : clients) {
            connect(c, true);
        }
    }

    //подключение к vnc
    public static void connect(Client c, boolean xml) {
        String clientIp = c.getIp();
        int clientPort = c.getPort();
        String name = c.getName();

        String key = clientIp + ":" + clientPort;
        ClientConnect.setPassword(c.getPass());
        try {
            if (!clientConnect.hasClient(key)) {
                if (column >= COLUMN_LIMIT) {
                    row += 1;
                    column = 0;
                    model.addRow(OBJECTS);
                }

                VernacularClient vnc = connect.connectVNC(row, column, clientIp, clientPort, xml);
                if (vnc.isRunning() || xml) {
                    Client client = new Client(row, column, clientIp, clientPort,
                            ClientConnect.getPassword(), name, vnc);
                    clientConnect.addClient(key, client);
                    column++;

                    if (!xml) {
                        config.addVncToXml(client);
                    }
                }
            } else {
                showMessageDialog(frame, "Данная машина уже подключена");
                return;
            }
            ClientConnect.setPassword("");
        } catch (Exception e) {
            showMessageDialog(frame, "Ошибка при подключении!");
            return;
        }
    }

    //изображение vnc
    public static void setView(Image image, int rowIndex, int colIndex) {
        table.setValueAt(image, rowIndex, colIndex);
    }

    //создание UI
    private void createUI() {
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
        createTable();

        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    //создание таблицы
    private void createTable() {
        table = userInterface.createTable();
        table.addMouseListener(new TableClickListener(table));
        clearTable();
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);
    }
}