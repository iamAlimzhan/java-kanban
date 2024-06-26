package manager;

import com.google.gson.reflect.TypeToken;
import exceptions.ManagerSaveException;
import server.DurationAdapter;
import server.InstantAdapter;
import server.KVTaskClient;
import tasks.*;
import com.google.gson.*;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class HTTPTasksManager extends FileBackedTasksManager{
    private KVTaskClient client;
    Gson gson = new Gson();


    public HTTPTasksManager(String url, boolean isLoad) {
        super(new File(""));
        this.client = new KVTaskClient(url);

        if (isLoad) {
            try {
                load();
            } catch (ManagerSaveException e) {
                e.printStackTrace();
            }
        }
    }


    public void load() throws ManagerSaveException {
        String tasksForLoad = client.load("task");
        int maxId = 0;
        if (tasksForLoad != null) {
            ArrayList<Task> taskList = gson.fromJson(tasksForLoad, new TypeToken<ArrayList<Task>>() {
            }.getType());
            for (Task task : taskList) {
                int id = task.getId();
                tasks.put(id, task);
                if (maxId < id) {
                    maxId = id;
                }
            }
        }
        String epicsForLoad = client.load("epic");
        if (epicsForLoad != null) {
            ArrayList<Epic> epicList = gson.fromJson(epicsForLoad, new TypeToken<ArrayList<Epic>>() {
            }.getType());
            for (Epic epic : epicList) {
                int id = epic.getId();
                epics.put(id, epic);
                if (maxId < id) {
                    maxId = id;
                }
            }
        }
        String subtasksForLoad = client.load("subtask");
        if (subtasksForLoad != null) {
            ArrayList<Subtask> subList = gson.fromJson(subtasksForLoad, new TypeToken<ArrayList<Subtask>>() {
            }.getType());
            for (Subtask subtask : subList) {
                int id = subtask.getId();
                subtasks.put(id, subtask);
                if (maxId < id) {
                    maxId = id;
                }
            }
        }
        idTask = maxId;
        mainTasksTreeSet.addAll(tasks.values());
        mainTasksTreeSet.addAll(subtasks.values());
        String historyForLoad = client.load("history");
        if(historyForLoad != null){
            ArrayList<Task> historyList = gson.fromJson(historyForLoad, new TypeToken<ArrayList<Task>>() {
            }.getType());
            for (Task task : historyList) {
                receivingTasks(task.getId());
                receivingEpics(task.getId());
                receivingSubtasks(task.getId());
            }
        }
    }
    @Override
    public void save() {
        client.put("task", gson.toJson(tasks.values()));
        client.put("epic", gson.toJson(epics.values()));
        client.put("subtask", gson.toJson(subtasks.values()));
        client.put("history", gson.toJson(historyManager.getHistory()));
    }

}
