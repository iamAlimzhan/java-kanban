import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int idTask = 0;

    HashMap<Integer, Task> taskMap = new HashMap<>();
    HashMap<Integer, Subtask> subMap = new HashMap<>();
    HashMap<Integer, Epic> epicMap = new HashMap<>();

    //получения списков задач
    public void printTasks(){
        System.out.println("tasks "+taskMap);
        System.out.println("epics "+epicMap);
        System.out.println("subtasks "+subMap);
    }
    //удаление задач
    public void delTasks(){
        taskMap.clear();
        subMap.clear();
        epicMap.clear();
    }
    //получение через id
    public void receivingTasks(int id){
        System.out.println(taskMap.get(id));
    }
    public void receivingEpics(int id){
        System.out.println(epicMap.get(id));
    }
    public void receivingSubtasks(int id){
        System.out.println(subMap.get(id));
    }
   // Создание. Сам объект должен передаваться в качестве параметра.
    public void buildTask(Task task){
       task.setId(++idTask);
       taskMap.put(task.getId(),task);
    }
    public void buildEpic(Epic epic){
        epic.setId(++idTask);
        epicMap.put(epic.getId(), epic);
    }
    public void buildSubtask(Subtask subtask){
        Epic epic = epicMap.get(subtask.getEpicId());
        subtask.setId(++idTask);
        subMap.put(subtask.getId(), subtask);
        epic.addEpicsId(subtask);
        epicMap.put(subtask.getEpicId(), epic);
    }
    //обновление
    public void updateTask(Task task){
        taskMap.put(task.getId(), task);
    }
    public void updateEpic(Epic epic){
        epicMap.put(epic.getId(), epic);
    }
    public void updateSubtask(Subtask subtask){
        subMap.put(subtask.getEpicId(),subtask);
    }
    //Удаление по идентификатору.
    public void deleteTask(int id){
        taskMap.remove(id);
    }
    public void deleteEpic(int id){
        epicMap.remove(id);
    }
    public void deleteSubtask(int id){
        subMap.remove(id);
    }
    //Доп Методы
    //Получение списка всех подзадач определённого эпика.
    public void receivingSubInEpic(Epic epic){
        System.out.println(epic.getEpicWithSubtask());
    }
    public void statusManager(Epic epic){
        int newStatus = 0;
        int done = 0;
        ArrayList<Subtask> subInEpic = epic.getEpicWithSubtask();
        if(subInEpic.isEmpty()){
            epic.setStatus("NEW");
            epicMap.put(epic.getId(), epic);
        }else{
            for (Subtask subtask : subInEpic) {
                if(subtask.getStatus().equals("NEW")){
                    newStatus ++;
                } else if(subtask.getStatus().equals("DONE")){
                    done ++;
                }
            }
            if (newStatus == subInEpic.size()){
                epic.setStatus("NEW");
                epicMap.put(epic.getId(), epic);
            } else if (done == subInEpic.size()){
                epic.setStatus("DONE");
                epicMap.put(epic.getId(), epic);
            } else {
                epic.setStatus("IN_PROGRESS");
                epicMap.put(epic.getId(), epic);
            }
        }
    }
}
