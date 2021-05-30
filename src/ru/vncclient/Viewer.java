package ru.vncclient;

import com.shinyhut.vernacular.client.VernacularClient;
import com.shinyhut.vernacular.client.VernacularConfig;
import ru.vncclient.clients.Client;
import ru.vncclient.ui.ImageLoader;
import ru.vncclient.ui.UserInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;

import static com.shinyhut.vernacular.client.rendering.ColorDepth.BPP_24_TRUE;
import static java.awt.BorderLayout.CENTER;
import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;
import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR;
import static java.awt.Toolkit.getDefaultToolkit;
import static java.lang.Math.min;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static ru.vncclient.ui.InterfaceParam.FONT;

public class Viewer extends JFrame {
    private final VernacularClient vnc;
    private final String password;
    private final String name;
    private VernacularConfig config;
    private Image img;

    public Viewer(Client client) {
        String ip = client.getIp();
        int port = client.getPort();
        password = client.getPass();
        name = client.getNameClient();

        createUI();
        createConfig();
        createView();
        createMouseListener();
        createKeyboardListener();

        vnc = new VernacularClient(config);
        vnc.start(ip, port);
    }

    /**
     * Создание интерфейса
     */
    private void createUI() {
        setTitle(name);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        setFocusable(true);
        setIconImage(ImageLoader.getImage("main_icon.png"));
        createMenu();
    }

    /**
     * Создание меню управление виртуальной машиной
     */
    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Управление");
        menu.setFont(FONT);

        JMenuItem reloadMenu = UserInterface.createItem(listener -> {
            try {
                //нажатие Ctrl+Alt+Del на виртуальной машине
                Robot robot = new Robot();
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_ALT);
                robot.keyPress(KeyEvent.VK_DELETE);

                robot.keyRelease(KeyEvent.VK_CONTROL);
                robot.keyRelease(KeyEvent.VK_ALT);
                robot.keyRelease(KeyEvent.VK_DELETE);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }, "Ctrl + Alt + Del", "params.png");
        menu.add(reloadMenu);

        JMenuItem sendMenu = UserInterface.createItem(listener -> {
            //TODO: написать ввод с буфера обмена/клавиатуры
        }, "Вставить текст", "clipboard.png");
        menu.add(sendMenu);

        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    /**
     * Работа клиента в данный момент
     * @return true - активен, false - нет
     */
    private boolean isClientWork() {
        return vnc != null && vnc.isRunning();
    }

    /**
     * Обработка нажатий мыши
     */
    private void createMouseListener() {
        getContentPane().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isClientWork()) {
                    vnc.updateMouseButton(e.getButton(), true);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isClientWork()) {
                    vnc.updateMouseButton(e.getButton(), false);
                }
            }
        });

        getContentPane().addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (isClientWork()) {
                    vnc.moveMouse(e.getX(), e.getY());
                }
            }
        });

        getContentPane().addMouseWheelListener(listener -> {
            if (isClientWork()) {
                int scroll = listener.getWheelRotation();
                if (scroll < 0) {
                    vnc.scrollUp();
                } else {
                    vnc.scrollDown();
                }
            }
        });
    }

    /**
     * Обработчик клавиатуры
     */
    private void createKeyboardListener() {
        setFocusTraversalKeysEnabled(false);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (isClientWork()) {
                    vnc.handleKeyEvent(e);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (isClientWork()) {
                    vnc.handleKeyEvent(e);
                }
            }
        });
    }

    /**
     * Создание конфигурации подключения
     */
    private void createConfig() {
        config = new VernacularConfig();

        config.setColorDepth(BPP_24_TRUE);
        config.setErrorListener(e -> {
            showMessageDialog(this, e.getMessage(), "Ошибка!", ERROR_MESSAGE);
        });
        config.setPasswordSupplier(this::getPassword);
        config.setScreenUpdateListener(image -> addImage(image));
        config.setBellListener(v -> getDefaultToolkit().beep());
        config.setRemoteClipboardListener(t -> getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(t), null));
    }

    /**
     * Отрисовка изображения
     */
    private void createView() {
        add(new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (img != null) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
                    g2.drawImage(img, 0, 0, getContentPane().getWidth(),
                            getContentPane().getHeight(), null);
                }
            }
        }, CENTER);
    }

    /**
     * Загрузка текущего изображения в JFrame
     * @param image текущее изображение
     */
    private void addImage(Image image) {
        if (resizeRequired(image)) {
            //перемасштабирование окна
            resizeWindow(image);
        }
        img = image;
        repaint();
    }

    /**
     * Необходимость перерисовки изображения
     * @param image текущее изображение
     * @return true - да, false - нет
     */
    private boolean resizeRequired(Image image) {
        return img == null || img.getWidth(null) != image.getWidth(null) ||
                img.getHeight(null) != image.getHeight(null);
    }

    /**
     * Перемасшатбирование окна
     * @param image текущее изображение
     */
    private void resizeWindow(Image image) {
        int remoteWidth = image.getWidth(null);
        int remoteHeight = image.getHeight(null);
        Rectangle screenSize = getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int paddingTop = getHeight() - getContentPane().getHeight();
        int paddingSides = getWidth() - getContentPane().getWidth();
        int maxWidth = (int) screenSize.getWidth() - paddingSides;
        int maxHeight = (int) screenSize.getHeight() - paddingTop;
        if (remoteWidth <= maxWidth && remoteHeight < maxHeight) {
            setWindowSize(remoteWidth, remoteHeight);
        } else {
            double scale = min((double) maxWidth / remoteWidth, (double) maxHeight / remoteHeight);
            int scaledWidth = (int) (remoteWidth * scale);
            int scaledHeight = (int) (remoteHeight * scale);
            setWindowSize(scaledWidth, scaledHeight);
        }
        setLocationRelativeTo(null);
    }

    /**
     * Размер экрана
     * @param width ширина
     * @param height высота
     */
    private void setWindowSize(int width, int height) {
        getContentPane().setPreferredSize(new Dimension(width, height));
        pack();
    }

    /**
     * Пароль виртуальной машины
     * @return password
     */
    public String getPassword() {
        return password;
    }
}