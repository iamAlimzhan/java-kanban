package tests.managerTests;

import com.google.gson.*;
import manager.HTTPTasksManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.DurationAdapter;
import server.InstantAdapter;
import server.KVServer;
import server.KVTaskClient;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatuses;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class HTTPTasksManagerTest extends TaskManagerTest<HTTPTasksManager>{
    private HTTPTasksManager httpTaskManager;
    @BeforeAll
    public static void beforeAllHTTPTaskManagerTests() {
        try {
            KVServer kvServer = new KVServer();
            kvServer.start();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new InstantAdapter())
                    .registerTypeAdapter(Long.class, new DurationAdapter())
                    .setPrettyPrinting()
                    .create();

            KVTaskClient kvTaskClient = new KVTaskClient("http://localhost:8078/");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    HTTPTasksManager createTaskManager() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .registerTypeAdapter(Long.class, new DurationAdapter())
                .setPrettyPrinting()
                .create();

        KVTaskClient kvTaskClient = new KVTaskClient("http://localhost:8078/");
        return new HTTPTasksManager("http://localhost:8078/", "localhost");
    }

    @BeforeEach
    public void beforeEach() {
        httpTaskManager = createTaskManager();
    }

    @Test
    public void testLoadTasksAndSave() {
        // Создание экземпляра HTTPTasksManager без выполнения выгрузки
        HTTPTasksManager httpTaskManager = new HTTPTasksManager("http://localhost:8078/", "localhost", false);

        Epic epic = new Epic("A", "A1", 1, TaskStatuses.NEW);
        Subtask subtask = new Subtask("B", "BB", 2, TaskStatuses.NEW, 1);
        Task task = new Task("C", "C1", 3, TaskStatuses.NEW);

        httpTaskManager.buildEpic(epic);
        httpTaskManager.buildSubtask(subtask);
        httpTaskManager.buildTask(task);

        // Создание нового экземпляра HTTPTasksManager с выполнением выгрузки
        HTTPTasksManager manager = new HTTPTasksManager("http://localhost:8078/", "localhost", true);

        // Сравнение содержимого списков задач, эпиков и подзадач
        assertEquals(httpTaskManager.getTasks(), manager.getTasks(), "Список задач после выгрузки не совпадает");
        assertEquals(httpTaskManager.getEpics(), manager.getEpics(), "Список эпиков после выгрузки не совпадает");
        assertEquals(httpTaskManager.getSubtasks(), manager.getSubtasks(), "Список подзадач после выгрузки не совпадает");
    }

    @Test
    public void testLoadHistoryAndSave() {
        Epic epic = new Epic("A", "A1", 1, TaskStatuses.NEW);
        Subtask subtask = new Subtask("B", "BB", 2,TaskStatuses.NEW, 1);
        Task task = new Task("C", "C1", 3, TaskStatuses.NEW);
        httpTaskManager.buildEpic(epic);
        httpTaskManager.buildSubtask(subtask);
        httpTaskManager.buildTask(task);
        httpTaskManager.receivingTasks(3);
        httpTaskManager.receivingSubtasks(2);
        HTTPTasksManager manager = new HTTPTasksManager("http://localhost:8078/", "localhost");
        assertEquals(httpTaskManager.getHistory().size(), manager.getHistory().size()); // и здесь тоже
    }

}