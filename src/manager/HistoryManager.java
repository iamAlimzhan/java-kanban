package manager;
import tasks.*;
import java.util.List;
public interface HistoryManager {
    List<MainTask> getHistory();
    void add(MainTask mainTask);
    void remove(int id);

}
