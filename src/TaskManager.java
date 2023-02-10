import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int idTask = 0;

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    //получения списков задач
    public String printTasks(){
        return "Tasks: " + tasks;
    }
    public String printSubtasks(){
        return "Subtasks: " + subtasks;
    }
    public String printEpics(){
        return "Epics: " + epics;
    }

    //удаление задач
    public void delTasks(){
        tasks.clear();
    }
    public void delEpics(){
        epics.clear();
    }
    public void delSubtasks(){
        subtasks.clear();
    }
    //получение через id
    public String receivingTasks(int id){
        //System.out.println(tasks.get(id));
        return "tasks by id: "+tasks.get(id);
    }
    public String receivingEpics(int id){
       // System.out.println(epics.get(id));
        return "Epic by id: "+epics.get(id);
    }
    public String receivingSubtasks(int id){
        //System.out.println(subtasks.get(id));
        return "Subtask id: "+subtasks.get(id);
    }
   // Создание. Сам объект должен передаваться в качестве параметра.
    public void buildTask(Task task){
       task.setId(++idTask);
       if(task.getStatus() ==  null || task.getStatus().equals("")){
           task.setStatus("NEW");
       }
        tasks.put(task.getId(),task);
    }
    public void buildEpic(Epic epic){
        epic.setId(++idTask);
        epic.setStatus("NEW");
        epics.put(epic.getId(), epic);
    }
    public void buildSubtask(Subtask subtask){
        Epic epic = epics.get(subtask.getEpicId());
        subtask.setId(++idTask);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubToEpic(subtask);
        statusManager(epic);
        epics.put(subtask.getEpicId(), epic);
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
    statusManager(ep);
        subtasks.put(subtask.getId(),subtask);
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
    Subtask subtask = subtasks.get(id);
        subtasks.remove(id);
        Epic ep = epics.get(subtask.getEpicId());
        ep.getEpicWithSubtask().remove(subtask);
        statusManager(ep);
    }
    //Получение списка всех подзадач определённого эпика.
    public String receivingSubInEpic(int idd){
        //System.out.println(epic.getEpicWithSubtask());
        Epic ep = epics.get(idd);
        ep.getEpicWithSubtask();
        return "Epic with subtasks id: "+ ep.getEpicWithSubtask();
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
