package tests.managerTests;

import com.google.gson.*;
import manager.HTTPTasksManager;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.DurationAdapter;
import server.HttpTaskServer;
import server.InstantAdapter;
import tasks.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private static Gson gson;
    private static Task task;
    private static Epic epic;
    private static Subtask subtask;
    private static HttpClient client;
    private static String url;

    @BeforeAll
    public static void beforeAll() throws IOException {
        gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .registerTypeAdapter(Long.class, new DurationAdapter())
                .setPrettyPrinting()
                .create();
        url = "http://localhost:8080/";
        epic = new Epic("A", "AA", 1, TaskStatuses.NEW);
        subtask = new Subtask("B", "B1",2 ,TaskStatuses.NEW, 1);
        task = new Task("C", "CC", 3,TaskStatuses.DONE);
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryTaskManager.buildEpic(epic);
        inMemoryTaskManager.buildSubtask(subtask);
        inMemoryTaskManager.buildTask(task);
        client = HttpClient.newHttpClient();
        HttpTaskServer server = new HttpTaskServer(inMemoryTaskManager.toString());
        server.start();
    }

    @Test
    public void testGetTasksFromServer() throws IOException, InterruptedException {
        URI uri = URI.create(url + "tasks/task/?id=3");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertFalse(response.body().isEmpty());
    }

    @Test
    public void testGetEpicFromServer() throws IOException, InterruptedException {
        URI uri = URI.create(url + "tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertFalse(response.body().isEmpty());
    }

    @Test
    public void testGetSubtaskFromServer() throws IOException, InterruptedException {
        URI uri = URI.create(url + "tasks/subtask/?id=2");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertFalse(response.body().isEmpty());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        URI uri = URI.create(url + "tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        uri = URI.create(url + "tasks/task/?id=3");
        request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        handler = HttpResponse.BodyHandlers.ofString();
        response = client.send(request, handler);
        assertFalse(response.body().isEmpty());
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        URI uri = URI.create(url + "tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        uri = URI.create(url + "tasks/subtask/?id=2");
        request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        handler = HttpResponse.BodyHandlers.ofString();
        response = client.send(request, handler);
        assertFalse(response.body().isEmpty());
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        URI uri = URI.create(url + "tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        uri = URI.create(url + "tasks/epic/?id=1");
        request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        handler = HttpResponse.BodyHandlers.ofString();
        response = client.send(request, handler);
        assertFalse(response.body().isEmpty());
    }

    @Test
    public void testBuildNewTask() throws IOException, InterruptedException {
        Task newTask = new Task("A", "AA", 1,TaskStatuses.NEW);
        newTask.setId(1);
        URI uri = URI.create(url + "tasks/task");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(newTask));
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(uri)
                .header("refresh", "create")
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        uri = URI.create(url + "tasks/task/?id=4");
        request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        response = client.send(request, handler);
        assertFalse(response.body().isEmpty());
    }

    @Test
    public void testBuildNewSubtask() throws IOException, InterruptedException {
        Subtask newSubtask = new Subtask("New Subtask", "Test", 4,TaskStatuses.NEW, 1);
        newSubtask.setId(4);
        URI uri = URI.create(url + "tasks/subtask");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(newSubtask));
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(uri)
                .header("refresh", "create")
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        uri = URI.create(url + "tasks/subtask/?id=4");
        request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        response = client.send(request, handler);
        assertFalse(response.body().isEmpty());
    }

    @Test
    public void testBuildNewEpic() throws IOException, InterruptedException {
        Epic epic2 = new Epic("A", "AA", 1, TaskStatuses.NEW);
        epic2.setId(1);
        URI uri = URI.create(url + "tasks/epic");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(epic2));
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(uri)
                .header("refresh", "create")
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        uri = URI.create(url + "tasks/epic/?id=4");
        request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        response = client.send(request, handler);
        assertFalse(response.body().isEmpty());
    }
    @Test
    public void testLoadDataFromServer() {
        // Создание объекта InMemoryTaskManager и заполнение его данными
        HTTPTasksManager taskManager = new HTTPTasksManager("saveKey", "url");

        // Загрузка данных с сервера
        String serverUrl = "http://localhost:8080/";
        HTTPTasksManager httpTaskManager = new HTTPTasksManager(serverUrl, url,true);

        // Проверка совпадения списков задач
        List<Task> tasksFromManager = taskManager.getTasks();
        List<Task> tasksFromServer = httpTaskManager.getTasks();
        assertEquals(tasksFromManager, tasksFromServer, "Список задач не совпадает");

        // Проверка совпадения списков эпиков
        List<Epic> epicsFromManager = taskManager.getEpics();
        List<Epic> epicsFromServer = httpTaskManager.getEpics();
        assertEquals(epicsFromManager, epicsFromServer, "Список эпиков не совпадает");

        // Проверка совпадения списков подзадач
        List<Subtask> subtasksFromManager = taskManager.getSubtasks();
        List<Subtask> subtasksFromServer = httpTaskManager.getSubtasks();
        assertEquals(subtasksFromManager, subtasksFromServer, "Список подзадач не совпадает");

        // Проверка совпадения отсортированного списка
        List<Task> sortedTasksFromManager = taskManager.getPrioritizedTasks();
        List<Task> sortedTasksFromServer = httpTaskManager.getPrioritizedTasks();
        assertEquals(sortedTasksFromManager, sortedTasksFromServer, "Отсортированный список задач не совпадает");

        // Проверка совпадения истории
        Map<Integer, List<Task>> historyFromManager = (Map<Integer, List<Task>>) taskManager.getHistory();
        Map<Integer, List<Task>> historyFromServer = (Map<Integer, List<Task>>) httpTaskManager.getHistory();
        assertEquals(historyFromManager, historyFromServer, "История не совпадает");

        // Проверка значения поля idTask
        int idTaskFromManager = taskManager.receivingTasks(task.getId()).getId();
        int idTaskFromServer = httpTaskManager.receivingTasks(task.getId()).getId();
        assertEquals(idTaskFromManager, idTaskFromServer, "Значение поля idTask не совпадает");
    }
}