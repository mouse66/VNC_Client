package ru.vncclient.Inferface.Listners;

import ru.vncclient.Clients.Client;
import ru.vncclient.Clients.ClientConfig;
import ru.vncclient.Clients.ClientList;
import ru.vncclient.Inferface.Dialogs;
import ru.vncclient.MainView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ItemSelectListener extends Component implements ActionListener {
    /**
     * Обработка нажатий в главном меню
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JMenuItem item = (JMenuItem) e.getSource();

        switch (item.getActionCommand()) {
            case "Подключить":
                Client client = Dialogs.showConnectDialog();
                if (client != null) {
                    MainView.connect(client, false);
                }
                break;
            case "Открыть":
                File file = Dialogs.showOpenDialog();

                if (file != null) {
                    ClientList.clearMap();
                    ClientConfig.setConfig(file);
                    MainView.clearTable();
                    MainView.connectClientXML();
                }
                break;
            case "Сохранить":
                Dialogs.showSaveDialog();
                break;
            case "Создать":
                ClientList.clearMap();
                ClientConfig.newConfig();
                MainView.clearTable();
                break;
        }
    }
}