package ru.arseny.Clients;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientConfig {
    private static File mainFolder;
    private static File config;
    private static FileWriter writer;
    private static Document document;
    private static ArrayList<Client> clients;

    public ClientConfig() {
        File home = FileSystemView.getFileSystemView().getHomeDirectory();

        mainFolder = new File(home, "VNC Viewer");
        if (!mainFolder.exists()) {
            mainFolder.mkdir();
        }

        createNewConfig();
    }

    public static void writeXmlToFile(File f) throws IOException {
        writer = new FileWriter(f, false);

        XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
        output.output(document, System.out);

        output.output(document, writer);
        writer.flush();
    }

    public static void createNewConfig() {
        config = new File(mainFolder, "main_config.xml");
        if (!config.exists()) {
            try {
                config.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Element clientElement = new Element("clients");
        document = new Document(clientElement);
        createList();
    }

    public static void newConfig() {
        config = new File(mainFolder, "main_config.xml");
        if (config.exists()) {
            config.delete();
        }
        createNewConfig();
    }

    private static void createList() {
        clients = new ArrayList<>();

        try {
            SAXBuilder builder = new SAXBuilder();
            document = builder.build(config);
            Element clientElement = document.getRootElement();

            List<Element> clientList = clientElement.getChildren("client");

            for (int i = 0; i < clientList.size(); i++) {
                Element client = clientList.get(i);
                String ip = client.getChildText("ip");
                int port = Integer.parseInt(client.getChildText("port"));
                String pass = client.getChildText("password");
                String name = client.getChildText("name");
                clients.add(new Client(ip, port, pass, name));
            }

        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setConfig(File file) {
        config = file;
        createList();
    }

    public ArrayList<Client> getListClient() {
        return clients;
    }

    public static void removeClient(String ip, int port) {
        Element clientElement = document.getRootElement();

        List<Element> clientList = clientElement.getChildren("client");

        for (int i = 0; i < clientList.size(); i++) {
            Element client = clientList.get(i);
            String ipClient = client.getChildText("ip");
            int portClient = Integer.parseInt(client.getChildText("port"));

            if (ipClient.equals(ip) && portClient == port) {
                client.detach();
                try {
                    writeXmlToFile(config);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    public void addVncToXml(Client client) {
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
            writeXmlToFile(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}