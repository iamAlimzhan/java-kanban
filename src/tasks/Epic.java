package tasks;

import java.util.ArrayList;
import java.util.List;



public class Epic extends Task {
    private List<Subtask> epicWithSubtask = new ArrayList<>();

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription);
    }

    public Epic(Integer id, String taskName, String taskDescription, TaskStatuses status) {
        super(id, taskName, taskDescription, status);

    }

    public Epic(String taskName, String taskDescription, List<Subtask> epicWithSubtask) {
        super(taskName, taskDescription);
        this.epicWithSubtask = epicWithSubtask;
    }

    public Epic(Integer id, String taskName, String taskDescription, TaskStatuses status, List<Subtask> epicWithSubtask) {
        super(id, taskName, taskDescription, status);
        this.epicWithSubtask = epicWithSubtask;
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
