package manager;

import tasks.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static tasks.TaskStatuses.*;

public class InMemoryTaskManager implements TaskManager {
    protected int idTask = 0;
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();

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
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }
    @Override
    public void delEpics(){
        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }
        epics.clear();
        subtasks.clear();
    }
    @Override
    public void delSubtasks(){
        for (Epic epic : epics.values()) {
            epic.getEpicWithSubtask().clear();
            statusManager(epic);
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
        task.setId(++idTask);
        tasks.put(task.getId(),task);
    }
    @Override
    public void buildEpic(Epic epic){
        epic.setId(++idTask);
        epics.put(epic.getId(), epic);
    }
    @Override
    public void buildSubtask(Subtask subtask){
        Epic epic = epics.get(subtask.getEpicId());
        subtask.setId(++idTask);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubToEpic(subtask);
        statusManager(epic);
    }


    //обновление
    @Override
    public void updateTask(Task task){
        tasks.put(task.getId(), task);
    }
    @Override
    public void updateEpic(Epic epic){
        epics.put(epic.getId(), epic);
    }
    @Override
    public void updateSubtask(Subtask subtask){
        Epic ep = epics.get(subtask.getEpicId());
        ep.getEpicWithSubtask().remove(subtasks.get(subtask.getId()));
        ep.addSubToEpic(subtask);
        subtasks.put(subtask.getId(),subtask);
        statusManager(ep);
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
        statusManager(ep);
    }
    //Получение списка всех подзадач определённого эпика.
    @Override
    public List<Subtask> receivingSubInEpic(int id){
        Epic ep = epics.get(id);
        return ep.getEpicWithSubtask();
    }

    @Override
    public List<Task> getHistory(){
        return historyManager.getHistory();
    }
    private void statusManager(Epic epic){
        int newStatus = 0;
        int done = 0;
        List<Subtask> subInEpic = epic.getEpicWithSubtask();
        if(subInEpic.isEmpty()){
            epic.setStatus(NEW);
        }else{
            for (Subtask subtask : subInEpic) {
                if(subtask.getStatus().equals(NEW)){
                    newStatus ++;
                } else if(subtask.getStatus().equals(DONE)){
                    done ++;
                } else {
                    epic.setStatus(IN_PROGRESS);
                    return;
                }
            }
            if (newStatus == subInEpic.size()){
                epic.setStatus(NEW);
            } else if (done == subInEpic.size()){
                epic.setStatus(DONE);
            } else {
                epic.setStatus(IN_PROGRESS);

            }
        }
    }
}
