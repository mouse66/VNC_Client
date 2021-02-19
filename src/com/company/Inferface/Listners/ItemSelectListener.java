package com.company.Inferface.Listners;

import com.company.Clients.Client;
import com.company.Clients.ClientConfig;
import com.company.Clients.ClientConnect;
import com.company.Inferface.Dialogs;
import com.company.Main;

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
                    String name = file.getName();
                    int pos = name.lastIndexOf(".");

                    ClientConfig.setConfig(file);
                    ClientConnect.stopClients();
                    Main.clearTable();
                    Main.connectClientXML();
                }
                break;
            case "Сохранить":
                Dialogs.showSaveDialog();
                break;
            case "Создать":
                ClientConnect.stopClients();
                Main.clearTable();
                break;
        }
    }
}