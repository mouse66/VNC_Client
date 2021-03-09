package ru.arseny.Inferface;

import ru.arseny.Clients.Client;
import ru.arseny.Clients.ClientConfig;
import ru.arseny.Clients.ClientConnection;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;

import static javax.swing.JOptionPane.*;

public class Dialogs {
    private static final AncestorListener focusRequester = new AncestorListener() {
        @Override
        public void ancestorAdded(AncestorEvent event) {
            event.getComponent().requestFocusInWindow();
        }

        @Override
        public void ancestorRemoved(AncestorEvent event) {
        }

        @Override
        public void ancestorMoved(AncestorEvent event) {
        }
    };
    private static JFrame frame;
    private static File mainFolder;

    public Dialogs(JFrame frame) {
        Dialogs.frame = frame;
        File home = FileSystemView.getFileSystemView().getDefaultDirectory();
        mainFolder = new File(home, "VNC Viewer");
    }

    public static Client showConnectDialog() {
        Client client = null;

        JPanel panel = new JPanel(new GridLayout(3, 0, 0, 5));

        JTextField ipField = new JTextField(15);
        ipField.setFont(InterfaceParam.getFont());
        JLabel ipLabel = new JLabel("IP-Адрес");
        ipLabel.setFont(InterfaceParam.getFont());
        ipLabel.setLabelFor(ipField);

        JTextField portField = new JTextField(8);
        portField.setFont(InterfaceParam.getFont());
        JLabel portLabel = new JLabel("Порт");
        portLabel.setFont(InterfaceParam.getFont());
        portLabel.setLabelFor(portField);

        JTextField nameField = new JTextField(15);
        nameField.setFont(InterfaceParam.getFont());
        JLabel nameLabel = new JLabel("Название");
        nameLabel.setFont(InterfaceParam.getFont());
        nameLabel.setLabelFor(nameField);

        panel.add(ipLabel);
        panel.add(ipField);
        panel.add(portLabel);
        panel.add(portField);
        panel.add(nameLabel);
        panel.add(nameField);

        int choice = showConfirmDialog(frame,
                panel, "Подключение", JOptionPane.OK_CANCEL_OPTION);

        if (choice == OK_OPTION) {
            String ip = ipField.getText();
            if (ip == null || ip.isEmpty()) {
                showMessageDialog(frame, "Некорректный IP-Адрес!");
                return null;
            }
            int port;
            try {
                port = Integer.parseInt(portField.getText());
            } catch (NumberFormatException e) {
                showMessageDialog(frame, "Некорректный порт!");
                return null;
            }

            String name = nameField.getText();
            if (name == null || name.isEmpty()) {
                name = "";
            }

            client = new Client(ip, port, name);
        }

        return client;
    }

    public static File showOpenDialog() {
        JFileChooser fileChooser = getFileChooser("Открыть конфигурацию");

        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = new File(String.valueOf(fileChooser.getSelectedFile()));
            return file;
        }

        return null;
    }

    public static void showSaveDialog() {
        JFileChooser fileChooser = getFileChooser("Сохранить конфигурацию");
        int result = fileChooser.showSaveDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = new File(fileChooser.getSelectedFile() + ".xml");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    ClientConfig.writeXmlToFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            JOptionPane.showMessageDialog(frame,
                    "Конфигурция " + file.getName() + " сохранена");
        }
    }

    public static String showPasswordDialog() {
        String pass = "";
        JPanel panel = new JPanel(new GridLayout(2, 0, 0, 5));
        JPasswordField passField = new JPasswordField(15);
        passField.setFont(InterfaceParam.getFont());
        passField.addAncestorListener(focusRequester);
        panel.add(passField);

        JCheckBox checkBox = new JCheckBox("Показать пароль");
        checkBox.setFont(InterfaceParam.getFont());

        char def = passField.getEchoChar();
        checkBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                passField.setEchoChar((char) 0);
            } else {
                passField.setEchoChar(def);
            }
        });
        panel.add(checkBox);

        int choice = showConfirmDialog(frame, panel,
                "Введите пароль", OK_CANCEL_OPTION);
        if (choice == OK_OPTION) {
            pass = new String(passField.getPassword());
        }

        ClientConnection.setPassword(pass);
        return pass;
    }

    public static JFileChooser getFileChooser(String s) {
        JFileChooser fileChooser = new JFileChooser(new File(mainFolder, "VNC Viewer"));
        fileChooser = setUpdateUI(fileChooser);
        fileChooser.setDialogTitle(s);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Файл .xml", "xml");
        fileChooser.setFileFilter(filter);
        return fileChooser;
    }

    private static JFileChooser setUpdateUI(JFileChooser chooser) {
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
        chooser.updateUI();

        return chooser;
    }
}