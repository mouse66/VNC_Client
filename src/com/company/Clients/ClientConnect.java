package com.company.Clients;

import java.util.HashMap;
import java.util.Map;

public class ClientConnect {
    private final Map<String, Client> clientMap;

    public ClientConnect() {
        this.clientMap = new HashMap<>();
    }

    public void addClient(String key, Client value) {
        clientMap.put(key, value);
    }

    public Client getClient(String key) {
        return clientMap.get(key);
    }

    public Client getClient(int rowIndex, int colIndex) {
        Client client = null;
        for (Client c : clientMap.values()) {
            if (c.getColumn() == colIndex && c.getRow() == rowIndex) {
                client = c;
                break;
            }
        }
        return client;
    }

    public void removeClient(String key) {
        clientMap.remove(key);
    }

    public boolean hasClient(String key) {
        return clientMap.containsKey(key);
    }

    public void stopClients() {
        for (Client c : clientMap.values()) {
            c.getClient().stop();
        }
    }

    public void stopClient(String key) {
        getClient(key).disconnect();
    }

    public void clearMap() {
        clientMap.clear();
    }
}