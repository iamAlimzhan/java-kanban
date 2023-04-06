package tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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
    public TypeOfTask getType() {
        return TypeOfTask.EPIC;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(epicWithSubtask, epic.epicWithSubtask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicWithSubtask);
    }
}
