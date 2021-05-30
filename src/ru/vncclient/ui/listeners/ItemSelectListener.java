package ru.vncclient.ui.listeners;

import ru.vncclient.clients.Client;
import ru.vncclient.clients.ClientConfig;
import ru.vncclient.clients.ClientConnector;
import ru.vncclient.clients.ClientList;
import ru.vncclient.ui.Dialogs;
import ru.vncclient.Table;
import ru.vncclient.vnc.ConnectParams;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ItemSelectListener extends Component implements ActionListener {
    /**
     * Обработка нажатий в главном меню
     * @param e ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JMenuItem item = (JMenuItem) e.getSource();

        switch (item.getActionCommand()) {
            case "Подключить":
                Client client = Dialogs.showConnectDialog();
                if (client != null) {
                    //подключение клиента
                    ClientConnector.connect(client, ConnectParams.INPUT);
                }
                break;
            case "Подключение с сервера":
                Dialogs.createConnect();
                break;
            case "Открыть":
                File file = Dialogs.showOpenDialog();

                if (file != null) {
                    //Удаление старых виртуальных машин и загрузка новых из файла
                    ClientList.clearMap();
                    ClientConfig.setConfig(file);
                    Table.clearTable();
                    ClientConnector.connectClients(ClientConfig.getListClient(), ConnectParams.XML);
                }
                break;
            case "Сохранить":
                Dialogs.showSaveDialog();
                break;
            case "Создать":
                //очистка main_config.xml
                ClientList.clearMap();
                ClientConfig.newConfig();
                Table.clearTable();
                break;
        }
    }
}