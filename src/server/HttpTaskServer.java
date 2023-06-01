package server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.HTTPTasksManager;
import manager.Managers;
import tasks.Task;
import java.io.InputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpTaskServer {
    private int PORT=8080;
    private HttpServer httpServer;
    private HTTPTasksManager httpTasksManager;
    private Gson gson;

    //Конструктор класса
    public HttpTaskServer() throws IOException {
        httpTasksManager = new HTTPTasksManager();    //Создание менеджера задач
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
    }

    //Конструктор класса с заданием именем сохранения менеджера на сервере
    public HttpTaskServer(String saveKey) throws IOException {
        httpTasksManager = new HTTPTasksManager(saveKey);    //Создание менеджера задач
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
    }

    //Конструктор класса на основе загрузки образа менеджера с сервера
    public HttpTaskServer(String newKey, String loadKey) throws IOException {
        httpTasksManager = HTTPTasksManager.loadingFromJson(loadKey, newKey);    //Создание менеджера задач
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        start();
    }

    private void handleUsers(HttpExchange httpExchange){
        try{
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();
            String stringPath = path.toString();
            switch(requestMethod){
                case "GET":
                    if (stringPath.equals("/tasks/task/")) {
                        Map<Integer, Task> tasks = (Map<Integer, Task>) httpTasksManager.getTasks();
                        String answer = gson.toJson(tasks);
                        httpExchange.sendResponseHeaders(200, 0);

                        try (OutputStream outputStream = httpExchange.getResponseBody()) {
                            outputStream.write(answer.getBytes());
                        }

                    } else if (stringPath.startsWith("/tasks/task/?id=")) {
                        String[] id = stringPath.split("=");
                        Task task = httpTasksManager.receivingTasks(Integer.parseInt(id[1]));
                        String answer = gson.toJson(task);
                        httpExchange.sendResponseHeaders(200, 0);

                        try (OutputStream outputStream = httpExchange.getResponseBody()) {
                            outputStream.write(answer.getBytes());
                        }
                    } else
                        throw new RuntimeException("Такого пути нет");
                    break;

                case "POST":
                    InputStream stream = httpExchange.getRequestBody();
                    String jsonToString = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                    JsonElement element = JsonParser.parseString(jsonToString);
                    if (!element.isJsonObject()) {
                        throw new RuntimeException("Не является json объектом");
                    }

                    JsonObject object = element.getAsJsonObject();
                    Task task = gson.fromJson(object, Task.class);
                    Map<Integer, Task> tasks = (Map<Integer, Task>) httpTasksManager.getTasks();
                    if (httpExchange.getRequestURI().toString().equals("/tasks/task/")) {

                        if (tasks.containsValue(task)) {
                            httpTasksManager.updateTask(task);

                        } else {
                            httpTasksManager.buildTask(task);
                        }
                    }

                    httpExchange.sendResponseHeaders(201, 0);
                    httpExchange.close();
                    break;

                case "DELETE":
                    if ("/tasks/task/".equals(stringPath)) {
                        httpTasksManager.delTasks();
                    } else {
                        if (stringPath.startsWith("/tasks/task/?id=")) {
                            String[] array = stringPath.split("=");
                            httpTasksManager.deleteTask(Integer.parseInt(array[1]));
                        }
                    }
                    httpExchange.sendResponseHeaders(200, 0);
                    httpExchange.close();
                    break;

                default:
                    throw new RuntimeException("У таски неверный get");
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    public void start(){
        System.out.println("Started task server " + PORT);
        System.out.println("http://localhost:" + PORT + "/api/v1/users");
        httpServer.start();
    }
    public void stop(){
        httpServer.stop(0);
        System.out.println("Stopped the server on port " + PORT);
    }
    private String readText(HttpExchange httpExchange) throws  IOException{
        return new String(httpExchange.getRequestBody().readAllBytes(), "UTF-8");
    }
    private void sendText(HttpExchange httpExchange, String text) throws IOException{
        byte[] resp = text.getBytes("UTF-8");
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(200, resp.length);
        httpExchange.getResponseBody().write(resp);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        httpTaskServer.stop();
    }
}
