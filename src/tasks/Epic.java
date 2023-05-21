package tasks;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import tasks.Subtask;
import tasks.Task;

public class Epic extends MainTask {
    private List<Subtask> epicWithSubtask = new ArrayList<>();
    private Instant endTime;
    public Epic(String taskName, String taskDescription, int id, TaskStatuses status, Instant startTime, long duration) {
        super(taskName, taskDescription, id, status,startTime,duration);
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
    public void removeEpicWithSubtask(Subtask subtask){
        epicWithSubtask.remove(subtask);
        setStatus();
        setEpicTime();
    }
    private void setEpicTime() {
        if (epicWithSubtask != null) {
            Instant startTime = null;
            Instant endTime = null;
            long duration = 0;

            for (Subtask subtask : epicWithSubtask) {
                    Instant subtaskStartTime = subtask.getStartTime();
                    Instant subtaskEndTime = subtask.getEndTime();

                    if (subtaskStartTime != null && (startTime == null || subtaskStartTime.isAfter(startTime))) {
                        startTime = subtaskStartTime;
                    }
                    if (subtaskEndTime != null && (endTime == null || subtaskEndTime.isBefore(endTime))) {
                        endTime = subtaskEndTime;
                    }

                    duration += subtask.getDuration() != null ? subtask.getDuration() : 0;
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
