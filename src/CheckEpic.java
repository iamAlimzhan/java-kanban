import server.KVServer;
import tasks.Epic;
import tasks.Task;
import tasks.TaskStatuses;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

public class CheckEpic {
    public static void main(String[] args) throws IOException {
        ArrayList<Task> taskList = new ArrayList<>();

        // Добавление некоторых задач в список
        taskList.add(new Task("name", "desc", 1 , TaskStatuses.NEW));
        taskList.add(new Task("name 2", "desc 2", 2, TaskStatuses.NEW));

        // Используем Gson для сериализации в JSON
        Gson gson = new Gson();
        String json = gson.toJson(taskList);
        System.out.println("JSON со списком задач:");
        System.out.println(json);

        // Для пустого списка задач
        ArrayList<Task> emptyTaskList = new ArrayList<>();
        String emptyJson = gson.toJson(emptyTaskList);
        System.out.println("JSON с пустым списком задач:");
        System.out.println(emptyJson);
       // new KVServer().start();
    }
}

