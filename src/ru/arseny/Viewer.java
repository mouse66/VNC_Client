package ru.arseny;

import com.shinyhut.vernacular.client.VernacularClient;
import com.shinyhut.vernacular.client.VernacularConfig;
import ru.arseny.Clients.Client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

import static com.shinyhut.vernacular.client.rendering.ColorDepth.BPP_24_TRUE;
import static java.awt.BorderLayout.CENTER;
import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;
import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR;
import static java.awt.Toolkit.getDefaultToolkit;
import static java.awt.datatransfer.DataFlavor.stringFlavor;
import static java.lang.Math.min;
import static java.lang.Thread.sleep;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

public class Viewer extends JFrame {
    private VernacularClient vnc;
    private String ip;
    private int port;
    private String password;
    private String name;
    private VernacularConfig config;
    private Image img;

    private volatile boolean shutdown = false;

    public Viewer(Client client) {
        ip = client.getIp();
        port = client.getPort();
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
        try {
            BufferedImage image = ImageIO.read(new FileInputStream("icons/main_icon.png"));
            setIconImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setTitle(name);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        setFocusable(true);
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

    private void addImage(Image image) {
        if (resizeRequired(image)) {
            resizeWindow(image);
        }
        img = image;
        repaint();
    }

    private boolean resizeRequired(Image image) {
        return img == null || img.getWidth(null) != image.getWidth(null) ||
                img.getHeight(null) != image.getHeight(null);
    }

    /**
     * Перемасшатбирование изображения
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

    public String getPassword() {
        return password;
    }
}