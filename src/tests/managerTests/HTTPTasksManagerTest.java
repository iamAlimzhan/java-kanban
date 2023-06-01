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

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class HTTPTasksManagerTest {
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
            kvTaskClient.setKEY_API("DEBUG");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @BeforeEach
    public void beforeEach() {
        httpTaskManager = new HTTPTasksManager("http://localhost:8078/");
    }

    @Test
    public void testLoadTasksAndSave() {
        Epic epic = new Epic("A", "A1", 1, TaskStatuses.NEW);
        Subtask subtask = new Subtask("B", "BB", 2,TaskStatuses.NEW, 1);
        Task task = new Task("C", "C1", 3, TaskStatuses.NEW);
        httpTaskManager.buildEpic(epic);
        httpTaskManager.buildSubtask(subtask);
        httpTaskManager.buildTask(task);
        HTTPTasksManager manager = new HTTPTasksManager("http://localhost:8078/");
        manager.loadingFromJson("old","new");
        assertEquals(task, manager.receivingTasks(3)); //не могу понять, как пофиксить эту ошибку
        assertEquals(epic, manager.receivingEpics(1));
        assertEquals(subtask, manager.receivingSubtasks(2));
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
        HTTPTasksManager manager = new HTTPTasksManager("http://localhost:8078/");
        manager.loadingFromJson("old", "new");
        assertEquals(httpTaskManager.getHistory().size(), manager.getHistory().size()); // и здесь тоже
    }

}