package server;

import exceptions.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class KVTaskClient {
    private final int PORT = 8078;
    private String apiToken;
    private final String url;
    private final HttpClient client;

    public KVTaskClient(String url) {
        this.client = HttpClient.newHttpClient();
        this.url = String.valueOf(URI.create(url));
        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(URI.create(url + "register"))
                .build();
        try{
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request,handler);
            int statusCode = response.statusCode();
            if(statusCode == 200 ) {
                this.apiToken = response.body();
            } else {
                System.out.println("Ошибка регистрации. Код: " + statusCode + ". Тело: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Что-то пошло не так: " + e.getMessage());
        }
    }

    //метод используется для загрузки значения
    public String load(String key) {
        URI uri = URI.create("http://localhost:" + PORT + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .GET()
                .uri(uri)
                .build();
        try{
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request,handler);
            int status = response.statusCode();
            if(status >= 200 && status <= 299) {
                return ( response.body());
            }
            if(status >= 400 && status <= 499) {
                return ("Сервер сообщил о проблеме с запросом. Код состояния: " + status + ". Тело запроса: "
                        + response.body());
            }
            if(status >= 500 && status <= 599) {
                return ("Сервер сообщил о внутренней проблеме и невозможности обработать запрос." + " Код состояния: "
                        + status + ". Тело запроса: " + response.body());
            } else {
                throw new ManagerSaveException("Что-то пошло не так. Сервер вернул код состояния: " + status
                        + ". Тело запроса: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Что-то пошло не так: " + e.getMessage());
        }
    }


    //метод используется для сохранения пары ключ-значение на сервере
    public void put(String key, String json) {
        URI uri = URI.create("http://localhost:" + PORT + "/save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .build();
        try{
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request,handler);
            int status = response.statusCode();
            if(status >= 200 && status <= 299) {
                System.out.println("Запрос успешно обработан! Тело запроса: " + response.body() + ". Код состояния: "
                        +  status);
            }
            else if(status >= 400 && status <= 499) {
                System.out.println("Проблема с запросом. Тело запроса: " +response.body() + ". Код состояния: "
                        +  status);
            }
            else if(status >= 500 && status <= 599) {
                System.out.println("Невозможно обработать запрос из за внутреней проблемы." + " Тело запроса: "
                        + response.body() + ". Код состояния: "
                        +  status);
            } else {
                throw new ManagerSaveException("Что-то пошло не так. Сервер вернул код состояния: " + status
                        + ". Тело запроса: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Что-то пошло не так: " + e.getMessage());
        }
    }

}
