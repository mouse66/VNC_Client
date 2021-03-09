package ru.arseny.VNC;

import ru.arseny.Clients.ClientConnection;
import ru.arseny.Inferface.Dialogs;
import ru.arseny.Inferface.InterfaceParam;
import ru.arseny.Main;
import com.shinyhut.vernacular.client.VernacularClient;
import com.shinyhut.vernacular.client.VernacularConfig;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;

import static com.shinyhut.vernacular.client.rendering.ColorDepth.BPP_16_TRUE;
import static java.awt.Toolkit.getDefaultToolkit;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

public class VNCConnect {
    private static JFrame frame;

    public VNCConnect(JFrame frame) {
        VNCConnect.frame = frame;
    }

    public static VernacularConfig createConfig(int rowIndex, int colIndex, boolean xml) {
        VernacularConfig config = new VernacularConfig();
        config.setColorDepth(BPP_16_TRUE);
        if (xml) {
            config.setPasswordSupplier(ClientConnection::getPassword);
        } else {
            config.setPasswordSupplier(Dialogs::showPasswordDialog);
        }
        config.setErrorListener(e -> {
            showMessageDialog(frame, e.getMessage(), "Ошибка!", ERROR_MESSAGE);
        });
        config.setScreenUpdateListener(image -> Main.setView(image, rowIndex, colIndex));
        config.setBellListener(v -> getDefaultToolkit().beep());
        config.setRemoteClipboardListener(t -> getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(t), null));

        return config;
    }

    public static VernacularClient connectVNC(int rowIndex, int colIndex, String ip, int port, boolean xml) {
        VernacularConfig config = createConfig(rowIndex, colIndex, xml);
        VernacularClient vncClient = new VernacularClient(config);
        vncClient.start(ip, port);

        boolean isRunning = vncClient.isRunning();

        if (!isRunning && xml) {
            Main.setView(InterfaceParam.getNotAvailable(), rowIndex, colIndex);
        }

        return vncClient;
    }
}