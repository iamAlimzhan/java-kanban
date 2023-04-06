package tasks;

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
}
