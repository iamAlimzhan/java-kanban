package manager;

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
    protected TreeSet<MainTask> mainTasksTreeSet = new TreeSet<>(Comparator.comparing((MainTask task) -> task.getStartTime(), Comparator.nullsLast(Comparator.naturalOrder())));


    //получения списков задач
    @Override
    public List<Task> getTasks(){
        List<Task> listOfTasks = new ArrayList<>();
        if(!tasks.isEmpty()) {
            listOfTasks.addAll(tasks.values());
        }
        return listOfTasks;
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
            mainTasksTreeSet.remove(epicId);
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
            epic.getEpicWithSubtask().clear();
        }
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
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
        Task oldTask = tasks.get(task.getId());
        deleteTask(task.getId());
        if (getPrioritizedTasks(task)) {
            mainTasksTreeSet.add(task);
            tasks.put(task.getId(), task);
            checkId(task.getId());
        } else {
            tasks.put(oldTask.getId(), oldTask);
            mainTasksTreeSet.add(oldTask);
        }
    }

    @Override
    public void buildEpic(Epic epic){
        epics.put(epic.getId(), epic);
        checkId(epic.getId());
    }
    @Override
    public void buildSubtask(Subtask subtask){
        Epic epic = epics.get(subtask.getEpicId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubToEpic(subtask);
        checkId(subtask.getId());
    }


    //обновление
    @Override
    public void updateTask(Task task){
        buildTask(task);
    }
    @Override
    public void updateEpic(Epic epic){
        epics.put(epic.getId(), epic);
        checkId(epic.getId());
    }
    @Override
    public void updateSubtask(Subtask subtask){
        Epic ep = epics.get(subtask.getEpicId());
        ep.getEpicWithSubtask().remove(subtasks.get(subtask.getId()));
        ep.addSubToEpic(subtask);
        subtasks.put(subtask.getId(),subtask);
        checkId(subtask.getId());
    }

    //Удаление по id
    @Override
    public void deleteTask(int id){
        historyManager.remove(id);
        tasks.remove(id);
    }
    @Override
    public void deleteEpic(int id){
        Epic epic = epics.get(id);
        historyManager.remove(id);
        for(Subtask subtask : epic.getEpicWithSubtask()){
            subtasks.remove(subtask.getId());
            historyManager.remove(subtask.getId());
        }
        epics.remove(id);
    }
    @Override
    public void deleteSubtask(int id){
        Subtask subtask = subtasks.remove(id);
        Epic ep = epics.get(subtask.getEpicId());
        historyManager.remove(id);
        ep.getEpicWithSubtask().remove(subtask);
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

     public boolean getPrioritizedTasks(MainTask mainTask) {

        if (mainTask == null) {
            return false;
        } else if (mainTask.getStartTime() == null) {
            return true;
        }
         Instant startTimeTask = mainTask.getStartTime();
        if (mainTask.getEndTime() == null) {
            for (MainTask task : mainTasksTreeSet) {
                Instant startTime = task.getStartTime();
                if (task.getEndTime() == null) {
                    continue;
                }
                Instant endTimeTask = task.getEndTime();
                if (startTimeTask.isBefore(startTime) && startTimeTask.isAfter(endTimeTask) || startTimeTask.equals(endTimeTask)) {
                    return false; //есть пересечение
                }
            }
            return true;
        } else {
            Instant endTask = mainTask.getEndTime();
            for (MainTask task : mainTasksTreeSet) {
                Instant startTime = task.getStartTime();
                if (task.getEndTime() == null) {
                    if (startTime.isAfter(endTask) && startTime.isBefore(startTimeTask) || endTask.equals(startTime)) {
                        return false; //есть пересечение
                    }
                    continue;
                }
                Instant endTime = task.getEndTime();
                if ((startTimeTask.isBefore(startTime) && startTimeTask.isAfter(endTime)) || (endTask.isAfter(startTime) && endTask.isBefore(endTime))) {
                    return false; //есть пересечение
                }
            }
        }
        return true; //персеения нет
    }

}