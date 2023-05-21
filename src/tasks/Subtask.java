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

}
