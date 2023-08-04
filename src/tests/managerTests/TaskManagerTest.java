package tests.managerTests;

import exceptions.CreateException;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import tasks.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest <T extends TaskManager> {
    TaskManager taskManager;
    abstract T createTaskManager();
    Task task;
    Task task2;
    Epic epic;
    Epic epic2;
    Subtask subtask1;
    Subtask subtask2;
    MainTask mainTask;

    @BeforeEach
    public void beforeEach(){
        taskManager = new InMemoryTaskManager();
    }
    //Проверка получения списка всех задач
    @Test
    public void getAllTasksList(){
        // С пустым списком задач
        List<Task> tasksList = taskManager.getTasks();
        assertTrue(tasksList.isEmpty());
        // Со стандартным поведением
        task = new Task("A","A1", 1, TaskStatuses.NEW);
        taskManager.buildTask(task);
        assertEquals(1, taskManager.getTasks().size(), "Не верное количество задач");
    }

    //Проверка получения списка задач (Эпики и задачи)
    @Test
    public  void getTasksList(){
        // С пустым списком задач
        assertDoesNotThrow(()->taskManager.getTasks(),
                "Не должно выбрасывать исключение при пустом списка задач");

        // Со стандартным поведением
        task = new Task("B","B1", 1, TaskStatuses.NEW);
        taskManager.buildTask(task);
        assertEquals(1, taskManager.getTasks().size(), "Не верное количество задач");
    }

    //Проверка получения отсортированного списка всех задач
    @Test
    public void getPrioritizedTasks() {
        assertNotNull(taskManager.getPrioritizedTasks(), "Список задач не пуст");

        Epic epic = new Epic("C", "C1", 1, TaskStatuses.NEW);
        taskManager.buildEpic(epic);

        epic = new Epic("CC", "C2", 1, TaskStatuses.IN_PROGRESS);
        taskManager.buildEpic(epic);

        Subtask subtask1 = new Subtask("D1",
                "DD",
                2,
                TaskStatuses.NEW,
                1,
                Instant.now().plusMillis(100),
                Long.valueOf(1800L));
        taskManager.buildSubtask(subtask1);

        Subtask subtask2 = new Subtask("D2",
                "DD2",
                3,
                TaskStatuses.NEW,
                1,
                Instant.now().plusMillis(3000),
                Long.valueOf(18000L));
        taskManager.buildSubtask(subtask2);

        Task task = new Task("A",
                "A1",
                1,
                TaskStatuses.NEW,
                Instant.now().plusMillis(60000),
                Long.valueOf(1800000L));
        taskManager.buildTask(task);
        List<MainTask> sortedTasks = taskManager.getPrioritizedTasks();

        assertEquals(5, sortedTasks.size(), "В отсортированном списке неверное количество задач");
    }


    //Проверка получения списка всех Epic-ов
    @Test
    public void getEpics(){
        // С пустым списком задач.
        assertDoesNotThrow(()->taskManager.getEpics(),
                "Не должно выбрасывать исключение при пустом списке задач");

        // Со стандартным поведением.
        epic = new Epic("A","A1", 1, TaskStatuses.NEW);
        taskManager.buildEpic(epic);
        assertEquals(1, taskManager.getEpics().size(), "Количество эпиков в истории не верно");
    }

    //Проверка получения списка всех подзадач определённого эпика
    @Test
    public void getSubTasks(){
        // С пустым списком подзадач.
        epic = new Epic("B","B1", 1, TaskStatuses.NEW);
        taskManager.buildEpic(epic);
        assertDoesNotThrow(()->taskManager.receivingSubtasks(epic.getId()),
                "Не должно выбрасывать исключение при пустом списке задач Эпика");

        // Со стандартным поведением
        subtask1 = new Subtask("C", "c1", 2, TaskStatuses.IN_PROGRESS, 1);
        taskManager.buildSubtask(subtask1);
        assertEquals(1, taskManager.receivingSubInEpic(epic.getId()).size(), "Количество подзадач в эпике не верно");
    }

    //Проверка получения задачи по id
    @Test
    public void getTask(){
        // С неверным id задачи
        assertDoesNotThrow(()->taskManager.receivingTasks(1),
                "Не должно выбрасывать искл при несущ задачи");

        // Со стандартным поведением.
        task = new Task("A","A1", 1, TaskStatuses.NEW);
        taskManager.buildTask(task);

        assertEquals(task, taskManager.receivingTasks(1),
                "Добавленая задача не соответсвует возвращенной задаче");
    }

    //Проверка добавления новой Task, Epic и Subtask
    @Test
    public void addTask(){
        // Со стандартным поведением.
        task = new Task("A","A1", 1, TaskStatuses.NEW);
        taskManager.buildTask(task);
        assertEquals(1, taskManager.getTasks().size(),
                "Количество задач после добавления неверно");
    }

    //Проверка обновления задачи любого типа по id
    @Test
    public void updateTask(){
        // С неверным id задачи пустой или несуществующий id
        taskManager.updateTask(null);
        assertDoesNotThrow(()->taskManager.updateTask(null),
                "Не должно выбрасывать исключение при пустом обновлении ");
        // Со стандартным поведением.
        task = new Task("A","A1", 1, TaskStatuses.NEW);
        taskManager.buildTask(task);

        task = new Task("A","A1", 1, TaskStatuses.NEW);
        taskManager.updateTask(task);
        assertEquals("A", task.getTaskName(),
                "Не обновлено описание задачи");
    }

    //Проверка удаления ранее добавленных задач и по идентификатору
    @Test
    public void delTask(){
        // С пустым списком задач.
        assertDoesNotThrow(()->taskManager.deleteTask(1),
                "Не должно вырасывать исключене после попытки удаления несуществ задачи");
        // Со стандартным поведением.
        task = new Task("A","A1", 1, TaskStatuses.NEW);
        taskManager.buildTask(task);
        task = new Task("AA","A2", 2, TaskStatuses.NEW);
        taskManager.buildTask(task);
        assertEquals(2, taskManager.getTasks().size(),
                "Не верное количество задач после добавления");
        taskManager.deleteTask(1);
        assertEquals(1, taskManager.getTasks().size(),
                "Не верное количество задач после удаления");

        // С неверным идентификатором задачи
        taskManager.deleteTask(0);
        assertEquals(1, taskManager.getTasks().size(),
                "После удаления список не пуст");
    }


    //Проверка получения просмотренных задачи
    @Test
    public void history(){
        // С пустым списком задач.
        assertDoesNotThrow(()->taskManager.getHistory(),
                "Не должно выбрасывать исключение при пустой истории");

        assertNotNull(taskManager.getHistory(), "История не пустая");

        // Со стандартным поведением.
        task = new Task("A","A1", 1, TaskStatuses.NEW);
        taskManager.buildTask(task);
        task = taskManager.receivingTasks(1);
        assertEquals(1, taskManager.getHistory().size(),
                "Не верный размер историй");
    }

    // Проверка метода валидации времени
    @Test
    public void validateTimeIntervals() {
        LocalDateTime start1 = LocalDateTime.of(2023, 1, 1, 8, 0);
        LocalDateTime end1 = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime start2 = LocalDateTime.of(2023, 1, 1, 9, 0);
        LocalDateTime end2 = LocalDateTime.of(2023, 1, 1, 11, 0);

        Instant instantStart1 = start1.toInstant(ZoneOffset.UTC);
        Instant instantEnd1 = end1.toInstant(ZoneOffset.UTC);
        Instant instantStart2 = start2.toInstant(ZoneOffset.UTC);
        Instant instantEnd2 = end2.toInstant(ZoneOffset.UTC);

        Task testTask = new Task("A", "AA", 1, TaskStatuses.NEW);
        testTask.setStartTime(instantStart1);
        testTask.setDuration(Duration.between(instantStart1, instantEnd1).toMillis());

        taskManager.buildTask(testTask);

        Task testTask2 = new Task("B", "BB", 2, TaskStatuses.NEW);
        testTask2.setStartTime(instantStart2);
        testTask2.setDuration(Duration.between(instantStart2, instantEnd2).toMillis());

        assertThrows(CreateException.class,
                () -> taskManager.buildTask(testTask2),
                "Новая задача не входит внутрь существующей");
    }
}