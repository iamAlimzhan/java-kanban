package tasks;

import java.time.Instant;
import java.util.Objects;

import static tasks.TaskStatuses.NEW;

public class Task extends MainTask{
    public Task(String taskName, String taskDescription, int id, TaskStatuses status, Instant startTime, Long duration) {
        super(taskName, taskDescription, id, status, TypeOfTask.TASK, startTime, duration);
    }

    public Task(String taskName, String taskDescription, int id, TaskStatuses status) {
        super(taskName, taskDescription, id, status);
    }
}