package manager;
import tasks.*;
import java.util.List;
public interface HistoryManager {
    List<Task> getHistory();
    void add(Task task);
}
