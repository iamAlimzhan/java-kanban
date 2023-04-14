 package manager;

import exceptions.ManagerSaveException;
import tasks.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import static tasks.TaskStatuses.DONE;
import static tasks.TaskStatuses.NEW;

 public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;
    private static StringBuilder STRING_BUILDER = new StringBuilder("id,type,name,status,description,epic\n");

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (br.ready()) {
                String line = br.readLine();
                if (!line.isEmpty() && !line.contains("id") && !isNumber(line)) {
                    fromString(line, fileManager);
                }
                if (isNumber(line)) {
                    fromStringHis(line, fileManager);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("К сожалению произошла ошибка :(");

        }
        return fileManager;
    }
    public static boolean isNumber(String line) {
        try {
            Integer.parseInt(line.replace(",", ""));
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
    private static void fromStringHis(String line, FileBackedTasksManager fileBackedTasksManager){
        String[] fields = line.split(",");
        for (String field : fields) {
            if(fileBackedTasksManager.tasks.containsValue(fileBackedTasksManager.receivingTasks(Integer.valueOf(field)))){
                fileBackedTasksManager.historyManager.add(fileBackedTasksManager.receivingTasks(Integer.valueOf(field)));
            }
            if(fileBackedTasksManager.subtasks.containsValue(fileBackedTasksManager.receivingSubtasks(Integer.valueOf(field)))){
                fileBackedTasksManager.historyManager.add(fileBackedTasksManager.receivingSubtasks(Integer.valueOf(field)));
            }
            if(fileBackedTasksManager.epics.containsValue(fileBackedTasksManager.receivingEpics(Integer.valueOf(field)))){
                fileBackedTasksManager.historyManager.add(fileBackedTasksManager.receivingEpics(Integer.valueOf(field)));
            }

        }
    }
     private static void fromString(String val, FileBackedTasksManager fileBackedTasksManager) {
         String[] lines = val.split(",");
         Integer id = Integer.parseInt(lines[0]);
         fileBackedTasksManager.idTask = Math.max(fileBackedTasksManager.idTask, id);
         TypeOfTask type = TypeOfTask.valueOf(lines[1]);
         String name = lines[2];
         TaskStatuses status = TaskStatuses.valueOf(lines[3]);
         String description = lines[4];
         switch (type) {
             case TASK:
                 fileBackedTasksManager.tasks.put(id, new Task(id, type, name, status, description));
                 break;
             case EPIC:
                 Epic epic = new Epic(id, type, name, status, description);
                 fileBackedTasksManager.epics.put(id, epic);
                 break;
             case SUBTASK:
                 Integer epicId = Integer.parseInt(lines[5]);
                 Subtask subtask = new Subtask(id, type, name, status, description, epicId);
                 fileBackedTasksManager.subtasks.put(id, subtask);
                 Epic epics = fileBackedTasksManager.epics.get(epicId);
                 if (epics != null) {
                     epics.addSubToEpic(subtask);
                 }
                 break;
             default:
                 throw new ManagerSaveException("К сожалению произошла ошибка :(");
         }
     }

    private void save() {
        STRING_BUILDER = new StringBuilder("id,type,name,status,description,epic\n");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            List<Task> tasksList = getTasks();
            for (Task task : tasksList) {
                STRING_BUILDER.append(task.getId()).append(",");
                STRING_BUILDER.append(task.getTypeOfTask()).append(",");
                STRING_BUILDER.append(task.getTaskName()).append(",");
                STRING_BUILDER.append(task.getStatus()).append(",");
                STRING_BUILDER.append(task.getTaskDescription()).append("\n");
            }
            List<Epic> epicsList = getEpics();
            for (Epic epic : epicsList) {
                STRING_BUILDER.append(epic.getId()).append(",");
                STRING_BUILDER.append(epic.getTypeOfTask()).append(",");
                STRING_BUILDER.append(epic.getTaskName()).append(",");
                STRING_BUILDER.append(epic.getStatus()).append(",");
                STRING_BUILDER.append(epic.getTaskDescription()).append("\n");
            }
            List<Subtask> subtasksList = getSubtasks();
            for (Subtask subtask : subtasksList) {
                STRING_BUILDER.append(subtask.getId()).append(",");
                STRING_BUILDER.append(subtask.getTypeOfTask()).append(",");
                STRING_BUILDER.append(subtask.getTaskName()).append(",");
                STRING_BUILDER.append(subtask.getStatus()).append(",");
                STRING_BUILDER.append(subtask.getTaskDescription()).append(",");
                STRING_BUILDER.append(subtask.getEpicId()).append("\n");
            }
            List<Task> tasksHist = getHistory();
            if (!tasksHist.isEmpty()) {
                for (Task taskHis : tasksHist) {
                    STRING_BUILDER.append(String.format("%s,", taskHis.getId()));
                }
                bw.write(STRING_BUILDER.deleteCharAt(STRING_BUILDER.length() - 1).toString());
            }
        } catch (IOException exp) {
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
        System.out.println("Поехали!");
        File file = new File(("data.csv"));
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        Task task = new Task("A", "AA", NEW);
        Task taskSecond = new Task("B", "BB", DONE);
        Epic epic = new Epic("C", "CC");
        Subtask subtask = new Subtask("C.1", "CC.1", DONE, 3);
        Subtask subtaskSecond = new Subtask("C.2", "CC.2", NEW, 3);
        Subtask subtaskThird = new Subtask("C.3", "CC.3", NEW, 3);
        Epic epicSecond = new Epic("D", "DD");
        //Task task2 = new Task("Тест", "Проверка", DONE);
        manager.buildTask(task);
        manager.buildTask(taskSecond);
        manager.buildEpic(epic);
        manager.buildEpic(epicSecond);
        manager.buildSubtask(subtask);
        manager.buildSubtask(subtaskSecond);
        manager.buildSubtask(subtaskThird);
        manager.receivingTasks(1);
        manager.receivingTasks(1);
        manager.receivingTasks(1);
        manager.receivingTasks(2);
        manager.receivingEpics(3);
        manager.receivingSubtasks(5);
        FileBackedTasksManager readManager = FileBackedTasksManager.loadFromFile(file);
        //fileBackedTasksManager2.createTask(task2);
        System.out.println(readManager.tasks);
        System.out.println(manager.getHistory());
        System.out.println(readManager.getHistory());

        System.out.println("Проверка задач: " + manager.tasks.equals(readManager.tasks));
        System.out.println("Проверка эпиков: " + manager.epics.equals(readManager.epics));
        System.out.println("Проверка подзадач: " + manager.subtasks.equals(readManager.subtasks));
        System.out.println("Проверка истории: " + manager.getHistory().equals(readManager.getHistory()));
        System.out.println("manager.idTask: " + manager.idTask);
        System.out.println("readManager.idTask: " + readManager.idTask);
        System.out.println("Проверка истории айди: " + (manager.idTask == readManager.idTask));
        System.out.println(manager.epics);
        System.out.println(readManager.epics);
        System.out.println(manager.getHistory());
        System.out.println(readManager.getHistory());

    }
}