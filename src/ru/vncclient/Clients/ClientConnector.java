package ru.vncclient.Clients;

import com.shinyhut.vernacular.client.VernacularClient;
import ru.vncclient.Table;
import ru.vncclient.VNC.ConnectParams;
import ru.vncclient.VNC.VNCConnect;

import javax.swing.*;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showMessageDialog;

public class ClientConnector {
    public static JFrame frame;

    /**
     * Подлкючение к клиенту / клиентам
     * @param frame JFrame
     */
    public ClientConnector(JFrame frame) {
        ClientConnector.frame = frame;
    }

    /**
     * Подключение клиентов
     * @param clientList список клиентов
     * @param params параметры подключения {@link ConnectParams}
     */
    public static void connectClients(ArrayList<Client> clientList, ConnectParams params) {
        for (Client client : clientList) {
            connect(client, params);
        }
    }

    /**
     * Подключение клиента
     * @param client {@link Client}
     * @param params {@link ConnectParams}
     */
    public static void connect(Client client, ConnectParams params) {
        String clientKey = client.getIp() + ":" + client.getPort();

        if (ClientList.hasClient(clientKey)) {
            showMessageDialog(frame, "Данная машина уже подключена");
            return;
        }

        ClientList.setPassword(client.getPass());
        Table.checkColumn();

        client.setRow(Table.getRow());
        client.setColumn(Table.getColumn());

        try {
            VernacularClient vnc = VNCConnect.connectVNC(
                    client.getRow(), client.getColumn(), client, params);

            if (vnc.isRunning() ||
                    params.equals(ConnectParams.XML) || params.equals(ConnectParams.JSON)) {
                client.setClient(vnc);
                addClientToList(params, client, vnc);
            }
        } catch (Exception e) {
            showMessageDialog(frame, "Ошибка при подключении!");
        }
        ClientList.setPassword("");
    }

    /**
     * Добавляет клиента в clientList {@link ClientList}
     * @param params {@link ConnectParams}
     * @param client клиент {@link Client}
     * @param vnc подключение VNC
     */
    private static void addClientToList(ConnectParams params, Client client, VernacularClient vnc) {
        String ip = client.getIp();
        int port = client.getPort();

        Client conClient = new Client(client.getRow(), client.getColumn(),
                ip, port, ClientList.getPassword(), client.getName(), vnc);
        ClientList.addClient(ip + ":" + port, conClient);

        Table.enlargeColumn();
        if (!params.equals(ConnectParams.XML)) {
            ClientConfig.addVncToXml(conClient);
        }
    }
}