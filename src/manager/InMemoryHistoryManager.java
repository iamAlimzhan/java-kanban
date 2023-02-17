package manager;
import tasks.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> taskHistory = new ArrayList<>(10);
    @Override
    public List<Task> getHistory(){
        return taskHistory;
    }
    @Override
    public void add(Task task){
    taskHistory.add(task);
    }
}