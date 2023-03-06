import manager.*;
import tasks.*;
import static tasks.TaskStatuses.DONE;
import static tasks.TaskStatuses.NEW;

public class Main {

    public static void main(String[] args) {
        //System.out.println("Поехали!");
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