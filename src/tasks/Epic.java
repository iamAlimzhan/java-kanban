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

    public void addSubToEpic(Subtask subtask) throws NullPointerException{
        if (subtask != null) {
            epicWithSubtask.add(subtask);
            setStatus();
            setEpicTime();
        } else {
            throw new NullPointerException("Subtask cannot be null");
        }
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
    public void clearEpicSubtasks() {
        epicWithSubtask.clear();
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

                    if (subtaskStartTime != null && (startTime == null || subtaskStartTime.isBefore(startTime))) {
                        startTime = subtaskStartTime;
                    }
                    if (subtaskEndTime != null && (endTime == null || subtaskEndTime.isAfter(endTime))) {
                        endTime = subtaskEndTime;
                    }

                duration += subtask.getDuration();
            }
            setStartTime(startTime);
            this.endTime = endTime;
            setDuration(duration);
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
    public Instant getEndTime() {
        return endTime;
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
        return Objects.equals(epicWithSubtask, epic.epicWithSubtask) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicWithSubtask, endTime);
    }

}
