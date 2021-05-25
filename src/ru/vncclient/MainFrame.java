package ru.vncclient;

import com.shinyhut.vernacular.client.VernacularClient;
import ru.vncclient.Clients.Client;
import ru.vncclient.Clients.ClientConfig;
import ru.vncclient.Clients.ClientConnector;
import ru.vncclient.Clients.ClientList;
import ru.vncclient.Inferface.Dialogs;
import ru.vncclient.Inferface.ImageLoader;
import ru.vncclient.Inferface.UserInterface;
import ru.vncclient.VNC.ConnectParams;
import ru.vncclient.VNC.VNCConnect;

import javax.swing.*;
import java.util.ArrayList;

import static javax.swing.JFrame.EXIT_ON_CLOSE;
import static javax.swing.JOptionPane.showMessageDialog;
import static ru.vncclient.Inferface.InterfaceParam.FONT;

public class MainFrame extends JFrame {
    private static UserInterface userInterface;

    /**
     * Отрисовка главного экрана
     */
    public MainFrame() {
        new ClientList();
        new Dialogs(this);
        userInterface = new UserInterface();

        new ClientConfig();
        new VNCConnect(this);

        new ClientConnector(this);

        createUI();

        //подключение к клиентам из загруженной конфигурации main_config.xml
        ClientConnector.connectClients(ClientConfig.getListClient(), ConnectParams.XML);
    }

    /**
     * Создание интерфейса
     */
    public void createUI() {
        userInterface.updateUIManager();
        setTitle("VNC Client");
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setFont(FONT);

        setIconImage(ImageLoader.getImage("main_icon.png"));
        setJMenuBar(userInterface.createMenu());
        createTable();

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * Создать таблицу {@link Table}
     */
    public void createTable() {
        new Table();

        JScrollPane scrollPane = new JScrollPane(Table.getTable());
        add(scrollPane);
    }
}