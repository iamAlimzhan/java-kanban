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
    private final static String HEADER = "id,type,name,status,description,epic\n";

    public FileBackedTasksManager(File file) {
        this.file = file;
    }
    static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            br.readLine(); // пропускаем первую строку с заголовком
            while (br.ready()) {
                String line = br.readLine();
                if (line.isEmpty()) {
                    break; // если строка пустая, выходим из цикла
                }
                lines.add(line);
            }
            for (int i = 0; i < lines.size() - 1; i++) {
                fromString(lines.get(i), fileManager);
            }
            fromStringHis(lines.get(lines.size() - 1), fileManager); // последняя строка - история
        } catch (IOException e) {
            throw new ManagerSaveException("К сожалению произошла ошибка :(");
        }
        return fileManager;
    }
    private static void fromStringHis(String line, FileBackedTasksManager fileBackedTasksManager) {
        String[] fields = line.split(",");
        for (String field : fields) {
            int id = Integer.parseInt(field);
            if(fileBackedTasksManager.tasks.containsKey(id)) {
                fileBackedTasksManager.historyManager.add(fileBackedTasksManager.tasks.get(id));
            }
            if(fileBackedTasksManager.subtasks.containsKey(id)) {
                fileBackedTasksManager.historyManager.add(fileBackedTasksManager.subtasks.get(id));
            }
            if(fileBackedTasksManager.epics.containsKey(id)) {
                fileBackedTasksManager.historyManager.add(fileBackedTasksManager.epics.get(id));
            }
        }
    }
     private static void fromString(String val, FileBackedTasksManager fileBackedTasksManager) {
         String[] lines = val.split(",");
         int id = Integer.parseInt(lines[0]);
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
                 int epicId = Integer.parseInt(lines[5]);
                 Subtask subtask = new Subtask(id, type, name, status, description, epicId);
                 fileBackedTasksManager.subtasks.put(id, subtask);
                 Epic ep = fileBackedTasksManager.epics.get(epicId);
                 if (ep != null) {
                     ep.addSubToEpic(subtask);
                 }
                 break;
             default:
                 throw new ManagerSaveException("К сожалению произошла ошибка :(");
         }
     }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            sb.append(HEADER);
            for (Task task : getTasks()) {
                toString(task, sb);
            }

            for (Epic epic : getEpics()) {
                toString(epic, sb);
            }
            for (Subtask subtask : getSubtasks()) {
                toString(subtask, sb);
            }
            List<Task> tasksHist = getHistory();
            if (!tasksHist.isEmpty()) {
                for (Task taskHis : tasksHist) {
                    sb.append(String.format("%s,", taskHis.getId()));
                }
                bw.write(sb.deleteCharAt(sb.length() - 1).toString());
            }
        } catch (IOException exp) {
            throw new ManagerSaveException("При сохранении файла произошла ошибка(");
        }
    }
     private void toString(Task task, StringBuilder sb) {
         sb.append(task.getId()).append(",").append(task.getTypeOfTask()).append(",").append(task.getTaskName())
                 .append(",").append(task.getStatus()).append(",").append(task.getTaskDescription());
         if (task.getTypeOfTask() == TypeOfTask.SUBTASK) {
             sb.append(",");
             sb.append(((Subtask)task).getEpicId());
         }
         sb.append("\n");
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


    public static void main(String[] args){
        System.out.println("Поехали!");
        File file = new File(("data.csv"));
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        Task task = new Task("A", "AA", NEW);
        Task taskSecond = new Task("B", "BB", NEW);
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