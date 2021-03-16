package ru.arseny.Inferface;

import ru.arseny.Clients.Client;
import ru.arseny.Clients.ClientConfig;
import ru.arseny.Clients.ClientList;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;

import static javax.swing.JOptionPane.*;
import static ru.arseny.Inferface.InterfaceParam.FONT;

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
        this.frame = frame;
        File home = FileSystemView.getFileSystemView().getDefaultDirectory();
        mainFolder = new File(home, "VNC Viewer");
    }

    public static Client showConnectDialog() {
        Client client = null;

        JPanel panel = new JPanel(new GridLayout(3, 0, 0, 5));

        JTextField ipField = new JTextField(15);
        ipField.setFont(FONT);
        JLabel ipLabel = new JLabel("IP-Адрес");
        ipLabel.setFont(FONT);
        ipLabel.setLabelFor(ipField);

        JTextField portField = new JTextField(8);
        portField.setFont(FONT);
        JLabel portLabel = new JLabel("Порт");
        portLabel.setFont(FONT);
        portLabel.setLabelFor(portField);

        JTextField nameField = new JTextField(15);
        nameField.setFont(FONT);
        JLabel nameLabel = new JLabel("Название");
        nameLabel.setFont(FONT);
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
        passField.setFont(FONT);
        passField.addAncestorListener(focusRequester);
        panel.add(passField);

        JCheckBox checkBox = new JCheckBox("Показать пароль");
        checkBox.setFont(FONT);

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

        ClientList.setPassword(pass);
        return pass;
    }

    public static JFileChooser getFileChooser(String s) {
        JFileChooser fileChooser = new JFileChooser(new File(mainFolder, "VNC Viewer"));
        fileChooser.setDialogTitle(s);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Файл .xml", "xml");
        fileChooser.setFileFilter(filter);
        return fileChooser;
    }
}