package tests.tasksTests;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatuses;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Epic epic;
    Subtask sub1;
    Subtask sub2;
    TaskManager taskManager;
    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }
    @AfterEach
    void clear(){
        taskManager.delTasks();
        taskManager.delEpics();
        taskManager.delSubtasks();
    }

    //Пустой список задачи
    @Test
    public void emptyTasksList() {
        epic = new Epic("A", "AA", 1, TaskStatuses.NEW);
        taskManager.buildEpic(epic);
        sub1 = new Subtask("В1", "BB1", 2,TaskStatuses.NEW,1);
        sub2 = new Subtask("B2", "BB2", 3,TaskStatuses.NEW,1);
        taskManager.buildSubtask(sub1);
        taskManager.buildSubtask(sub2);
        TaskStatuses epicsStatus = taskManager.receivingEpics(1).getStatus();
        assertEquals(epicsStatus, TaskStatuses.NEW);
    }

    //Все подзадачи со статусом NEW
    @Test
    public void allSubtaskstasksWithStatusNEW(){
        epic = new Epic("A", "AA", 1, TaskStatuses.NEW);
        taskManager.buildEpic(epic);
        sub1 = new Subtask("B1", "BB1", 2, TaskStatuses.NEW, 1);
        sub2 = new Subtask("B2", "BB2", 3, TaskStatuses.NEW, 1);
        taskManager.buildSubtask(sub1);
        taskManager.buildSubtask(sub2);
        TaskStatuses epicsStatus = taskManager.receivingEpics(1).getStatus();
        assertEquals(epicsStatus, TaskStatuses.NEW);
    }
    //Все подзадачи со статусом DONE.
    @Test
    public void allSubtasksWithStatusDONE(){
        epic = new Epic("A", "AA", 1, TaskStatuses.NEW);
        taskManager.buildEpic(epic);
        sub1 = new Subtask("B1", "BB1", 2, TaskStatuses.DONE, 1);
        sub2 = new Subtask("B2", "BB2", 3, TaskStatuses.DONE, 1);
        taskManager.buildSubtask(sub1);
        taskManager.buildSubtask(sub2);
        TaskStatuses epicsStatus = taskManager.receivingEpics(1).getStatus();
        assertEquals(epicsStatus, TaskStatuses.DONE);
    }
    //Подзадачи со статусами NEW и DONE.
    @Test
    public void allSubtasksWithStatusNEWandDONE(){
        epic = new Epic("A", "AA", 1, TaskStatuses.NEW);
        taskManager.buildEpic(epic);
        sub1 = new Subtask("B1", "BB1", 2, TaskStatuses.NEW, 1);
        sub2 = new Subtask("B2", "BB2", 3, TaskStatuses.DONE, 1);
        taskManager.buildSubtask(sub1);
        taskManager.buildSubtask(sub2);
        TaskStatuses epicsStatus = taskManager.receivingEpics(1).getStatus();
        assertEquals(epicsStatus, TaskStatuses.IN_PROGRESS);
    }

    //Подзадачи со статусом IN_PROGRESS.
    @Test
    public void allSubtasksWithStatusInProgress(){
        epic = new Epic("A", "AA", 1, TaskStatuses.NEW);
        taskManager.buildEpic(epic);
        sub1 = new Subtask("B1", "BB1", 2, TaskStatuses.IN_PROGRESS, 1);
        sub2 = new Subtask("B2", "BB2", 3, TaskStatuses.IN_PROGRESS, 1);
        taskManager.buildSubtask(sub1);
        taskManager.buildSubtask(sub2);
        TaskStatuses epicsStatus = taskManager.receivingEpics(1).getStatus();
        assertEquals(epicsStatus, TaskStatuses.IN_PROGRESS);
    }
}