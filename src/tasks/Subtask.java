package tasks;

import java.time.Instant;
import java.util.Objects;

public class Subtask extends MainTask {
    private int epicId;
    public Subtask(String taskName, String taskDescription, int id, TaskStatuses status, int epicId, Instant startTime, long duration) {
        super(taskName, taskDescription, id, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String taskName, String taskDescription, int id, TaskStatuses status, int epicId) {
        super(taskName, taskDescription, id, status);
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
