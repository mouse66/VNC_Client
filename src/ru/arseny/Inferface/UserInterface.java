package ru.arseny.Inferface;

import ru.arseny.Clients.Client;
import ru.arseny.Clients.ClientConfig;
import ru.arseny.Clients.ClientConnection;
import ru.arseny.Inferface.Listners.ImageRender;
import ru.arseny.Inferface.Listners.ItemSelectListener;
import ru.arseny.Main;
import ru.arseny.VNC.VNCConnect;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

public class UserInterface {
    private static Font font;

    public UserInterface() {
        font = InterfaceParam.getFont();
    }

    public static void createPopup(MouseEvent e, int colIndex, int rowIndex) {
        Client client = ClientConnection.getClient(rowIndex, colIndex);
        if (client == null) {
            return;
        }
        String ip = client.getIp();
        int port = client.getPort();
        String pass = client.getPass();

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem reloadItem = new JMenuItem("Обновить");
        reloadItem.setFont(font);
        try {
            BufferedImage image = ImageIO.read(new FileInputStream("src/icons/refresh.png"));
            reloadItem.setIcon(new ImageIcon(image));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        reloadItem.addActionListener(listener -> {
            ClientConnection.setPassword(pass);
            VNCConnect.connectVNC(rowIndex, colIndex, ip, port, true);
            ClientConnection.setPassword("");
        });
        JMenuItem deleteItem = new JMenuItem("Удалить");
        deleteItem.setFont(font);
        try {
            BufferedImage image = ImageIO.read(new FileInputStream("src/icons/delete.png"));
            deleteItem.setIcon(new ImageIcon(image));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        deleteItem.addActionListener(listener -> {
            String key = ip + ":" + port;
            ClientConnection.stopClient(key);
            ClientConnection.removeClient(key);
            ClientConfig.removeClient(ip, port);

            Main.setView(InterfaceParam.getNotAvailable(), rowIndex, colIndex);
        });

        popupMenu.add(reloadItem);
        popupMenu.add(deleteItem);
        popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    public JMenuBar createMenu() {
        ItemSelectListener listener = new ItemSelectListener();

        JMenu menu = new JMenu("Меню");
        menu.setFont(font);
        JMenuBar menuBar = new JMenuBar();

        JMenuItem connectItem = new JMenuItem("Подключить");
        connectItem.addActionListener(listener);
        connectItem.setFont(font);
        try {
            BufferedImage image = ImageIO.read(new FileInputStream("src/icons/connect.png"));
            connectItem.setIcon(new ImageIcon(image));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        menu.add(connectItem);
        menuBar.add(menu);

        JMenuItem settingsMenu = new JMenu("Настройки");
        settingsMenu.setFont(font);

        JMenuItem openItem = new JMenuItem("Открыть");
        openItem.addActionListener(listener);
        openItem.setFont(font);
        try {
            BufferedImage image = ImageIO.read(new FileInputStream("src/icons/upload.png"));
            openItem.setIcon(new ImageIcon(image));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        JMenuItem saveItem = new JMenuItem("Сохранить");
        saveItem.addActionListener(listener);
        saveItem.setFont(font);
        try {
            BufferedImage image = ImageIO.read(new FileInputStream("src/icons/save.png"));
            saveItem.setIcon(new ImageIcon(image));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        JMenuItem newItem = new JMenuItem("Создать");
        newItem.addActionListener(listener);
        newItem.setFont(font);
        try {
            BufferedImage image = ImageIO.read(new FileInputStream("src/icons/create.png"));
            newItem.setIcon(new ImageIcon(image));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        settingsMenu.add(openItem);
        settingsMenu.add(saveItem);
        settingsMenu.add(newItem);

        menuBar.add(settingsMenu);

        return menuBar;
    }

    public JTable createTable() {
        JTable table = new JTable();
        table.setModel(createModel());
        table.setDefaultRenderer(Object.class, new ImageRender());
        table.setRowHeight(145);
        table.setShowGrid(false);
        table.setCellSelectionEnabled(false);
        table.setTableHeader(null);

        return table;
    }

    public DefaultTableModel createModel() {
        DefaultTableModel model = new DefaultTableModel(1, 6) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        return model;
    }
}