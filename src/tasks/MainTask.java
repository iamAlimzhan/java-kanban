package tasks;

import java.time.Instant;
import java.util.Objects;

import static tasks.TaskStatuses.NEW;

public abstract class MainTask {
    private String taskName;
    private String taskDescription;
    private int id;
    private TaskStatuses status = NEW; //New; In Progress; Done
    private long duration;
    private Instant startTime;

    public MainTask(String taskName, String taskDescription, int id, TaskStatuses status, Instant startTime, long duration) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }
    public MainTask(String taskName, String taskDescription, int id, TaskStatuses status){
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.id = id;
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
    public abstract TypeOfTask getType();

    public void setStatus(TaskStatuses status) {
        this.status = status;
    }

    public TypeOfTask getTypeOfTask() {
        return getType();
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }
    public Instant getEndTime() {
        if(startTime == null){
            return null;
        }
        return startTime.plusMillis(duration);
    }

    public String toString() {
        return "tasks.Task{" +
                "taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", id=" + id +
                ", status='" + status + '\'' +
                '}';
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MainTask mainTask = (MainTask) o;
        return id == mainTask.id && duration == mainTask.duration && Objects.equals(taskName, mainTask.taskName)
                && Objects.equals(taskDescription, mainTask.taskDescription) && status == mainTask.status
                && Objects.equals(startTime, mainTask.startTime);
    }

    public int hashCode() {
        return Objects.hash(taskName, taskDescription, id, status, duration, startTime);
    }
}