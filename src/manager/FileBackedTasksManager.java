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
    private final File file;
    private final static String HEADING= "type,name,status,description,epic\n";
    public FileBackedTasksManager(File file){
        this.file = file;
    }

    static FileBackedTasksManager  loadFromFile(File file){
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        // загрузка данных в manager
        try(FileReader fr = new FileReader(file, StandardCharsets.UTF_8)) {
            BufferedReader bfr = new BufferedReader(fr);
            while (bfr.ready()){
                String line = bfr.readLine();
                String[] split = line.split(",");
                for (String elementsId : split) {
                    int tasksID = Integer.parseInt(elementsId);
                    fileBackedTasksManager.historyManager.add(fileBackedTasksManager.receivingTasks(tasksID));
                    fileBackedTasksManager.historyManager.add(fileBackedTasksManager.receivingEpics(tasksID));
                    fileBackedTasksManager.historyManager.add(fileBackedTasksManager.receivingSubtasks(tasksID));
                }
            }

        }catch (IOException exp){
            System.out.println("Произошла ошибка: " + exp.getMessage());
        }

        return fileBackedTasksManager;

    }

    private static Task fromString(String val){
        String[] split = val.split(",");
            Integer id = Integer.parseInt(split[0]);
            TypeOfTask type = TypeOfTask.valueOf(split[1]);
            String name = split[2];
            TaskStatuses status = TaskStatuses.valueOf(split[3]);
            String description = split[4];
            Integer epicId = Integer.parseInt(split[5]);

            switch (type){
                case EPIC:
                    return new Epic(id, name, description, status);
                case SUBTASK:
                    return new Subtask(id, name, description, status, epicId);
                case TASK:
                    return new Task(id, name, description, status);
                default:
                    return null;
            }
    }

    private String toString(HistoryManager historyManager) {
        List<String> hist = new ArrayList<>();
        for (Task task : historyManager.getHistory()) {
            hist.add(String.valueOf(task.getId()));
        }
        return String.join(",", hist);
    }
    private void save(){

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))){
            bw.write(HEADING);
            StringBuilder stringBuilder = new StringBuilder("id,type,name,status,description,epic\n");

            List<Task> tasksList = getTasks();
            for (Task task : tasksList) {
                stringBuilder.append(task.toString()).append("\n");
            }

            List<Epic> epicsList = getEpics();
            for (Epic epic : epicsList) {
                stringBuilder.append(epic.toString()).append("\n");
            }

            List<Subtask> subtasksList = getSubtasks();
            for (Subtask subtask : subtasksList) {
                stringBuilder.append(subtask.toString()).append("\n");
            }
            bw.append("\n");
            List<Task> tasksHist = getHistory();
            if (!tasksHist.isEmpty()) {
                for (Task taskHis : tasksHist) {
                    stringBuilder.append(taskHis.getId()).append(",");
                }
                bw.write(stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString());
            }
        } catch (IOException exp){
            throw new ManagerSaveException("При сохранении файла произошла ошибка(");
        }
    }


    @Override
    public void delTasks() {
        super.delTasks();
        save();
    }

    @Override
    public void delEpics() {
        super.delEpics();
        save();
    }

    @Override
    public void delSubtasks() {
        super.delSubtasks();
        save();
    }

    @Override
    public void buildTask(Task task) {
        super.buildTask(task);
        save();
    }

    @Override
    public void buildEpic(Epic epic) {
        super.buildEpic(epic);
        save();
    }

    @Override
    public void buildSubtask(Subtask subtask) {
        super.buildSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public Epic receivingEpics(int id) {
        Epic epic = super.receivingEpics(id);
        save();
        return epic;
    }

    @Override
    public Subtask receivingSubtasks(int id) {
        Subtask subtask = super.receivingSubtasks(id);
        save();
        return subtask;
    }

    @Override
    public List<Subtask> receivingSubInEpic(int id) {
        List<Subtask> subtask = super.receivingSubInEpic(id);
        save();
        return subtask;
    }

    public static void main(String[] args){
        File file = new File("resources/tasks.csv");
        FileBackedTasksManager inMemoryTaskManager = new FileBackedTasksManager(file);
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

        FileBackedTasksManager readManager = FileBackedTasksManager.loadFromFile(file);

        System.out.println("Проверка задач: " + readManager.tasks.equals(readManager.tasks));
        System.out.println("Проверка эпиков: " + readManager.epics.equals(readManager.epics));
        System.out.println("Проверка подзадач: " + readManager.subtasks.equals(readManager.subtasks));
        System.out.println("Проверка истории: " + readManager.getHistory().equals(readManager.getHistory()));
        System.out.println("Проверка истории: " + (readManager.idTask == readManager.idTask));
        System.out.println();
        for (Task task : readManager.getTasks()) {
            System.out.println(task);
        }
        for (Subtask subtask : readManager.getSubtasks()) {
            System.out.println(subtask);
        }
        for (Epic epic : readManager.getEpics()) {
            System.out.println(epic);
        }
        System.out.println();
        for (Task task : readManager.getHistory()) {
            System.out.println(task);
        }
    }
}

