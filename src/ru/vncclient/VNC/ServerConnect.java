package ru.vncclient.VNC;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.vncclient.Clients.Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class ServerConnect {
    /**
     * Подключение к серверу и получение данных о виртуальных машинах
     * @param ip IP адрес сервера
     * @param port порт сервера
     * @return список клиентов
     * @throws Exception
     */
    public static ArrayList<Client> connect(String ip, int port) throws Exception {
        URL url = new URL(String.format("http://%s:%d/getAllVMs", ip, port));

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine = "";
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return parseArray(response.toString());
    }

    /**
     * Лист с клиентами, полученных с сервера
     * @param array JSON массив в виде строки
     * @return лист с клиентами
     * @throws ParseException ошибка в парсинге JSON
     */
    private static ArrayList<Client> parseArray(String array) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONArray object = (JSONArray) parser.parse(array);

        Iterator iterator = object.iterator();

        ArrayList<Client> clients = new ArrayList<>();
        while (iterator.hasNext()) {
            JSONObject obj = (JSONObject) iterator.next();

            try {
                int port = Integer.parseInt(String.valueOf(obj.get("port")));
                String ip = String.valueOf(obj.get("ip"));
                Client client = new Client(ip, port, "");

                clients.add(client);
            } catch (Exception ignored) {
            }
        }

        return clients;
    }
}