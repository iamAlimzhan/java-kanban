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
    private KVTaskClient client;
    private final String saveKey;
    private final Gson gson;

    public HTTPTasksManager(String saveKey, String url) {
        this(saveKey, url, false);
    }

    public HTTPTasksManager(String saveKey,String url ,boolean load) {
        super(new File(""));
        this.saveKey = saveKey;
        this.client = new KVTaskClient(url);
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
        if(load){
            loadingFromJson(saveKey);
        }
    }

//сохранение на сервер
    @Override
    public void save() {
        JsonObject output = new JsonObject();
        output.add("tasks", gson.toJsonTree(getTasks()));
        // Сохранение подзадач
        JsonArray subtaskArray = new JsonArray();
        for (Subtask subtask : subtasks.values()) {
            subtaskArray.add(gson.toJsonTree(subtask));
        }
        output.add("subtasks", subtaskArray);

        // Сохранение эпиков
        JsonArray epicArray = new JsonArray();
        for (Epic epic : epics.values()) {
            epicArray.add(gson.toJsonTree(epic));
        }
        output.add("epics", epicArray);
        JsonArray history = new JsonArray();
        output.add("history", history);
        for(MainTask task : historyManager.getHistory()){
            history.add(task.getId());
        }
        client.put(saveKey, output.toString());
    }
//создание с помощью данных новый экземпляр сенеджера
    private void loadingFromJson(String oldKey) {
        JsonElement element = JsonParser.parseString(client.load(oldKey));
        if (!element.isJsonObject()) {
            System.out.println("Вывод не похож с тем, который ожидался :(");
            return;
    }
        JsonObject object = element.getAsJsonObject();

    // Загрузка задач
        JsonArray taskArray = object.getAsJsonArray("tasks");
        for (JsonElement jsonElement : taskArray) {
            String typeOfTask = jsonElement.getAsJsonObject().get("type").getAsString();
            switch (TypeOfTask.valueOf(typeOfTask)) {
                case TASK:
                    buildTask(gson.fromJson(jsonElement, Task.class));
                    break;
                    case EPIC:
                        Epic epic = gson.fromJson(jsonElement, Epic.class);
                        buildEpic(epic);
                        for (Subtask subtask : epic.getEpicWithSubtask()) {
                            subtask.setEpic(epic);
                            subtasks.put(subtask.getId(), subtask);
                        }
                        break;
                        default:
                            System.out.println(typeOfTask + " ошибка типа задачи");
            }
    }
        updateSortedList();
        JsonArray jsonArrayHist = object.getAsJsonArray("history");
        for (JsonElement jsonElementHist : jsonArrayHist) {
            getTasks();
        }
    }
}
