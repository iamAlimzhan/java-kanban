import manager.*;
import tasks.*;

import java.io.File;

import static tasks.TaskStatuses.DONE;
import static tasks.TaskStatuses.NEW;

public class Main {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        Task taskFirst = new Task ("Сходить в школу", "Отсидеть уроки", NEW);
        Task taskSecond = new Task("Поехать на работу", "Сесть в автобус", DONE);
        Epic epicFirst = new Epic("Уборка дома", "Убраться в своей комнате, убраться на кухне, убраться в гостиной");
        Subtask subtaskFirst = new Subtask("Убрать свою комнату", "Заправить постель, выкинуть мусор, вытереть пыль", DONE, 3);
        Subtask subtaskSecond = new Subtask("Убраться на кухне", "Помыть посуду, помыть полы", DONE, 3);
        Subtask subtaskThird = new Subtask("Убраться в гостиной", "Помыть полы, сложить вещи в шкаф", NEW, 3);
        Epic epicSecond = new Epic("Написать код", "Сесть за компьютер");
        inMemoryTaskManager.buildTask(taskFirst);
        inMemoryTaskManager.buildTask(taskSecond);
        inMemoryTaskManager.buildEpic(epicFirst);
        inMemoryTaskManager.buildEpic(epicSecond);
        inMemoryTaskManager.buildSubtask(subtaskFirst);
        inMemoryTaskManager.buildSubtask(subtaskSecond);
        inMemoryTaskManager.buildSubtask(subtaskThird);

        inMemoryTaskManager.receivingTasks(1);
        inMemoryTaskManager.receivingTasks(1);
        System.out.println(inMemoryTaskManager.getHistory());
        inMemoryTaskManager.receivingEpics(3);
        inMemoryTaskManager.receivingEpics(3);
        System.out.println(inMemoryTaskManager.getHistory());
        inMemoryTaskManager.deleteTask(1);
        System.out.println(inMemoryTaskManager.getHistory());
        inMemoryTaskManager.deleteEpic(3);
        System.out.println(inMemoryTaskManager.getHistory());
    }
}