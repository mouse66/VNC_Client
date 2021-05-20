package ru.vncclient.Inferface;

import ru.vncclient.Clients.Client;
import ru.vncclient.Clients.ClientConfig;
import ru.vncclient.Clients.ClientList;
import ru.vncclient.Inferface.Listners.ItemSelectListener;
import ru.vncclient.TableView;
import ru.vncclient.VNC.ConnectParams;
import ru.vncclient.VNC.VNCConnect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

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

        JMenuItem updateItem = createItem(listener -> {
            ClientList.setPassword(pass);
            VNCConnect.connectVNC(rowIndex, colIndex, client, ConnectParams.XML);
            ClientList.setPassword("");
        }, "Обновить", "refresh.png");

        JMenuItem deleteItem = createItem(listener -> {
            String key = ip + ":" + port;
            ClientList.stopClient(key);
            ClientList.removeClient(key);
            ClientConfig.removeClient(ip, port);

            //MainView.setView(NOT_AVAILABLE, rowIndex, colIndex);
            TableView.setView(NOT_AVAILABLE, rowIndex, colIndex);
        }, "Удалить", "delete.png");


        popupMenu.add(updateItem);
        popupMenu.add(deleteItem);
        popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    /**
     * Создание главного меню
     * @return JMenuBar
     */
    public JMenuBar createMenu() {
        ItemSelectListener listener = new ItemSelectListener();

        JMenu menu = new JMenu("Подключение");
        menu.setFont(FONT);
        JMenuBar menuBar = new JMenuBar();

        menu.add(createItem(listener, "Подключить", "connect.png"));
        menu.add(createItem(listener, "Подключение с сервера", "server.png"));
        menuBar.add(menu);

        JMenu settingsMenu = new JMenu("Настройки");
        settingsMenu.setFont(FONT);

        settingsMenu.add(createItem(listener, "Открыть", "upload.png"));
        settingsMenu.add(createItem(listener, "Сохранить", "save.png"));
        settingsMenu.add(createItem(listener, "Создать", "create.png"));

        menuBar.add(settingsMenu);

        return menuBar;
    }

    /**
     * Создание кнопки
     * @param listener ActionListener
     * @param name название кнопки
     * @param picPath путь к картинке
     * @return JMenuItem
     */
    public static JMenuItem createItem(ActionListener listener, String name, String picPath) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(listener);
        menuItem.setFont(FONT);
        try {
            ImageIcon icon = new ImageIcon(ImageLoader.getImage(picPath));
            menuItem.setIcon(icon);
        } catch (Exception ignored) {
        }

        return menuItem;
    }

    /**
     * Создание таблицы
     *
     * @return JTable
     */
    public JTable createTable() {
        JTable table = new JTable();
        table.setModel(createModel());
        table.setDefaultRenderer(Object.class, new ImageRender());
        int height = (800 / InterfaceParam.COLUMN_LIMIT) + (10 + InterfaceParam.COLUMN_LIMIT);
        table.setRowHeight(height);
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