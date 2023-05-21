package manager;
import tasks.*;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    //получения списков задач
     List<Task> getTasks();
     List<Subtask> getSubtasks();
     List<Epic> getEpics();

    //удаление задач
    void delTasks();
    void delEpics();
    void delSubtasks();
    //получение через id
    Task receivingTasks(int id);
    Epic receivingEpics(int id);
    Subtask receivingSubtasks(int id);
   // Создание. Сам объект должен передаваться в качестве параметра.
    void buildTask(Task task);
    void buildEpic(Epic epic);
    void buildSubtask(Subtask subtask);

    //обновление
    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubtask(Subtask subtask);

    //Удаление по id
    void deleteTask(int id);
    void deleteEpic(int id);
    void deleteSubtask(int id);
    //Получение списка всех подзадач определённого эпика.
    List<Subtask> receivingSubInEpic(int id);
    List<MainTask> getHistory();

    default boolean getPrioritizedTasks(MainTask mainTask) { // я не совсем понял как сделать метод, с помощью которого мы будем получать задачи в отсортированном порядке
        return false;
    }

}
