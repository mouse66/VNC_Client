package ru.vncclient.Inferface;

import ru.vncclient.Clients.Client;
import ru.vncclient.Clients.ClientConfig;
import ru.vncclient.Clients.ClientList;
import ru.vncclient.Inferface.Listners.ImageRender;
import ru.vncclient.Inferface.Listners.ItemSelectListener;
import ru.vncclient.MainView;
import ru.vncclient.VNC.VNCConnect;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static ru.vncclient.Inferface.InterfaceParam.FONT;
import static ru.vncclient.Inferface.InterfaceParam.NOT_AVAILABLE;

public class UserInterface {
    public UserInterface() {
    }

    /**
     * Изменение темы и перевод строк
     */
    public void updateUIManager() {
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

        UIManager.put("FileChooser.openButtonText", "Открыть");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.lookInLabelText", "Смотреть в");
        UIManager.put("FileChooser.fileNameLabelText", "Имя файла");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Тип файла");

        UIManager.put("FileChooser.saveButtonText", "Сохранить");
        UIManager.put("FileChooser.saveButtonToolTipText", "Сохранить");
        UIManager.put("FileChooser.openButtonText", "Открыть");
        UIManager.put("FileChooser.openButtonToolTipText", "Открыть");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.cancelButtonToolTipText", "Отмена");

        UIManager.put("FileChooser.lookInLabelText", "Папка");
        UIManager.put("FileChooser.saveInLabelText", "Папка");
        UIManager.put("FileChooser.fileNameLabelText", "Имя файла");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Тип файлов");

        UIManager.put("FileChooser.upFolderToolTipText", "На один уровень вверх");
        UIManager.put("FileChooser.newFolderToolTipText", "Создание новой папки");
        UIManager.put("FileChooser.listViewButtonToolTipText", "Список");
        UIManager.put("FileChooser.detailsViewButtonToolTipText", "Таблица");
        UIManager.put("FileChooser.fileNameHeaderText", "Имя");
        UIManager.put("FileChooser.fileSizeHeaderText", "Размер");
        UIManager.put("FileChooser.fileTypeHeaderText", "Тип");
        UIManager.put("FileChooser.fileDateHeaderText", "Изменен");
        UIManager.put("FileChooser.fileAttrHeaderText", "Атрибуты");

        UIManager.put("FileChooser.acceptAllFileFilterText", "Все файлы");
    }

    /**
     * Создание pop-up меню при нажатии на активную виртуальную машину
     * @param e MouseEvent
     * @param colIndex активаный столбец
     * @param rowIndex активная строка
     */
    public static void createPopup(MouseEvent e, int colIndex, int rowIndex) {
        Client client = ClientList.getClient(rowIndex, colIndex);
        if (client == null) {
            return;
        }
        String ip = client.getIp();
        int port = client.getPort();
        String pass = client.getPass();

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem reloadItem = new JMenuItem("Обновить");
        reloadItem.setFont(FONT);
        try {
            BufferedImage image = ImageIO.read(UserInterface.class.getResourceAsStream("/resources/refresh.png"));
            reloadItem.setIcon(new ImageIcon(image));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        reloadItem.addActionListener(listener -> {
            ClientList.setPassword(pass);
            VNCConnect.connectVNC(rowIndex, colIndex, ip, port, true);
            ClientList.setPassword("");
        });

        JMenuItem deleteItem = new JMenuItem("Удалить");
        deleteItem.setFont(FONT);
        try {
            BufferedImage image = ImageIO.read(UserInterface.class.getResourceAsStream("resources/delete.png"));
            deleteItem.setIcon(new ImageIcon(image));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        deleteItem.addActionListener(listener -> {
            String key = ip + ":" + port;
            ClientList.stopClient(key);
            ClientList.removeClient(key);
            ClientConfig.removeClient(ip, port);

            MainView.setView(NOT_AVAILABLE, rowIndex, colIndex);
        });

        popupMenu.add(reloadItem);
        popupMenu.add(deleteItem);
        popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    /**
     * Создание главного меню
     * @return JMenuBar
     */
    public JMenuBar createMenu() {
        ItemSelectListener listener = new ItemSelectListener();

        JMenu menu = new JMenu("Меню");
        menu.setFont(FONT);
        JMenuBar menuBar = new JMenuBar();

        JMenuItem connectItem = new JMenuItem("Подключить");
        connectItem.addActionListener(listener);
        connectItem.setFont(FONT);
        connectItem.setIcon(new ImageIcon(ImageLoader.getImage("resources/connect.png")));

        menu.add(connectItem);
        menuBar.add(menu);

        JMenuItem settingsMenu = new JMenu("Настройки");
        settingsMenu.setFont(FONT);

        JMenuItem openItem = new JMenuItem("Открыть");
        openItem.addActionListener(listener);
        openItem.setFont(FONT);
        openItem.setIcon(new ImageIcon(ImageLoader.getImage("resources/upload.png")));

        JMenuItem saveItem = new JMenuItem("Сохранить");
        saveItem.addActionListener(listener);
        saveItem.setFont(FONT);
        saveItem.setIcon(new ImageIcon(ImageLoader.getImage("resources/save.png")));

        JMenuItem newItem = new JMenuItem("Создать");
        newItem.addActionListener(listener);
        newItem.setFont(FONT);
        newItem.setIcon(new ImageIcon(ImageLoader.getImage("resources/create.png")));

        settingsMenu.add(openItem);
        settingsMenu.add(saveItem);
        settingsMenu.add(newItem);

        menuBar.add(settingsMenu);

        return menuBar;
    }

    /**
     * Создание таблицы
     * @return JTable
     */
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

    /**
     * Создание модели таблицы
     * @return DefaultTableModel
     */
    public DefaultTableModel createModel() {
        DefaultTableModel model = new DefaultTableModel(1,
                InterfaceParam.COLUMN_LIMIT) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        return model;
    }
}