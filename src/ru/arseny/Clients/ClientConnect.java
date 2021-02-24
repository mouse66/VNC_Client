package ru.arseny.Clients;

import java.util.HashMap;
import java.util.Map;

public class ClientConnect {
    private static Map<String, Client> clientMap;
    private static String passwordCurrent;

    public ClientConnect() {
        clientMap = new HashMap<>();
    }

    public void addClient(String key, Client value) {
        clientMap.put(key, value);
    }

    public static Client getClient(String key) {
        return clientMap.get(key);
    }

    public static Client getClient(int rowIndex, int colIndex) {
        Client client = null;
        for (Client c : clientMap.values()) {
            if (c.getColumn() == colIndex && c.getRow() == rowIndex) {
                client = c;
                break;
            }
        }
        return client;
    }

    public static void removeClient(String key) {
        clientMap.remove(key);
    }

    public boolean hasClient(String key) {
        return clientMap.containsKey(key);
    }

    public static void stopClients() {
        for (Client c : clientMap.values()) {
            c.getClient().stop();
        }
    }

    public static void stopClient(String key) {
        getClient(key).disconnect();
    }

    public void clearMap() {
        clientMap.clear();
    }

    public static String getPassword() {
        return passwordCurrent;
    }

    public static void setPassword(String pass) {
        passwordCurrent = pass;
    }
}