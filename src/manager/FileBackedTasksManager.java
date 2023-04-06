package manager;

import exceptions.ManagerSaveException;
import tasks.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tasks.TaskStatuses.DONE;
import static tasks.TaskStatuses.NEW;

public class FileBackedTasksManager extends InMemoryTaskManager{
    /*Привет) Из за учебы сильно отстал, немного задержался с этим проектом
    не понял как сделать мейн в этом классе, поэтому закомментировал его. Можно новодку какую нибудь?)
     */
    private File file;
    public String nameOfFile;
    public FileBackedTasksManager(File file){
        this.file = file;
        nameOfFile = "resources/data.scv";
        file = new File(nameOfFile);
        if(!file.isFile()){
            try{
                Path path = Files.createFile(Paths.get(nameOfFile));
            } catch (IOException exp){
                System.out.println("При создании файла произошла ошибка( " + exp.getMessage());
            }
        }
    }

    static FileBackedTasksManager  loadFromFile(File file){
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        // загрузка данных в manager
        /*String info = "";
        try {
            info = Files.readString(Path.of(file.getAbsolutePath()));
        } catch (IOException exp) {
            throw new ManagerSaveException("File reading error(");
        }

        List<String> taskList = new ArrayList<>();
        List<String> epicList = new ArrayList<>();
        List<String> subtaskList = new ArrayList<>();
        String[] lines = info.split("\n");
        String historyLines = "";
        boolean isTask = true;
        boolean isTitle = true;
        int peakId = 0, id;*/

        try(FileReader fr = new FileReader(file, StandardCharsets.UTF_8)) {
            BufferedReader bfr = new BufferedReader(fr);
            while (bfr.ready()){
                String line = bfr.readLine();
                String[] split = line.split(",");

                if(!line.isEmpty()){
                    Integer id = Integer.parseInt(split[0]);
                    TypeOfTask type = TypeOfTask.valueOf(split[1]);
                    String name = split[2];
                    TaskStatuses status = TaskStatuses.valueOf(split[3]);
                    String description = split[4];
                    Integer epicId = Integer.parseInt(split[5]);

                    switch (type){
                        case EPIC:
                            fileBackedTasksManager.getEpicHashMap().put(id, new Epic(id, name, description, status));
                            break;
                        case SUBTASK:
                            fileBackedTasksManager.getSubtaskMap().put(id, new Subtask(id, name, description, status, epicId));
                            break;
                        case TASK:
                            fileBackedTasksManager.getTaskHashMap().put(id, new Task(id, name, description, status));
                            break;
                    }
                    for (String elementsId : split) {
                        int tasksID = Integer.parseInt(elementsId);
                        fileBackedTasksManager.historyManager.add(fileBackedTasksManager.receivingTasks(tasksID));
                        fileBackedTasksManager.historyManager.add(fileBackedTasksManager.receivingEpics(tasksID));
                        fileBackedTasksManager.historyManager.add(fileBackedTasksManager.receivingSubtasks(tasksID));
                    }
                }
            }

        }catch (IOException exp){
            System.out.println("Произошла ошибка: " + exp.getMessage());
        }

        return fileBackedTasksManager;


    }

    private String toString(HistoryManager historyManager) {
        List<String> hist = new ArrayList<>();
        for (Task task : historyManager.getHistory()) {
            hist.add(String.valueOf(task.getId()));
        }
        String id = String.join(",", hist);
        return id;
    }
    public void save() throws ManagerSaveException {

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(nameOfFile , StandardCharsets.UTF_8))){
            bw.write("type,name,status,description,epic\n");
            Map<Integer,String> allTasksHMap= new HashMap<>();

            Map<Integer,Task> tasksMap = getTaskHashMap();
            for (Integer id : tasksMap.keySet()) {
                allTasksHMap.put(id, tasksMap.get(id).toStringTask());
            }

            Map<Integer, Epic> epicsHMap = getEpicHashMap();
            for (Integer id : epicsHMap.keySet()) {
                allTasksHMap.put(id, epicsHMap.get(id).toStringTask());
            }

            Map<Integer, Subtask> subtasksHMap = getSubtaskMap();
            for (Integer id : subtasksHMap.keySet()) {
                allTasksHMap.put(id, subtasksHMap.get(id).toStringTask());
            }
            for (String val : allTasksHMap.values()) {
                bw.write(String.format("%s\n", val));
            }
            bw.write("\n");
            bw.write(toString(this.historyManager));

        } catch (IOException exp){
            System.out.println("При сохранении файла произошла ошибка( " + exp.getMessage());
        }

    }

    @Override
    public void delTasks() {
        super.delTasks();
    }

    @Override
    public void delEpics() {
        super.delEpics();
    }

    @Override
    public void delSubtasks() {
        super.delSubtasks();
    }

    @Override
    public void buildTask(Task task) {
        super.buildTask(task);
    }

    @Override
    public void buildEpic(Epic epic) {
        super.buildEpic(epic);
    }

    @Override
    public void buildSubtask(Subtask subtask) {
        super.buildSubtask(subtask);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
    }

    @Override
    public Epic receivingEpics(int id) {
        return super.receivingEpics(id);
    }

    @Override
    public Subtask receivingSubtasks(int id) {
        return super.receivingSubtasks(id);
    }

    @Override
    public List<Subtask> receivingSubInEpic(int id) {
        return super.receivingSubInEpic(id);
    }

    /*public static void main(String[] args){
        File files = new File("src/resources/Tasks.csv");
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
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
    }*/
}

