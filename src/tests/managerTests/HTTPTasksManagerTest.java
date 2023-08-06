package tests.managerTests;

import exceptions.ManagerSaveException;
import manager.HTTPTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.KVServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatuses;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class HTTPTasksManagerTest extends TaskManagerTest<HTTPTasksManager> {
    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;

    @BeforeEach
    void setUpServers() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

    @AfterEach
    void tearDownServers() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    public void testLoadTasksAndSave() {
        // Создание экземпляра HTTPTasksManager без выполнения выгрузки
        HTTPTasksManager httpTaskManager = new HTTPTasksManager("http://localhost:8078/", true);

        Epic epic = new Epic("A", "A1", 1, TaskStatuses.NEW);
        Subtask subtask = new Subtask("B", "BB", 2, TaskStatuses.NEW, 1);
        Task task = new Task("C", "C1", 3, TaskStatuses.NEW);

        // Создание нового экземпляра HTTPTasksManager с выполнением выгрузки
        HTTPTasksManager manager = new HTTPTasksManager("http://localhost:8078/", true);

        // Сравнение содержимого списков задач, эпиков и подзадач
        assertEquals(httpTaskManager.getTasks(), manager.getTasks());
        assertEquals(httpTaskManager.getEpics(), manager.getEpics());
        assertEquals(httpTaskManager.getSubtasks(), manager.getSubtasks());
    }

    @Test
    public void testLoadHistoryAndSave() {
        HTTPTasksManager httpTaskManager = new HTTPTasksManager("http://localhost:8078/", false);
        Epic epic = new Epic("A", "A1", 1, TaskStatuses.NEW);
        Subtask subtask = new Subtask("B", "BB", 2,TaskStatuses.NEW, 1);
        Task task = new Task("C", "C1", 3, TaskStatuses.NEW);
        httpTaskManager.buildEpic(epic);
        httpTaskManager.buildSubtask(subtask);
        httpTaskManager.buildTask(task);

        HTTPTasksManager manager = new HTTPTasksManager("http://localhost:8078/", false);

        assertEquals(httpTaskManager.getHistory().size(), manager.getHistory().size());
    }



    @Override
    HTTPTasksManager createTaskManager() {
        return new HTTPTasksManager("http://localhost:8078/", false);
    }
}
