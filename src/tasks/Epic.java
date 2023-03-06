package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> epicWithSubtask = new ArrayList<>();

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription);
    }

    public void addSubToEpic(Subtask subtask){
        epicWithSubtask.add(subtask);
    }

    public List<Subtask> getEpicWithSubtask() {
        return epicWithSubtask;
    }
    public void setEpicWithSubtask(List<Subtask> epicWithSubtask) {
        this.epicWithSubtask = epicWithSubtask;
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                "epicTaskName='" + getTaskName() + '\'' +
                ", epicTaskDescription='" + getTaskDescription() + '\'' +
                ", id=" + getId() +
                ", epicStatus='" + getStatus() + '\'' +
                '}';
    }

}
