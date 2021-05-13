package ru.vncclient;

import com.shinyhut.vernacular.client.VernacularClient;
import ru.vncclient.Clients.Client;
import ru.vncclient.Clients.ClientConfig;
import ru.vncclient.Clients.ClientList;
import ru.vncclient.Inferface.Dialogs;
import ru.vncclient.Inferface.ImageLoader;
import ru.vncclient.Inferface.Listners.TableClickListener;
import ru.vncclient.Inferface.UserInterface;
import ru.vncclient.VNC.VNCConnect;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static javax.swing.JOptionPane.showMessageDialog;
import static ru.vncclient.Inferface.InterfaceParam.COLUMN_LIMIT;
import static ru.vncclient.Inferface.InterfaceParam.FONT;

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

    /**
     * Создание интерфейса
     */
    public void createUI() {
        userInterface.updateUIManager();
        frame.setTitle("VNC Client");
        frame.setSize(1280, 720);
        frame.setLocationRelativeTo(null);
        frame.setFont(FONT);

        frame.setIconImage(ImageLoader.getImage("main_icon.png"));
        frame.setJMenuBar(userInterface.createMenu());
        createTable();

        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * Создать таблицу
     */
    public void createTable() {
        table = userInterface.createTable();
        table.addMouseListener(new TableClickListener(table));
        clearTable();
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane);
    }

    /**
     * Очистить таблицу
     */
    public static void clearTable() {
        row = 0;
        column = 0;
        table.setModel(userInterface.createModel());
        ClientList.clearMap();
    }

    /**
     * Подлючение клиента из конфигурации
     */
    public static void connectClientXML() {
        ArrayList<Client> clients = config.getListClient();
        for (Client client : clients) {
            connect(client, true);
        }
    }

    /**
     * Подключение клиента
     * @param client {@link Client}
     * @param xml true - параметры из конфигурации, false - введены вручную
     */
    public static void connect(Client client, boolean xml) {
        String clientKey = client.getIp() + ":" + client.getPort();

        ClientList.setPassword(client.getPass());
        try {
            if (!clientList.hasClient(clientKey)) {
                checkColumn();
                VernacularClient vnc = VNCConnect.connectVNC(row, column, client, xml);
                if (vnc.isRunning() || xml) {
                    addClientToList(xml, client, vnc);
                }
            } else {
                showMessageDialog(frame, "Данная машина уже подключена");
            }
        } catch (Exception e) {
            showMessageDialog(frame, "Ошибка при подключении!");
        }
        ClientList.setPassword("");
    }

    /**
     * Добавляет клиента в clientList {@link ClientList}
     * @param xml true - из xml конфигурации, false - введен вручную
     * @param client клиент {@link Client}
     * @param vnc подключение VNC
     */
    private static void addClientToList(boolean xml, Client client, VernacularClient vnc) {
        String ip = client.getIp();
        int port = client.getPort();

        Client conClient = new Client(row, column, ip, port,
                ClientList.getPassword(), client.getName(), vnc);
        clientList.addClient(ip + ":" + port, conClient);
        column++;

        if (!xml) {
            config.addVncToXml(conClient);
        }
    }

    /**
     * Проверка текущей колонки
     * Если равно или больше, добавляет новый ряд
     */
    private static void checkColumn() {
        if (column >= COLUMN_LIMIT) {
            row += 1;
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.insertRow(row, new Object[COLUMN_LIMIT]);
            column = 0;
        }
    }

    /**
     * Добавить картинку в таблицу
     * @param image картинка
     * @param rowIndex строка
     * @param colIndex столбец
     */
    public static void setView(Image image, int rowIndex, int colIndex) {
        table.setValueAt(image, rowIndex, colIndex);
    }
}