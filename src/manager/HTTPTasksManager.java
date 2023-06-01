package manager;

import server.DurationAdapter;
import server.InstantAdapter;
import server.KVTaskClient;
import tasks.*;
import com.google.gson.*;

import java.io.File;
import java.time.Duration;
import java.time.Instant;

public class HTTPTasksManager extends FileBackedTasksManager{
    private static KVTaskClient kvTaskClient = new KVTaskClient("localhost");
    private final String saveKey;

    public HTTPTasksManager(String saveKey) {
        super(new File(""));
        this.saveKey = saveKey;
    }

    public HTTPTasksManager() {
        super(new File(""));
        saveKey = "HTTPTaskManager";
    }

//сохранение на сервер
    @Override
    public void save() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
        JsonObject output = new JsonObject();
        output.add("tasks", gson.toJsonTree(getTasks()));
        JsonArray history = new JsonArray();
        output.add("history", history);
        for(MainTask task : historyManager.getHistory()){
            history.add(task.getId());
        }
        kvTaskClient.put(saveKey, output.toString());
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public Task receivingTasks(int id) {
        Task task =  super.receivingTasks(id);
        save();
        return task;
    }

    @Override
    public void buildTask(Task task) {
        super.buildTask(task);
        save();
    }
//создание с помощью данных новый экземпляр сенеджера
    public static  HTTPTasksManager loadingFromJson(String oldKey, String newKey){
        HTTPTasksManager httpTasksManager = new HTTPTasksManager(newKey);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
        JsonElement element = JsonParser.parseString(kvTaskClient.load(oldKey));
        if(!element.isJsonObject()){
            System.out.println("ВЫвод не похож с тем, который ожидался :(");
            return null;
        }
        JsonObject object = element.getAsJsonObject();


        JsonArray jsonArray = object.getAsJsonArray("tasks");
        for (JsonElement jsonElement : jsonArray) {
            String typeOfTask = jsonElement.getAsJsonObject().get("type").getAsString();
            switch(TypeOfTask.valueOf(typeOfTask)){
                case TASK:
                    httpTasksManager.buildTask(gson.fromJson(jsonElement, Task.class));
                case EPIC:
                    Epic epic = gson.fromJson(jsonElement, Epic.class);
                    httpTasksManager.buildEpic(epic);
                    for (Subtask subtask : epic.getEpicWithSubtask()) {
                        subtask.setEpic(epic);
                        httpTasksManager.subtasks.put(subtask.getId(), subtask);
                    }
                    break;
                default:
                    System.out.println(typeOfTask + " ошибка типа задачи");
            }
        }
        httpTasksManager.updateSortedList();
        JsonArray jsonArrayHist = object.getAsJsonArray("history");
        for (JsonElement jsonElementHist : jsonArrayHist) {
            httpTasksManager.getTasks();
        }
        return httpTasksManager;
    }
}
