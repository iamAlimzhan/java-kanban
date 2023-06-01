package server;

import javax.sound.sampled.Port;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final int PORT = 8078;
    private  String KEY_API;
    private String url;
    private HttpClient client;

    public KVTaskClient(String url) {
        this.url = url;
        this.client = HttpClient.newHttpClient();
        this.KEY_API = register();
    }

    public String register(){
        URI uri = URI.create("http://" + url + ":" + PORT + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try{
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            if (response.statusCode() == 200){
                return response.body();
            }

        } catch (IOException | InterruptedException exp){
            System.out.println("Во время запроса произошла ошибка " + exp.getMessage());
        }
        return "";
    }

    public String load(String key){
        URI uri = URI.create("http://" + url + ":" + PORT + "/load/" + key + "?API_KEY=" + KEY_API);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try{
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            if(response.statusCode() == 200){
                return response.body();
            }
        }catch (IOException | InterruptedException exp){
            System.out.println("Во время запроса произошла ошибка " + exp.getMessage());
        }
        return "";
    }

    public void put(String key, String json){
        URI uri = URI.create("http://" + url + ":" + PORT + "/save/" + key + "?API_KEY=" + KEY_API);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try{
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            if(response.statusCode() != 200){
                System.out.println("Код ответа " + response.statusCode());
            }
        }catch (IOException | InterruptedException exp){
            System.out.println("Во время запроса произошла ошибка " + exp.getMessage());
        }
    }
    public void setKEY_API(String KEY_API){
        this.KEY_API = KEY_API;
    }
}
