public class Subtask extends Task{
    private int epicId;

    public Subtask(String taskName, String taskDescription, String status, int epicId) {
        super(taskName, taskDescription, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
