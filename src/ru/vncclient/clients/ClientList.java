package ru.vncclient.clients;

import java.util.HashMap;
import java.util.Map;

public class ClientList {
    /**
     * Ключ в формате "ip:port"
     */
    private static Map<String, Client> clientMap;
    private static String passwordCurrent;

    public ClientList() {
        clientMap = new HashMap<>();
    }

    /**
     * Добавление клиента
     * @param key
     * @param value {@link Client}
     */
    public static void addClient(String key, Client value) {
        clientMap.put(key, value);
    }

    /**
     * Возвращает клиента по ключу
     * @param key
     * @return {@link Client}
     */
    public static Client getClient(String key) {
        return clientMap.get(key);
    }

    /**
     * Возвращает клиента по строке и столбцу
     * @param rowIndex строка
     * @param colIndex столбец
     * @return {@link Client}
     */
    public static Client getClient(int rowIndex, int colIndex) {
        for (Client client : clientMap.values()) {
            if (client.getColumn() == colIndex && client.getRow() == rowIndex) {
                return client;
            }
        }
        return null;
    }

    /**
     * Удалает клиента из списка
     * @param key
     */
    public static void removeClient(String key) {
        clientMap.remove(key);
    }

    /**
     * Присутствие клиента по ключу
     * @param key
     * @return true - содержит, false - нет
     */
    public static boolean hasClient(String key) {
        return clientMap.containsKey(key);
    }

    /**
     * Останавливает клиента по ключу
     * @param key
     */
    public static void stopClient(String key) {
        Client c = getClient(key);
        if (c != null) c.disconnect();
    }

    /**
     * Остановка всех клиентов
     */
    public static void stopClients() {
        for (Client c : clientMap.values()) {
            c.disconnect();
        }
    }

    /**
     * Очистка списка и остановка клиентов
     */
    public static void clearMap() {
        stopClients();
        clientMap.clear();
    }

    /**
     * Текущий введенный пароль пользователем, или из конфигурации
     * @return passwordCurrent
     */
    public static String getPassword() {
        return passwordCurrent;
    }

    /**
     * Устанавливает последний введенный пароль
     * @param pass
     */
    public static void setPassword(String pass) {
        passwordCurrent = pass;
    }
}