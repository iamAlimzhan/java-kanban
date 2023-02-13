import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int idTask = 0;

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    //получения списков задач
    public ArrayList<Task>  getTasks(){
        return new ArrayList<>(tasks.values());
    }
    public ArrayList<Subtask> getSubtasks(){
        return new ArrayList<>(subtasks.values());
    }
    public ArrayList<Epic> getEpics(){

        return new ArrayList<>(epics.values());
    }

    //удаление задач
    public void delTasks(){
        tasks.clear();
    }
    public void delEpics(){
        epics.clear();
        subtasks.clear();
    }
    public void delSubtasks(){
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getEpicWithSubtask().clear();
            statusManager(epic);
        }
    }
    //получение через id
    public Task receivingTasks(int id){

        return tasks.get(id);
    }
    public Epic receivingEpics(int id){

        return epics.get(id);
    }
    public Subtask receivingSubtasks(int id){

        return subtasks.get(id);
    }
   // Создание. Сам объект должен передаваться в качестве параметра.
    public void buildTask(Task task){
        task.setId(++idTask);
        tasks.put(task.getId(),task);
    }
    public void buildEpic(Epic epic){
        epic.setId(++idTask);
        epics.put(epic.getId(), epic);
    }
    public void buildSubtask(Subtask subtask){
        Epic epic = epics.get(subtask.getEpicId());
        subtask.setId(++idTask);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubToEpic(subtask);
        statusManager(epic);
    }

    //обновление
    public void updateTask(Task task){
        tasks.put(task.getId(), task);
    }
    public void updateEpic(Epic epic){
        epics.put(epic.getId(), epic);
    }
    public void updateSubtask(Subtask subtask){
        Epic ep = epics.get(subtask.getEpicId());
        ep.getEpicWithSubtask().remove(subtasks.get(subtask.getId()));
        ep.addSubToEpic(subtask);
        subtasks.put(subtask.getId(),subtask);
        statusManager(ep);
    }

    //Удаление по id
    public void deleteTask(int id){
        tasks.remove(id);
    }
    public void deleteEpic(int id){
        Epic epic = epics.get(id);
        for(Subtask subtask : epic.getEpicWithSubtask()){
            subtasks.remove(subtask.getId());
        }
        epics.remove(id);
    }
    public void deleteSubtask(int id){
        Subtask subtask = subtasks.remove(id);
        Epic ep = epics.get(subtask.getEpicId());
        ep.getEpicWithSubtask().remove(subtask);
        statusManager(ep);
    }
    //Получение списка всех подзадач определённого эпика.
    public ArrayList<Subtask> receivingSubInEpic(int id){
        Epic ep = epics.get(id);
        return ep.getEpicWithSubtask();
    }
    private void statusManager(Epic epic){
        int newStatus = 0;
        int done = 0;
        ArrayList<Subtask> subInEpic = epic.getEpicWithSubtask();
        if(subInEpic.isEmpty()){
            epic.setStatus("NEW");

        }else{
            for (Subtask subtask : subInEpic) {
                if(subtask.getStatus().equals("NEW")){
                    newStatus ++;
                } else if(subtask.getStatus().equals("DONE")){
                    done ++;
                } else {
                    epic.setStatus("IN_PROGRESS");
                    return;
                }
            }
            if (newStatus == subInEpic.size()){
                epic.setStatus("NEW");

            } else if (done == subInEpic.size()){
                epic.setStatus("DONE");
            } else {
                epic.setStatus("IN_PROGRESS");

            }
        }
    }
}
