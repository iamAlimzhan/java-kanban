import java.util.ArrayList;
public class Epic extends Task{
    private ArrayList<Subtask> epicWithSubtask = new ArrayList<>();//не понял как сделать так чтобы хранить не ссылки а а идентификаторы подзадач эпика(

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription);
    }

    public void addSubToEpic(Subtask subtask){
        epicWithSubtask.add(subtask);
    }

    public ArrayList<Subtask> getEpicWithSubtask() {
        return epicWithSubtask;
    }
    public void setEpicWithSubtask(ArrayList<Subtask> epicWithSubtask) {
        this.epicWithSubtask = epicWithSubtask;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "epicTaskName='" + getTaskName() + '\'' +
                ", epicTaskDescription='" + getTaskDescription() + '\'' +
                ", id=" + getId() +
                ", epicStatus='" + getStatus() + '\'' +
                '}';
    }

}
