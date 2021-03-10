package ru.arseny.Inferface.Listners;

import ru.arseny.Clients.Client;
import ru.arseny.Clients.ClientConfig;
import ru.arseny.Clients.ClientList;
import ru.arseny.Inferface.Dialogs;
import ru.arseny.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ItemSelectListener extends Component implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        JMenuItem item = (JMenuItem) e.getSource();

        switch (item.getActionCommand()) {
            case "Подключить":
                Client client = Dialogs.showConnectDialog();
                if (client != null) {
                    Main.connect(client, false);
                }
                break;
            case "Открыть":
                File file = Dialogs.showOpenDialog();

                if (file != null) {
                    ClientList.clearMap();
                    ClientConfig.setConfig(file);
                    Main.clearTable();
                    Main.connectClientXML();
                }
                break;
            case "Сохранить":
                Dialogs.showSaveDialog();
                break;
            case "Создать":
                ClientList.clearMap();
                ClientConfig.newConfig();
                Main.clearTable();
                break;
        }
    }
}