package server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.HTTPTasksManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.MainTask;
import tasks.Subtask;
import tasks.Task;
import java.io.InputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class HttpTaskServer {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private int PORT=8080;
    private HttpServer httpServer;
    private TaskManager httpTasksManager;
    private Gson gson;


    //Конструктор класса на основе загрузки образа менеджера с сервера
    public HttpTaskServer() throws IOException {
        httpTasksManager = Managers.getDefault();
        httpServer = HttpServer.create();
        gsonInit();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", this::handleUsers);

    }

    //метод обрабатывает входящие HTTP-запросы и отправляет ответ обратно клиенту
    private void handleUsers(HttpExchange httpExchange) throws IOException{
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();
            String stringPath = path.toString();
            String[] id = stringPath.split("=");
            switch (requestMethod) {
                case "GET":
                    if (stringPath.equals("/tasks/task/")) {
                        System.out.println("Getting a list of all tasks");
                        String response = gson.toJson(httpTasksManager.getTasks());
                        httpExchange.sendResponseHeaders(200,0);
                        try (OutputStream os = httpExchange.getResponseBody()){
                            os.write(response.getBytes());
                        }
                    } else if (stringPath.startsWith("/tasks/task/?id=" + id)) {
                        System.out.println("Getting a task by id");
                        String response = gson.toJson(httpTasksManager.receivingTasks(Integer.parseInt(id[1])));
                        if (response != null) {
                            httpExchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } else {
                            String notFoundResponse = "Task not found";
                            httpExchange.sendResponseHeaders(404, notFoundResponse.length());
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(notFoundResponse.getBytes());
                            }
                        }
                    } else if (stringPath.equals("/tasks/epic/")) {
                        System.out.println("Getting a list of all epics");
                        String response = gson.toJson(httpTasksManager.getEpics());
                        httpExchange.sendResponseHeaders(200,0);
                        try (OutputStream os = httpExchange.getResponseBody()){
                            os.write(response.getBytes());
                        }
                    } else if (stringPath.startsWith("/tasks/epic/?id=" + id)) {
                        System.out.println("Getting an epic by id");
                        String response = gson.toJson(httpTasksManager.receivingEpics(Integer.parseInt(id[1])));
                        if (response != null) {
                            httpExchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } else {
                            String notFoundResponse = "Task not found";
                            httpExchange.sendResponseHeaders(404, notFoundResponse.length());
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(notFoundResponse.getBytes());
                            }
                        }
                    } else if (stringPath.startsWith("/tasks/subtask/?id=" + id)) {
                        System.out.println("Getting a subtask by id");
                        String response = gson.toJson(httpTasksManager.receivingSubtasks(Integer.parseInt(id[1])));
                        if (response != null) {
                            httpExchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } else {
                            String notFoundResponse = "Task not found";
                            httpExchange.sendResponseHeaders(404, notFoundResponse.length());
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(notFoundResponse.getBytes());
                            }
                        }
                    } else if (stringPath.equals("/tasks/subtask/")) {
                        System.out.println("Getting a list of all subtasks");
                        String response = gson.toJson(httpTasksManager.getSubtasks());
                        httpExchange.sendResponseHeaders(200,0);
                        try (OutputStream os = httpExchange.getResponseBody()){
                            os.write(response.getBytes());
                        }
                    } else if (stringPath.equals("/tasks/history/")) {
                        System.out.println("Getting a history");
                        String response = gson.toJson(httpTasksManager.getHistory());
                        httpExchange.sendResponseHeaders(200,0);
                        try (OutputStream os = httpExchange.getResponseBody()){
                            os.write(response.getBytes());
                        }
                    } else if (stringPath.equals("/tasks")) {
                        System.out.println("Getting tasks by priority");
                        String response = gson.toJson(httpTasksManager.getPrioritizedTasks());
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }

                    } else if (stringPath.equals("/tasks/subtask/epic/?id=" + id)) {
                        System.out.println("Getting Epic Subtasks");
                        String response = gson.toJson(httpTasksManager.receivingSubInEpic(Integer.parseInt(id[1])));
                        if (response != null) {
                            httpExchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(response.getBytes());
                            }
                        } else {
                            String notFoundResponse = "Task not found";
                            httpExchange.sendResponseHeaders(404, notFoundResponse.length());
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(notFoundResponse.getBytes());
                            }
                        }

                    } else {
                        throw new RuntimeException("There is no such way");
                    }

                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), CHARSET);
                    if (body.isEmpty()) {
                        String response = "Пустое тело";
                        httpExchange.sendResponseHeaders(400, response.length());
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                        httpExchange.close();
                        return;
                    }
                    if (stringPath.equals("/tasks/task/")) {
                        System.out.println("Build task");
                        httpTasksManager.buildTask(gson.fromJson(body, Task.class));
                        String response = "Task built";
                        httpExchange.sendResponseHeaders(201, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } else if (stringPath.equals("/tasks/task/?id=" + id)) {
                        System.out.println("Update task");
                        httpTasksManager.updateTask(gson.fromJson(body, Task.class));
                        String response = "Task updated";
                        httpExchange.sendResponseHeaders(201, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } else if (stringPath.equals("/tasks/epic/")) {
                        System.out.println("Build epic");
                        httpTasksManager.buildEpic(gson.fromJson(body, Epic.class));
                        String response = "Epic built";
                        httpExchange.sendResponseHeaders(201, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } else if (stringPath.equals("/tasks/epic/?id=" + id)) {
                        System.out.println("Update epic");
                        httpTasksManager.updateEpic(gson.fromJson(body, Epic.class));
                        String response = "Epic updated";
                        httpExchange.sendResponseHeaders(201, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } else if (stringPath.equals("/tasks/subtask/")) {
                        System.out.println("Build subtask");
                        Subtask subtask = gson.fromJson(body, Subtask.class);
                        System.out.println(subtask);
                        httpTasksManager.buildSubtask(gson.fromJson(body, Subtask.class));
                        String response = "Subtask built";
                        httpExchange.sendResponseHeaders(201, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } else if (stringPath.equals("/tasks/subtask/epic/?id=" + id)) {
                        System.out.println("Update subtask");
                        httpTasksManager.updateSubtask(gson.fromJson(body, Subtask.class));
                        String response = "Subtask updated";
                        httpExchange.sendResponseHeaders(201, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } else {
                        throw new RuntimeException("There is no such way");
                    }
                    httpExchange.close();
                    break;

                case "DELETE":
                    if ("/tasks/task/".equals(stringPath)) {
                        System.out.println("Delete tasks");
                        httpTasksManager.delTasks();
                        String response = "Tasks deleted";
                        httpExchange.sendResponseHeaders(200,0);
                        try (OutputStream os = httpExchange.getResponseBody()){
                            os.write(response.getBytes());
                        }
                    } else if (stringPath.startsWith("/tasks/task/?id=" + id)) {
                        System.out.println("Delete tasks by id");
                        httpTasksManager.deleteTask(Integer.parseInt(id[1]));
                        String response = "Task deleted";
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } else if ("/tasks/subtask/".equals(stringPath)) {
                        System.out.println("Delete subtasks");
                        httpTasksManager.delSubtasks();
                        String response = "Subtasks deleted";
                        httpExchange.sendResponseHeaders(200,0);
                        try (OutputStream os = httpExchange.getResponseBody()){
                            os.write(response.getBytes());
                        }
                    } else if (stringPath.startsWith("/tasks/subtask/?id=" + id)) {
                        System.out.println("Delete subtask by id");
                        httpTasksManager.deleteSubtask(Integer.parseInt(id[1]));
                        String response = "Subtask deleted";
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } else if ("/tasks/epic/".equals(stringPath)) {
                        System.out.println("Delete epics");
                        httpTasksManager.delEpics();
                        String response = "Epics deleted";
                        httpExchange.sendResponseHeaders(200,0);
                        try (OutputStream os = httpExchange.getResponseBody()){
                            os.write(response.getBytes());
                        }
                    } else if (stringPath.startsWith("/tasks/epic/?id=" + id)) {
                        System.out.println("Delete epic by id");
                        httpTasksManager.deleteEpic(Integer.parseInt(id[1]));
                        String response = "Epic deleted";
                        httpExchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } else {
                        throw new RuntimeException("There is no such way");
                    }
                    httpExchange.close();
                    break;
                default:
                    String response = "There is no such way";
                    httpExchange.sendResponseHeaders(400, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
            }
        }

    // метод инициализирует экземпляр gson с помощью адаптеров
    private void gsonInit(){
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, (JsonSerializer<Duration>) (src, typeOfSrc, context) ->
                        new JsonPrimitive(src.toMinutes()))
                .registerTypeAdapter(Duration.class, (JsonDeserializer<Duration>) (json, typeOfT, context) ->
                        Duration.ofMinutes(json.getAsLong()))
                .registerTypeAdapter(Instant.class, (JsonSerializer<Instant>) (src, typeOfSrc, context) ->
                        src != null ? new JsonPrimitive(src.toString()) : JsonNull.INSTANCE)
                .registerTypeAdapter(Instant.class, (JsonDeserializer<Instant>) (json, typeOfT, context) ->
                        json.isJsonNull() ? null : Instant.parse(json.getAsString()))
                .create();
    }

    //метод запускает HTTP-сервер
    public void start(){
        System.out.println("Started the server on port " + PORT);
        System.out.println("http://localhost:" + PORT + "/");
        httpServer.start();
    }

    //метод останавливает HTTP-сервер
    public void stop(){
        httpServer.stop(0);
        System.out.println("Stopped the server on port " + PORT);
    }

}
