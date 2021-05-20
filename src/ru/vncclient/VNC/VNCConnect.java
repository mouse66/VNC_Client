package ru.vncclient.VNC;

import ru.vncclient.Clients.Client;
import ru.vncclient.Clients.ClientList;
import ru.vncclient.Inferface.Dialogs;
import com.shinyhut.vernacular.client.VernacularClient;
import com.shinyhut.vernacular.client.VernacularConfig;
import ru.vncclient.MainView;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;

import static com.shinyhut.vernacular.client.rendering.ColorDepth.*;
import static java.awt.Toolkit.getDefaultToolkit;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static ru.vncclient.Inferface.InterfaceParam.NOT_AVAILABLE;

public class VNCConnect {
    private static JFrame frame;

    public VNCConnect(JFrame frame) {
        this.frame = frame;
    }

    /**
     * Создание конфигурации вирт. машины
     * @param rowIndex строка
     * @param colIndex столбец
     * @param params {@link ConnectParams}
     * @return VernacularConfig
     */
    public static VernacularConfig createConfig(int rowIndex, int colIndex, ConnectParams params) {
        VernacularConfig config = new VernacularConfig();
        config.setColorDepth(BPP_16_TRUE);
        if (params.equals(ConnectParams.XML) || params.equals(ConnectParams.JSON)) {
            config.setPasswordSupplier(ClientList::getPassword);
        } else {
            config.setPasswordSupplier(Dialogs::showPasswordDialog);
        }
        config.setErrorListener(e -> {
            showMessageDialog(frame, e.getMessage(), "Ошибка!", ERROR_MESSAGE);
        });
        config.setScreenUpdateListener(image ->
                MainView.setView(image, rowIndex, colIndex));
        config.setBellListener(v ->
                getDefaultToolkit().beep());
        config.setRemoteClipboardListener(t ->
                getDefaultToolkit()
                        .getSystemClipboard()
                        .setContents(new StringSelection(t), null));

        return config;
    }

    /**
     * Подкючение к вирт. машине
     * @param rowIndex строка
     * @param colIndex столбец
     * @param client клиент {@link Client}
     * @param params {@link ConnectParams}
     * @return VernacularClient
     */
    public static VernacularClient connectVNC(int rowIndex, int colIndex, Client client, ConnectParams params) {
        String ip = client.getIp();
        int port = client.getPort();

        VernacularConfig config = createConfig(rowIndex, colIndex, params);
        VernacularClient vncClient = new VernacularClient(config);
        vncClient.start(ip, port);

        boolean isRunning = vncClient.isRunning();

        if (!isRunning && (params.equals(ConnectParams.XML) || params.equals(ConnectParams.JSON))) {
            MainView.setView(NOT_AVAILABLE, rowIndex, colIndex);
        }

        return vncClient;
    }
}