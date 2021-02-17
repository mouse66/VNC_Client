package com.company;

import com.shinyhut.vernacular.client.VernacularClient;
import com.shinyhut.vernacular.client.VernacularConfig;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shinyhut.vernacular.client.rendering.ColorDepth.BPP_16_TRUE;
import static java.awt.Toolkit.getDefaultToolkit;
import static javax.swing.JOptionPane.*;

public class Main extends JFrame implements FormDrawer {
    final Font font = new Font("Segoe UI", Font.TRUETYPE_FONT, 12);

    JMenu menu, settingsMenu;
    JMenuBar menuBar;
    JTable table;
    DefaultTableModel model;
    FileWriter writer;
    Document document;
    File file, mainFolder;

    private int row = 0;
    private int column = 0;
    private int columnLimit = 6;
    private Object[] objects = new Object[columnLimit];
    private String password = "";
    private String name = "";
    private Map<String, Client> clientMap;
    private AncestorListener focusRequester = new AncestorListener() {
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

    Main() {
        File home = FileSystemView.getFileSystemView().getHomeDirectory();

        mainFolder = new File(home, "VNC Viewer");
        if (!mainFolder.exists()) {
            mainFolder.mkdir();
        }

        file = new File(mainFolder, "main.xml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Element clientElement = new Element("clients");
        document = new Document(clientElement);
        createUI();

        connectClientXML(file);
    }

    public static void main(String[] args) {
        new Main();
    }

    public String getPassword() {
        return password;
    }

    //создание UI
    public void createUI() {
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

        setTitle("VNC Client");
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setFont(font);

        createMenu();
        createTable();

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        clientMap = new HashMap<>();
    }

    //создание меню
    public void createMenu() {
        menu = new JMenu("Меню");
        menu.setFont(font);
        menuBar = new JMenuBar();

        JMenuItem connectItem = new JMenuItem("Подключить");
        connectItem.addActionListener(new ItemSelectListener());
        connectItem.setFont(font);
        try {
            connectItem.setIcon(new ImageIcon(ImageIO.read(new File("icons\\connect.png"))));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        menu.add(connectItem);
        menuBar.add(menu);

        settingsMenu = new JMenu("Настройки");
        settingsMenu.setFont(font);

        JMenuItem openItem = new JMenuItem("Открыть");
        openItem.addActionListener(new ItemSelectListener());
        openItem.setFont(font);
        try {
            openItem.setIcon(new ImageIcon(ImageIO.read(new File("icons\\upload.png"))));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        JMenuItem saveItem = new JMenuItem("Сохранить");
        saveItem.addActionListener(new ItemSelectListener());
        saveItem.setFont(font);
        try {
            saveItem.setIcon(new ImageIcon(ImageIO.read(new File("icons\\save.png"))));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        JMenuItem newItem = new JMenuItem("Создать");
        newItem.addActionListener(new ItemSelectListener());
        newItem.setFont(font);
        try {
            newItem.setIcon(new ImageIcon(ImageIO.read(new File("icons\\create.png"))));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        settingsMenu.add(openItem);
        settingsMenu.add(saveItem);
        settingsMenu.add(newItem);

        menuBar.add(settingsMenu);

        setJMenuBar(menuBar);
    }

    //создание таблицы
    private void createTable() {
        model = new DefaultTableModel(1, columnLimit) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setDefaultRenderer(Object.class, new ImageRender());
        table.setRowHeight(145);
        table.setShowGrid(false);
        table.setCellSelectionEnabled(false);
        table.addMouseListener(new ClickListener());
        table.setTableHeader(null);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
    }

    //диалог подключения
    private void showConnectDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 0, 0, 5));

        JTextField ipField = new JTextField(15);
        ipField.setFont(font);
        JLabel ipLabel = new JLabel("IP-Адрес");
        ipLabel.setFont(font);
        ipLabel.setLabelFor(ipField);

        JTextField portField = new JTextField(8);
        portField.setFont(font);
        JLabel portLabel = new JLabel("Порт");
        portLabel.setFont(font);
        portLabel.setLabelFor(portField);

        JTextField nameField = new JTextField(15);
        nameField.setFont(font);
        JLabel nameLabel = new JLabel("Название");
        nameLabel.setFont(font);
        nameLabel.setLabelFor(nameField);

        panel.add(ipLabel);
        panel.add(ipField);
        panel.add(portLabel);
        panel.add(portField);
        panel.add(nameLabel);
        panel.add(nameField);

        int choice = showConfirmDialog(this,
                panel, "Подключение", JOptionPane.OK_CANCEL_OPTION);

        if (choice == OK_OPTION) {
            String ip = ipField.getText();
            if (ip == null || ip.isEmpty()) {
                showMessageDialog(this, "Некорректный IP-Адрес!");
                return;
            }
            int port;
            try {
                port = Integer.parseInt(portField.getText());
            } catch (NumberFormatException e) {
                showMessageDialog(this, "Некорректный порт!");
                return;
            }

            name = nameField.getText();
            if (name == null || name.isEmpty()) {
                name = "";
            }

            connect(ip, port, false);
        }
    }

    //диалог выбора конфигурации
    private void showOpenDialog() {
        JFileChooser fileChooser = getFileChooser("Открыть конфигурацию");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = new File(String.valueOf(fileChooser.getSelectedFile()));
            System.out.println(file.getName());
        }
    }

    //диалог сохранения конфигурации
    private void showSaveDialog() {
        JFileChooser fileChooser = getFileChooser("Сохранить конфигурацию");
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = new File(String.valueOf(fileChooser.getSelectedFile()) + ".xml");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    writeXmlToFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Конфигурция " + file.getName() + " сохранена");
        }
    }

    private JFileChooser getFileChooser(String s) {
        JFileChooser fileChooser = new JFileChooser(mainFolder);
        fileChooser.setDialogTitle(s);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Файл .xml", "xml");
        fileChooser.setFileFilter(filter);
        return fileChooser;
    }

    //диалог ввода пароля
    private String showPasswordDialog() {
        String pass = "";
        JPanel panel = new JPanel(new GridLayout(2, 0, 0, 5));
        JPasswordField passField = new JPasswordField(15);
        passField.setFont(font);
        passField.addAncestorListener(focusRequester);
        panel.add(passField);

        JCheckBox checkBox = new JCheckBox("Показать пароль");
        checkBox.setFont(font);

        char def = passField.getEchoChar();
        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    passField.setEchoChar((char) 0);
                } else {
                    passField.setEchoChar(def);
                }
            }
        });
        panel.add(checkBox);

        int choice = showConfirmDialog(this, panel,
                "Введите пароль", OK_CANCEL_OPTION);
        if (choice == OK_OPTION) {
            pass = new String(passField.getPassword());
        }

        password = pass;
        return pass;
    }

    //создание конфигурации
    public VernacularConfig createConfig(int rowIndex, int colIndex, boolean xml) {
        VernacularConfig config = new VernacularConfig();
        config.setColorDepth(BPP_16_TRUE);
        if (xml) {
            config.setPasswordSupplier(this::getPassword);
        } else {
            config.setPasswordSupplier(this::showPasswordDialog);
        }
        config.setErrorListener(e -> {
            showMessageDialog(this, e.getMessage(), "Ошибка!", ERROR_MESSAGE);
        });
        config.setScreenUpdateListener(image -> addView(image, rowIndex, colIndex));
        config.setBellListener(v -> getDefaultToolkit().beep());
        config.setRemoteClipboardListener(t -> getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(t), null));

        return config;
    }

    //подключение к клиентам
    public void connectClientXML(File f) {
        try {
            SAXBuilder builder = new SAXBuilder();
            document = builder.build(f);
            Element clientElement = document.getRootElement();

            List<Element> clientList = clientElement.getChildren("client");

            for (int i = 0; i < clientList.size(); i++) {
                Element client = (Element) clientList.get(i);
                String ip = client.getChildText("ip");
                int port = Integer.parseInt(client.getChildText("port"));
                password = client.getChildText("password");
                name = client.getChildText("name");
                connect(ip, port, true);
            }

        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //подключение к vnc
    private void connect(String ip, int port, boolean xml) {
        String host = ip + ":" + port;
        try {
            if (!clientMap.containsKey(host)) {
                if (column >= columnLimit) {
                    row += 1;
                    column = 0;
                    model.addRow(objects);
                }

                if (connectVNC(row, column, ip, port, xml) || xml) {
                    Client client = new Client(row, column, ip, port, password, name);
                    clientMap.put(host, client);
                    column++;

                    if (!xml) {
                        addVncToXml(client);
                    }
                }
                password = "";
            } else {
                showMessageDialog(this, "Данная машина уже подключена");
                return;
            }
        } catch (Exception e) {
            showMessageDialog(this, "Ошибка при подключении!");
            return;
        }
    }

    //подключение к VNC
    private boolean connectVNC(int rowIndex, int colIndex, String ip, int port, boolean xml) {
        VernacularConfig config = createConfig(rowIndex, colIndex, xml);
        VernacularClient vncClient = new VernacularClient(config);
        vncClient.start(ip, port);

        boolean isRunning = vncClient.isRunning();

        if (!isRunning && xml) {
            File pathToFile = new File("not_available.jpg");
            Image image = null;
            try {
                image = ImageIO.read(pathToFile);
                addView(image, rowIndex, colIndex);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return isRunning;
    }

    //добавление VNC в файл xml
    private void addVncToXml(Client client) {
        Element clientElement = new Element("client");
        clientElement.addContent(new Element("ip")
                .addContent(client.getIp()));
        clientElement.addContent(new Element("port")
                .addContent(String.valueOf(client.getPort())));
        clientElement.addContent(new Element("password")
                .addContent(client.getPass()));
        clientElement.addContent(new Element("name")
                .addContent(client.getNameClient()));

        document.getRootElement().addContent(clientElement);

        try {
            writeXmlToFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeXmlToFile(File f) throws IOException {
        writer = new FileWriter(f, false);

        XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
        output.output(document, System.out);

        output.output(document, writer);
        writer.flush();
    }

    //изображение vnc
    public void addView(Image image, int rowIndex, int colIndex) {
        table.setValueAt(image, rowIndex, colIndex);
    }

    private Client getClient(int rowIndex, int colIndex) {
        Client client = null;
        for (Client c : clientMap.values()) {
            if (c.getColumn() == colIndex && c.getRow() == rowIndex) {
                client = c;
                break;
            }
        }
        return client;
    }

    //обрабокта нажатий на таблицу
    private class ClickListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            int col = table.columnAtPoint(e.getPoint());
            int row = table.rowAtPoint(e.getPoint());

            Client client = getClient(row, col);
            if (client != null) {
                String ip = client.getIp();
                int port = client.getPort();
                String pass = client.getPass();

                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        new Viewer(ip, port, pass, client.getNameClient());
                        break;
                    case MouseEvent.BUTTON3:
                        JPopupMenu popupMenu = new JPopupMenu();
                        JMenuItem reloadItem = new JMenuItem("Обновить");
                        reloadItem.setFont(font);
                        try {
                            reloadItem.setIcon(
                                    new ImageIcon(ImageIO.read(new File("icons\\refresh.png"))));
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        reloadItem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                password = pass;
                                connectVNC(row, col, ip, port, true);
                            }
                        });
                        popupMenu.add(reloadItem);

                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                        break;
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    //обработка нажатий в меню
    public class ItemSelectListener extends Component implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem) e.getSource();

            switch (item.getActionCommand()) {
                case "Подключить":
                    showConnectDialog();
                    break;
                case "Открыть":
                    showOpenDialog();
                    break;
                case "Сохранить":
                    showSaveDialog();
                    break;
                case "Создать":
                    break;
            }
        }
    }

    class ImageRender extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof BufferedImage) {
                Image image = (BufferedImage) value;
                ImageIcon icon = new ImageIcon(image.getScaledInstance(
                        table.getColumnModel().getColumn(column).getWidth(),
                        table.getRowHeight(), Image.SCALE_AREA_AVERAGING));

                setIcon(icon);
                setText(null);
                return this;
            }
            return null;
        }
    }
}
