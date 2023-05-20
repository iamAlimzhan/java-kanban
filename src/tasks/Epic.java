package tasks;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import tasks.Subtask;
import tasks.Task;

public class Epic extends MainTask {
    private List<Subtask> epicWithSubtask = new ArrayList<>();

    public Epic(String taskName, String taskDescription, int id, TaskStatuses status, Instant startTime, Long duration) {
        super(taskName, taskDescription, id, status, TypeOfTask.EPIC,startTime,duration);
    }

    public Epic(String taskName, String taskDescription, int id, TaskStatuses status) {
        super(taskName, taskDescription, id, status);
    }

    public void addSubToEpic(Subtask subtask){
        epicWithSubtask.add(subtask);
        setStatus();
        setEpicTime();
    }

    public List<Subtask> getEpicWithSubtask() {
        return epicWithSubtask;
    }
    public void setEpicWithSubtask(List<Subtask> epicWithSubtask) {
        this.epicWithSubtask = epicWithSubtask;
        setStatus();
        setEpicTime();
    }
    public void addEpicWithSubtask(Subtask subtask){
        epicWithSubtask.add(subtask);
        setStatus();
        setEpicTime();
    }
    public void removeEpicWithSubtask(Subtask subtask){
        epicWithSubtask.remove(subtask);
        setStatus();
        setEpicTime();
    }
    private void setEpicTime() {
        if (epicWithSubtask != null) {
            Instant startTime = Instant.MAX;
            Instant endTime = Instant.MIN;
            for (Subtask subtask : epicWithSubtask) {
                if (subtask != null) { // Добавьте проверку на null
                    if (subtask.getStartTime() != null && subtask.getStartTime().isAfter(startTime)) {
                        startTime = subtask.getStartTime();
                    }
                    if (subtask.getEndTime() != null && subtask.getEndTime().isBefore(endTime)) {
                        endTime = subtask.getEndTime();
                    }
                }
            }
            if (startTime != Instant.MAX) {
                setStartTime(startTime);
            }
            if (endTime != Instant.MIN) {
                setDuration(endTime.toEpochMilli() - startTime.toEpochMilli());
            }
        }
    }



    private void setStatus(){
        int flag = 0;
        for (Subtask subtask : epicWithSubtask) {
            if(subtask.getStatus() == TaskStatuses.DONE){
                flag++;
            } else if (subtask.getStatus() == TaskStatuses.IN_PROGRESS){
                super.setStatus(TaskStatuses.IN_PROGRESS);
                return;
            }
        }
        if(flag == 0){
            super.setStatus(TaskStatuses.NEW);
        } else if(flag == epicWithSubtask.size()){
            super.setStatus(TaskStatuses.DONE);
        } else {
            super.setStatus(TaskStatuses.IN_PROGRESS);
        }
    }

    @Override
    public TypeOfTask getType() {
        return TypeOfTask.EPIC;
    }

    @Override
    public String toString() {
        return "tasks.Epic{" +
                super.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(epicWithSubtask, epic.epicWithSubtask) && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicWithSubtask);
    }
}
