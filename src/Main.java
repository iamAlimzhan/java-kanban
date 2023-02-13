public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager manager = new TaskManager();
        Epic epic = new Epic("1", "1.0");
        Subtask subtask = new Subtask("1.1", "1.1", "NEW", 1);
        Subtask subtask2 = new Subtask("1.2", "1.2", "NEW", 1);
        manager.buildEpic(epic);
        manager.buildSubtask(subtask);
        manager.buildSubtask(subtask2);

        Epic epic1 = new Epic("2","2.0");
        Subtask subtask1 = new Subtask("2.1","2,1","NEW", 4);
        manager.buildEpic(epic1);
        manager.buildSubtask(subtask1);
    }
}