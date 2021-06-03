package ru.vncclient;

import ru.vncclient.clients.ClientConfig;
import ru.vncclient.clients.ClientConnector;
import ru.vncclient.clients.ClientList;
import ru.vncclient.ui.Dialogs;
import ru.vncclient.ui.ImageLoader;
import ru.vncclient.ui.UserInterface;
import ru.vncclient.vnc.ConnectParams;
import ru.vncclient.vnc.VNCConnect;

import javax.swing.*;

import static ru.vncclient.ui.InterfaceParam.FONT;

@SuppressWarnings("ALL")
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