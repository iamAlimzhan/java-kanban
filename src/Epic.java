import java.util.ArrayList;

public class Epic extends Task{
    public ArrayList<Subtask> epicWithSubtask = new ArrayList<>();

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription);
    }

public void addEpicsId(Subtask subtask){
    epicWithSubtask.add(subtask);
}

    public ArrayList<Subtask> getEpicWithSubtask() {
        return epicWithSubtask;
    }

    public void setEpicWithSubtask(ArrayList<Subtask> epicWithSubtask) {
        this.epicWithSubtask = epicWithSubtask;
    }
}
