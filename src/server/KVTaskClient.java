package server;

import exceptions.ManagerSaveException;

import javax.sound.sampled.Port;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final int PORT = 8078;
    private final String keyApi;
    private final String url;
    private final HttpClient client;

    public KVTaskClient(String url) {
        this.url = url;
        this.client = HttpClient.newHttpClient();
        this.keyApi = register();
    }

    private String register() {
        URI uri = URI.create("http://" + url + ":" + PORT + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new ManagerSaveException("Ошибка регистрации. Статус код: " + response.statusCode());
            }
        } catch (IOException | InterruptedException exp) {
            throw new ManagerSaveException("Ошибка при выполнении запроса: " + exp.getMessage());
        }
    }


    public String load(String key) {
        URI uri = URI.create("http://" + url + ":" + PORT + "/load/" + key + "?API_KEY=" + keyApi);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            if (response.statusCode() == 200) {
                return response.body();
            } else if (response.statusCode() != 200){
                throw new ManagerSaveException("Ошибка загрузки. Статус код: " + response.statusCode());
            }
        } catch (IOException | InterruptedException exp) {
            throw new ManagerSaveException("Ошибка при выполнении запроса: " + exp.getMessage());
        }
        return key;
    }


    public void put(String key, String json) {
        URI uri = URI.create("http://" + url + ":" + PORT + "/save/" + key + "?API_KEY=" + keyApi);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Ошибка сохранения. Статус код: " + response.statusCode());
            }
        } catch (IOException | InterruptedException exp) {
            throw new ManagerSaveException("Ошибка при выполнении запроса: " + exp.getMessage());
        }
    }

}
