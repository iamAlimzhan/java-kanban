package tasks;

import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String taskName, String taskDescription, TaskStatuses status, int epicId) {
        super(taskName, taskDescription, status);
        this.epicId = epicId;
    }
    public Subtask(Integer id, String taskName, String taskDescription, TaskStatuses status, int epicId) {
        super(id, taskName, taskDescription, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TypeOfTask getType() {
        return TypeOfTask.SUBTASK;
    }

    @Override
    public String toStringTask() {
        return getId() + "," + getTypeOfTask() + "," + getTaskName() + "," + getStatus() + "," + getTaskDescription() + "," + getEpicId();
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "subtaskTaskName='" + getTaskName() + '\'' +
                ", subtaskTaskDescription='" + getTaskDescription() + '\'' +
                ", id=" + getId() +
                ", subtaskStatus='" + getStatus() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
