package com.company;

public class Client {
    private int row;
    private int column;
    private String ip;
    private int port;
    private String pass = "";
    private String name;

    public Client(int row, int column, String ip, int port, String pass, String name) {
        this.row = row;
        this.column = column;
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

    public String getNameClient() {
        if (!name.equals("")) {
            return name;
        } else {
            return ip + ":" + port;
        }
    }
}