package ru.arseny;

import com.shinyhut.vernacular.client.VernacularClient;
import com.shinyhut.vernacular.client.VernacularConfig;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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
import static java.lang.Math.min;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

public class Viewer extends JFrame {
    private final VernacularClient vnc;
    private final String ip;
    private final int port;
    private final String password;
    private final String name;
    private VernacularConfig config;
    private Image img;

    public Viewer(String ip, int port, String pass, String name) {
        this.ip = ip;
        this.port = port;
        this.password = pass;
        this.name = name;

        createUI();
        createConfig();
        createView();
        createMouseListner();
        createKeyboardListener();

        vnc = new VernacularClient(config);
        vnc.start(this.ip, this.port);
    }

    public void createUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        try {
            BufferedImage image = ImageIO.read(new FileInputStream("src/icons/main_icon.png"));
            setIconImage(image);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        setTitle(name);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    //если клиент работает
    private boolean isClientWork() {
        return vnc != null && vnc.isRunning();
    }

    //слушатель движений мышки
    private void createMouseListner() {
        //нажатия мышки
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

        //движения мыши
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

        //скролл
        getContentPane().addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (isClientWork()) {
                    int scroll = e.getWheelRotation();
                    if (scroll < 0) {
                        vnc.scrollUp();
                    } else {
                        vnc.scrollDown();
                    }
                }
            }
        });
    }

    //слушатель клавиатуры
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

    //создание конфига
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

    //прорисовка изображения
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

    //отрисовка
    private void addImage(Image image) {
        if (resizeRequired(image)) {
            resizeWindow(image);
        }
        img = image;
        repaint();
    }

    //изменение размера экрана
    private boolean resizeRequired(Image image) {
        return img == null || img.getWidth(null) != image.getWidth(null) ||
                img.getHeight(null) != image.getHeight(null);
    }

    //изменить размер изображения под экран
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

    private void setWindowSize(int width, int height) {
        getContentPane().setPreferredSize(new Dimension(width, height));
        pack();
    }

    public String getPassword() {
        return password;
    }
}