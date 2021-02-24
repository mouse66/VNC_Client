package ru.arseny.Clients;

import com.shinyhut.vernacular.client.VernacularClient;

public class Client {
    private int row;
    private int column;
    private String ip;
    private int port;
    private String pass = "";
    private String name;
    private VernacularClient client;

    public Client(int row, int column, String ip, int port, String pass, String name,
                  VernacularClient client) {
        this.row = row;
        this.column = column;
        this.ip = ip;
        this.port = port;
        this.pass = pass;
        this.name = name;
        this.client = client;
    }

    public Client(String ip, int port, String name) {
        this.ip = ip;
        this.port = port;
        this.name = name;
    }

    public Client(String ip, int port, String pass, String name) {
        this.ip = ip;
        this.port = port;
        this.pass = pass;
        this.name = name;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VernacularClient getClient() {
        return client;
    }

    public void setClient(VernacularClient client) {
        this.client = client;
    }

    public String getNameClient() {
        if (!name.equals("")) {
            return name;
        } else {
            return ip + ":" + port;
        }
    }

    public void disconnect() {
        client.stop();
    }
}