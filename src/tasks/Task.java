package tasks;

import java.util.Objects;

import static tasks.TaskStatuses.NEW;

public class Task {
    private String taskName;
    private String taskDescription;
    private int id;
    private TaskStatuses status = NEW; //New; In Progress; Done
    //private TypeOfTask typeOfTask = TypeOfTask.TASK;
    public TypeOfTask getType() { return TypeOfTask.TASK; }

    public Task(String taskName, String taskDescription, TaskStatuses status) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
    }
    public Task(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
    }

    public Task(Integer id, String taskName, String taskDescription, TaskStatuses status) {
        this.id = id;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatuses getStatus() {
        return status;
    }

    public void setStatus(TaskStatuses status) {
        this.status = status;
    }

    public TypeOfTask getTypeOfTask() {
        return getType();
    }


    public String toStringTask(){
        return getId() + "," + getTypeOfTask() + "," + getTaskName() + "," + getStatus() + "," + getTaskDescription();
    }

    @Override
    public String toString() {
        return "tasks.Task{" +
                "taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && taskName.equals(task.taskName) && taskDescription.equals(task.taskDescription) && status.equals(task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, taskDescription, id, status);
    }
}