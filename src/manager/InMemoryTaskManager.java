package manager;
import tasks.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static tasks.TaskStatuses.*;


public class InMemoryTaskManager implements TaskManager {
    private int idTask = 0;

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();

    //получения списков задач
    @Override
    public ArrayList<Task> getTasks(){

        return new ArrayList<>(tasks.values());
    }
    @Override
    public ArrayList<Subtask> getSubtasks(){
        return new ArrayList<>(subtasks.values());
    }
    @Override
    public ArrayList<Epic> getEpics(){

        return new ArrayList<>(epics.values());
    }


    //удаление задач
    @Override
    public void delTasks(){
        tasks.clear();
    }
    @Override
    public void delEpics(){
        epics.clear();
        subtasks.clear();
    }
    @Override
    public void delSubtasks(){
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getEpicWithSubtask().clear();
            statusManager(epic);
        }
    }
    //получение через id
    @Override
    public Task receivingTasks(int id){
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }
    @Override
    public Epic receivingEpics(int id){
        historyManager.add(epics.get(id));
        return epics.get(id);
    }
    @Override
    public Subtask receivingSubtasks(int id){
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }
    // Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public void buildTask(Task task){
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
        tasks.remove(id);
    }
    @Override
    public void deleteEpic(int id){
        Epic epic = epics.get(id);
        for(Subtask subtask : epic.getEpicWithSubtask()){
            subtasks.remove(subtask.getId());
        }
        epics.remove(id);
    }
    @Override
    public void deleteSubtask(int id){
        Subtask subtask = subtasks.remove(id);
        Epic ep = epics.get(subtask.getEpicId());
        ep.getEpicWithSubtask().remove(subtask);
        statusManager(ep);
    }
    //Получение списка всех подзадач определённого эпика.
    @Override
    public ArrayList<Subtask> receivingSubInEpic(int id){
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
        ArrayList<Subtask> subInEpic = epic.getEpicWithSubtask();
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
