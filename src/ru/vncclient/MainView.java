package ru.vncclient;

import com.shinyhut.vernacular.client.VernacularClient;
import ru.vncclient.Clients.Client;
import ru.vncclient.Clients.ClientConfig;
import ru.vncclient.Clients.ClientList;
import ru.vncclient.Inferface.Dialogs;
import ru.vncclient.Inferface.ImageLoader;
import ru.vncclient.Inferface.Listners.TableClickListener;
import ru.vncclient.Inferface.UserInterface;
import ru.vncclient.VNC.ConnectParams;
import ru.vncclient.VNC.VNCConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

        connectClients(config.getListClient(), ConnectParams.XML);
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
     * Подключение клиентов
     * @param clientList список клиентов
     * @param params {@link ConnectParams}
     */
    public static void connectClients(ArrayList<Client> clientList, ConnectParams params) {
        for (Client client : clientList) {
            connect(client, params);
        }
    }

    /**
     * Подключение клиента
     * @param client {@link Client}
     * @param params {@link ConnectParams}
     */
    public static void connect(Client client, ConnectParams params) {
        String clientKey = client.getIp() + ":" + client.getPort();

        if (clientList.hasClient(clientKey)) {
            showMessageDialog(frame, "Данная машина уже подключена");
            return;
        }

        ClientList.setPassword(client.getPass());
        checkColumn();
        client.setRow(row);
        client.setColumn(column);

        try {
            VernacularClient vnc = VNCConnect.connectVNC(row, column, client, params);
            if (vnc.isRunning() || params.equals(ConnectParams.XML) || params.equals(ConnectParams.JSON)) {
                client.setClient(vnc);
                addClientToList(params, client, vnc);
            }
        } catch (Exception e) {
            showMessageDialog(frame, "Ошибка при подключении!");
        }
        ClientList.setPassword("");
    }

    /**
     * Добавляет клиента в clientList {@link ClientList}
     * @param params {@link ConnectParams}
     * @param client клиент {@link Client}
     * @param vnc подключение VNC
     */
    private static void addClientToList(ConnectParams params, Client client, VernacularClient vnc) {
        String ip = client.getIp();
        int port = client.getPort();

        Client conClient = new Client(row, column, ip, port,
                ClientList.getPassword(), client.getName(), vnc);
        clientList.addClient(ip + ":" + port, conClient);
        column++;

        if (!params.equals(ConnectParams.XML)) {
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