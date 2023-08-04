package manager;

import exceptions.CreateException;
import tasks.*;

import java.time.Instant;
import java.util.*;

import static tasks.TaskStatuses.*;
public class InMemoryTaskManager implements TaskManager {
    protected int idTask = 0;
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    protected TreeSet<MainTask> mainTasksTreeSet = new TreeSet<>(Comparator.comparing(MainTask::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(MainTask::getId));


    //получения списков задач
    @Override
    public List<Task> getTasks(){
        return new ArrayList<>(tasks.values());
    }
    @Override
    public List<Subtask> getSubtasks(){
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics(){
        return new ArrayList<>(epics.values());
    }

    //удаление задач
    @Override
    public void delTasks(){
        for (Task taskId : tasks.values()) {
            mainTasksTreeSet.remove(taskId);
            historyManager.remove(taskId.getId());
        }
        tasks.clear();
    }
    @Override
    public void delEpics(){
        for (Epic epicId : epics.values()) {
            historyManager.remove(epicId.getId());
        }
        for (Subtask subtaskId : subtasks.values()) {
            mainTasksTreeSet.remove(subtaskId);
            historyManager.remove(subtaskId.getId());
        }
        epics.clear();
        subtasks.clear();
    }
    @Override
    public void delSubtasks(){
        for (Epic epic : epics.values()) {
            epic.clearEpicSubtasks();
        }
        for (Subtask subtaskId : subtasks.values()) {
            mainTasksTreeSet.remove(subtaskId);
            historyManager.remove(subtaskId.getId());
        }
        subtasks.clear();
    }
    //получение через id
    @Override
    public Task receivingTasks(int id){
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }
    @Override
    public Epic receivingEpics(int id){
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }
    @Override
    public Subtask receivingSubtasks(int id){
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }
    // Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public void buildTask(Task task) {
        if (task == null) {
            return;
        }
        task.setId(++idTask);
        deleteTask(task.getId());
        if (checkValidation(task)) {
            mainTasksTreeSet.add(task);
            tasks.put(task.getId(), task);
            checkId(task.getId());
        } else {
            throw new CreateException("Ошибка валидации");
        }
    }

    @Override
    public void buildEpic(Epic epic){
        if (epic == null){
            return;
        }
        epic.setId(++idTask);
        if (checkValidation(epic)) {
            mainTasksTreeSet.add(epic);
            epics.put(epic.getId(), epic);
            checkId(epic.getId());
        } else {
            throw new CreateException("Ошибка валидации");
        }
    }
    @Override
    public void buildSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }
        subtask.setId(++idTask); // Установка идентификатора
        Epic epic = epics.get(subtask.getEpicId());
        if (!checkValidation(subtask)) {
            throw new CreateException("Ошибка валидации");
        }
        subtasks.put(subtask.getId(), subtask);
        epic.addSubToEpic(subtask);
        mainTasksTreeSet.add(subtask);
        checkId(subtask.getId());
    }

    //обновление
    @Override
    public void updateTask(Task task) {
        if (task == null) {
            return;
        }

        int taskId = task.getId();
        Task existingTask = tasks.get(taskId);
        if (existingTask == null) {
            return;
        }

        if (checkValidation(task)) {
            mainTasksTreeSet.remove(existingTask); // Удалить старую версию задачи из отсортированного списка
            mainTasksTreeSet.add(task); // Добавить обновленную задачу в отсортированный список
            tasks.put(taskId, task); // Обновить задачу в мапе
        } else {
            throw new CreateException("Ошибка валидации");
        }
    }

    @Override
    public void updateEpic(Epic epic){
        epics.put(epic.getId(), epic);
    }
    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }

        int subtaskId = subtask.getId();
        Subtask existingSubtask = subtasks.get(subtaskId);
        if (existingSubtask == null) {
            return;
        }

        // Проверка на пересечение существующих задач
        if (!checkValidation(subtask)) {
            throw new CreateException("Ошибка валидации");
        }

        Epic ep = epics.get(subtask.getEpicId());
        ep.getEpicWithSubtask().remove(existingSubtask); // Удалить старую версию задачи из списка подзадач эпика
        ep.addSubToEpic(subtask); // Добавить обновленную задачу в список подзадач эпика
        subtasks.put(subtaskId, subtask); // Обновить задачу в мапе подзадач
        mainTasksTreeSet.remove(existingSubtask); // Удалить старую версию задачи из отсортированного списка
        mainTasksTreeSet.add(subtask); // Добавление обновленной задачи в отсортированный список
    }

    //Удаление по id
    @Override
    public void deleteTask(int id) {
        historyManager.remove(id);
        tasks.remove(id);
        mainTasksTreeSet.removeIf(task -> task.getId() == id); // удалить задачу из отсортированного списка
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        historyManager.remove(id);

        for (Subtask subtask : epic.getEpicWithSubtask()) {
            subtasks.remove(subtask.getId());
            historyManager.remove(subtask.getId());
            mainTasksTreeSet.remove(subtask); // Удалить подзадачу из отсортированного списка
        }

    }

    @Override
    public void deleteSubtask(int id){
        Subtask subtask = subtasks.remove(id);
        Epic ep = epics.get(subtask.getEpicId());
        historyManager.remove(id);
        ep.removeEpicWithSubtask(subtask);
        mainTasksTreeSet.remove(subtask); // Удалить подзадачу из отсортированного списка
        // не понял как обновить статус эпика и времени
    }

    //Получение списка всех подзадач определённого эпика.
    @Override
    public List<Subtask> receivingSubInEpic(int id){
        Epic ep = epics.get(id);
        return ep.getEpicWithSubtask();
    }

    @Override
    public List<MainTask> getHistory(){
        return historyManager.getHistory();
    }

    private void checkId(int id){
        if(id > idTask){
            idTask = id;
        }
    }

     public boolean checkValidation(MainTask mainTask) {
         if (mainTask == null) {
             return false;
         } else if (mainTask.getStartTime() == null) {
             return true;
         }

         Instant startTimeTask = mainTask.getStartTime();
         Instant endTimeTask = mainTask.getEndTime();

         for (MainTask task : getPrioritizedTasks()) {
             if (task.getId() == mainTask.getId()) {
                 continue;
             }

             Instant startTime = task.getStartTime();
             Instant endTime = task.getEndTime();
             if (endTime == null || startTime == null) {
                 return true;
             }
             if (!endTime.isAfter(startTimeTask) || !startTime.isBefore(endTimeTask)) {
                 continue;
             }
                 return false; // есть пересечение
         }

         return true; // пересечения нет
    }
    public void updateSortedList(){
        mainTasksTreeSet.clear();
        for (Task value : tasks.values()) {
            if(value.getType() != TypeOfTask.EPIC){
                mainTasksTreeSet.add(value);
            }
        }
    }
    @Override
    public List<MainTask> getPrioritizedTasks(){
        List<MainTask> tasksAndSubtasksList = new ArrayList<>();
        tasksAndSubtasksList.addAll(tasks.values());
        tasksAndSubtasksList.addAll(epics.values());
        tasksAndSubtasksList.addAll(subtasks.values());
        mainTasksTreeSet.addAll(tasksAndSubtasksList);
        return new ArrayList<>(mainTasksTreeSet);
    }

}