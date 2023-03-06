import manager.*;
import tasks.*;
import static tasks.TaskStatuses.DONE;
import static tasks.TaskStatuses.NEW;

public class Main {

    public static void main(String[] args) {
        //System.out.println("Поехали!");
       /* TaskManager manager = Managers.getDefault();
        Task taskFirst = new Task ("Сходить в школу", "Отсидеть уроки", NEW);
        Task taskSecond = new Task("Поехать на работу", "Сесть в автобус", DONE);
        Epic epicFirst = new Epic("Уборка дома", "Убраться в своей комнате и убраться на кухне", "");
        Subtask subtaskFirst = new Subtask("Убрать свою комнату", "Заправить постель, выкинуть мусор, вытереть пыль", NEW, 3);
        Subtask subtaskSecond = new Subtask("Убратьс на кухне", "Помыть посуду, помыть полы", DONE, 3);
        Epic epicSecond = new Epic("Сделать проект", "Сдать проект до конца недели");
        Subtask subtaskThird = new Subtask("Кодинг", "Написать бекенд трекера задач", NEW, 4);
        manager.buildTask(taskFirst);
        manager.buildTask(taskSecond);
        manager.buildEpic(epicFirst);
        manager.buildEpic(epicSecond);
        manager.buildSubtask(subtaskFirst);
        manager.buildSubtask(subtaskSecond);
        manager.buildSubtask(subtaskThird);
        manager.receivingSubtasks(5);
        manager.receivingSubtasks(6);
        manager.receivingTasks(1);
        manager.receivingTasks(1);
        manager.receivingTasks(1);
        manager.receivingEpics(3);
        manager.receivingTasks(1);
        manager.receivingEpics(3);
        manager.receivingTasks(1);
        manager.receivingEpics(3);
        manager.receivingSubtasks(5);
        manager.receivingSubtasks(5);
        System.out.println(manager.getHistory());
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks()); */
        TaskManager manager = Managers.getDefault();
        Task taskFirst = new Task ("Сходить в школу", "Отсидеть уроки", NEW);
        Task taskSecond = new Task("Поехать на работу", "Сесть в автобус", DONE);
        Epic epicFirst = new Epic("Уборка дома", "Убраться в своей комнате, убраться на кухне, убраться в гостиной");
        Subtask subtaskFirst = new Subtask("Убрать свою комнату", "Заправить постель, выкинуть мусор, вытереть пыль", DONE, 3);
        Subtask subtaskSecond = new Subtask("Убраться на кухне", "Помыть посуду, помыть полы", NEW, 3);
        Subtask subtaskThird = new Subtask("Убраться в гостиной", "Помыть полы, сложить вещи в шкаф", NEW, 3);
        Epic epicSecond = new Epic("Написать код", "Сесть за компьютер");
        manager.buildTask(taskFirst);
        manager.buildTask(taskSecond);
        manager.buildEpic(epicFirst);
        manager.buildEpic(epicSecond);
        manager.buildSubtask(subtaskFirst);
        manager.buildSubtask(subtaskSecond);
        manager.buildSubtask(subtaskThird);

        manager.receivingTasks(1);
        manager.receivingTasks(1);
        System.out.println(manager.getHistory());
        manager.receivingEpics(3);
        manager.receivingEpics(3);
        System.out.println(manager.getHistory());
        manager.deleteTask(1);
        System.out.println(manager.getHistory());
        manager.deleteEpic(3);
        System.out.println(manager.getHistory());
    }
}