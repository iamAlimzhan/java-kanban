package manager;

import exceptions.ManagerSaveException;
import tasks.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import static tasks.TaskStatuses.DONE;
import static tasks.TaskStatuses.NEW;

 public class FileBackedTasksManager extends InMemoryTaskManager {
     private final File file;
     private final static String HEADER = "id,type,name,status,description,epic,startTime,duration\n";

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

     //метод для создания новой задачи и загрузки данных из указанного файла
    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            br.readLine(); // пропускаем первую строку с заголовком
            while (br.ready()) {
                String line = br.readLine();
                if (line.isEmpty()) {
                    break; // если строка пустая, выходим из цикла
                }
                fromString(line, fileManager);
            }
            if (br.ready()) {
                fromStringHis(br.readLine(), fileManager);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("К сожалению произошла ошибка :(");
        }
        return fileManager;
    }
     // метод для анализа данных истории из строки и добавления соответствующих задач
     private static void fromStringHis(String line, FileBackedTasksManager fileBackedTasksManager) {
         String[] fields = line.split(",");
         for (String field : fields) {
             int id = Integer.parseInt(field);
             if (fileBackedTasksManager.tasks.containsKey(id)) {
                 fileBackedTasksManager.historyManager.add(fileBackedTasksManager.tasks.get(id));
             } else if (fileBackedTasksManager.subtasks.containsKey(id)) {
                 fileBackedTasksManager.historyManager.add(fileBackedTasksManager.subtasks.get(id));
             } else if (fileBackedTasksManager.epics.containsKey(id)) {
                 fileBackedTasksManager.historyManager.add(fileBackedTasksManager.epics.get(id));
             }
         }
     }
     //метод для анализа данных из строки и создания соответствующих задач
     private static void fromString(String val, FileBackedTasksManager fileBackedTasksManager) {
         String[] lines = val.split(",");
         int id = Integer.parseInt(lines[0]);
         fileBackedTasksManager.idTask = Math.max(fileBackedTasksManager.idTask, id);
         TypeOfTask type = TypeOfTask.valueOf(lines[1]);
         String name = lines[2];
         TaskStatuses status = TaskStatuses.valueOf(lines[3]);
         String description = lines[4];
         Instant startTime = null;
         if(!lines[5].equals("null")) {
             startTime = Instant.ofEpochMilli(Long.parseLong(lines[5]));
         }
         long duration = Long.parseLong(lines[6]);
         switch (type) {
             case TASK:
                 fileBackedTasksManager.tasks.put(id, new Task(name, description, id, status, startTime, duration));
                 break;
             case EPIC:
                 Epic epic = new Epic(name, description, id, status);
                 fileBackedTasksManager.epics.put(id, epic);
                 break;
             case SUBTASK:
                 int epicId = Integer.parseInt(lines[7]);
                 Subtask subtask = new Subtask(name, description, id, status, epicId);
                 subtask.setStartTime(startTime);
                 subtask.setDuration(duration);
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

     //метод отвечает за сохранение задач, эпиков и подзадач обратно в файл в формате CSV
     public void save() {
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
            List<MainTask> tasksHist = getHistory();
            sb.append("\n");
            if (!tasksHist.isEmpty()) {
                for (MainTask taskHis : tasksHist) {
                    sb.append(String.format("%s,", taskHis.getId()));
                }
                sb.deleteCharAt(sb.length() - 1);
            }
            bw.write(String.valueOf(sb));
        } catch (IOException exp) {
            throw new ManagerSaveException("При сохранении файла произошла ошибка(");
        }
    }
     private void toString(MainTask task, StringBuilder sb) {
         sb.append(task.getId()).append(",").append(task.getTypeOfTask()).append(",").append(task.getTaskName())
                 .append(",").append(task.getStatus()).append(",").append(task.getTaskDescription()).append(",").
                 append(task.getStartTime() == null?"null" : task.getStartTime().toEpochMilli()).append(","). append(task.getDuration());
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

     @Override
     public Task receivingTasks(int id) {
        Task task = super.receivingTasks(id);
        save();
         return task;
     }
}